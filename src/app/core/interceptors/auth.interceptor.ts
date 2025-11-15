// src/app/interceptors/auth.interceptor.ts
import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

const URL_EXCLUIDA = 'https://apiperu.dev';
/**
 * Interceptor HTTP que agrega automáticamente el token JWT  a todas las peticiones HTTP
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
    if (req.url.includes(URL_EXCLUIDA)) {
        console.log(`⚠️ Interceptor omitido para URL externa: ${req.url}`);
        return next(req);
    }

    const router = inject(Router);

    // Obtener token del localStorage
    const token = localStorage.getItem('authToken');

    // Clonar la request y agregar el header Authorization si existe token
    if (token) {
        req = req.clone({
            setHeaders: {
                Authorization: `Bearer ${token}`
            }
        });

        console.log('Token agregado a la petición:', req.url);
    }

    // Continuar con la petición y manejar errores
    return next(req).pipe(
        catchError((error) => {
            console.error('❌ Error en petición HTTP:', error);

            // Si es error 401 (No autorizado), redirigir al login
            if (error.status === 401) {
                console.warn('Token inválido o expirado, redirigiendo a login...');

                // Limpiar sesión
                localStorage.removeItem('authToken');
                localStorage.removeItem('currentUser');

                // Redirigir al login
                router.navigate(['/autenticacion']);
            }

            return throwError(() => error);
        })
    );
};