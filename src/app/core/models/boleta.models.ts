// src/app/models/boleta.models.ts (Versión Consolidada)

/**
 * Request para crear detalle de boleta simula un item de carrito
 */
export interface DetalleRequest {
    nombreProducto: string;
    precioUnitario: number;
    cantidad: number;
}

/**
 * Request para crear boleta (checkout)
 * Usa la estructura del backend
 */
export interface BoletaRequest {
    idUsuario?: number;
    cartItems: DetalleRequest[]; // Lista de detalles/items
    total: number;
    subtotal: number;

    nombreCliente: string;
    documentoCliente: string;
    emailCliente: string;
}

/**
 * Respuesta genérica al crear boleta
 */
export interface BoletaResponse {
    success: boolean;
    mensaje: string;
    boletaId: number;
}

/**
 * Sub-DTO para los datos del usuario (si se incluyen)
 */
export interface UsuarioDTO {
    id: number;
    nombres: string;
    apellidos: string;
    numero_documento: string;
    email: string;
}

/**
 * Sub-DTO para DetalleBoleta (Respuesta)
 */
export interface DetalleBoletaDTO {
    idDetalle: number;
    producto: string;
    precio_unitario: number;
    cantidad: number;
    subtotal: number;
}

/**
 * Boleta completa con detalles (la estructura del backend)
 */
export interface BoletaDetailsResponse {
    idBoleta: number;
    idUsuario: number;
    fecha_creacion: string; // "2025-10-16 22:34:34.39801"
    total: number;
    detalles: DetalleBoletaDTO[];
    nombreCliente: string;
    documentoCliente: string;
    emailCliente: string;

    usuarioVendedor?: UsuarioDTO;
}

// Alias para la lista de boletas en la tabla
export type Boleta = BoletaDetailsResponse;