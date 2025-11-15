//MODELOS DEL BACKEND Y FRONTEND

/**
 * Respuesta de autenticación del backend
 */
export interface AuthResponse {
    token: string;
    idUsuario: number;
    email: string;
    nombreCompleto: string;
    documento: string;
    mensaje: string;
    success: boolean;
}

/**
 * Datos del usuario autenticado
 */
export interface UserData {
    idUsuario: number;
    email: string;
    nombreCompleto: string;
    documento: string;
}

/**
 * Request para registro
 */
export interface RegistroRequest {
    email: string;
    password: string;
    nombres: string;
    apellidos: string;
    numeroDocumento: string;
}

/**
 * Credenciales de login
 */
export interface LoginCredentials {
    email: string;
    password: string;
}