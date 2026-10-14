import { TarjetaBancaria, TarjetaDto } from '../models/tarjeta.model';

/**
 * Adaptador Frontend para Tarjetas Bancarias (Patrón Adapter GoF).
 * Traduce el TarjetaDto de Spring Boot al modelo visual TarjetaBancaria con enmascaramiento PCI-DSS.
 */
export class TarjetaAdapter {
  static adaptar(dto: TarjetaDto, titular: string = 'CLIENTE BANCORE'): TarjetaBancaria {
    return {
      id: String(dto.id),
      numeroEnmascarado: dto.numeroTarjeta,
      titular: titular,
      franquicia: dto.franquicia,
      cupoTotal: Number(dto.cupoTotal),
      cupoDisponible: Number(dto.cupoDisponible),
      estado: dto.estado === 'ACTIVA' ? 'ACTIVA' : 'BLOQUEADA'
    };
  }
}
