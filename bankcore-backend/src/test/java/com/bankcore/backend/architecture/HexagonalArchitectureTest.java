package com.bankcore.backend.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

/**
 * Suite de Fitness Functions Arquitectónicas con ArchUnit.
 * Ejecuta gobernanza automatizada garantizando que la Arquitectura Hexagonal,
 * los principios SOLID y las restricciones de seguridad no se degraden en el tiempo.
 */
@DisplayName("Gobernanza Arquitectónica: Fitness Functions con ArchUnit")
class HexagonalArchitectureTest {

    private static JavaClasses classes;

    @BeforeAll
    static void setup() {
        classes = new ClassFileImporter().importPackages("com.bankcore.backend");
    }

    @Test
    @DisplayName("REGLA 1: El Dominio debe ser puro Java (cero dependencias de Spring, JPA o Infraestructura)")
    void elDominioDebeSerPuroJava() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage(
                        "..org.springframework..",
                        "..jakarta.persistence..",
                        "..com.bankcore.backend.infrastructure..",
                        "..com.bankcore.backend.dto.."
                );

        rule.check(classes);
    }

    @Test
    @DisplayName("REGLA 2: Los Servicios de Aplicación nunca deben depender de adaptadores concretos de persistencia (DIP)")
    void serviciosNoDebenDependerDeAdaptadoresConcretos() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..service..")
                .should().dependOnClassesThat()
                .resideInAPackage("..infrastructure.persistence.adapter..");

        rule.check(classes);
    }

    @Test
    @DisplayName("REGLA 3: Los Controladores REST deben residir exclusivamente en infrastructure.rest.controller")
    void controladoresRestDebenResidirEnPaqueteCorrecto() {
        ArchRule rule = classes()
                .that().areAnnotatedWith(RestController.class)
                .should().resideInAPackage("..infrastructure.rest.controller..");

        rule.check(classes);
    }

    @Test
    @DisplayName("REGLA 4: Los Puertos de salida en domain.port deben ser interfaces con sufijo 'Port'")
    void puertosDebenSerInterfacesConSufijoPort() {
        ArchRule rule = classes()
                .that().resideInAPackage("..domain.port..")
                .should().beInterfaces()
                .andShould().haveSimpleNameEndingWith("Port");

        rule.check(classes);
    }

    @Test
    @DisplayName("REGLA 5: Se prohíbe la inyección por campo con @Autowired en código de producción (Obligatorio Constructor)")
    void prohibirInyeccionPorCampoConAutowired() {
        ArchRule rule = noFields()
                .that().areDeclaredInClassesThat()
                .resideInAnyPackage("..service..", "..infrastructure..")
                .should().beAnnotatedWith("org.springframework.beans.factory.annotation.Autowired")
                .because("Se debe aplicar Dependency Inversion e inyección por constructor con Records o Lombok en producción");

        rule.check(classes);
    }
}
