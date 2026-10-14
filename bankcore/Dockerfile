# ====================================================================
# STAGE 1: Compilación de producción optimizada (Build Stage)
# ====================================================================
FROM node:20-alpine AS builder

WORKDIR /app

# Copia de manifiestos y dependencias para aprovechar el caché de Docker
COPY package*.json ./
RUN npm ci

# Copia del código fuente y compilación AOT de Angular
COPY . .
RUN npm run build

# ====================================================================
# STAGE 2: Servidor Web Ultraligero Nginx (Runtime Stage)
# ====================================================================
FROM nginx:1.27-alpine

# Eliminar configuración por defecto de Nginx
RUN rm -rf /etc/nginx/conf.d/default.conf

# Copiar configuración endurecida con CSP y cabeceras bancarias
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copiar bundle optimizado generado por Angular
COPY --from=builder /app/dist/bankcore/browser /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
