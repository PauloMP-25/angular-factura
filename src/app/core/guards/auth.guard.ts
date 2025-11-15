// src/app/guards/auth.guard.ts
import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../servicios/servicio-auth';

/**
 * Guard que protege rutas que requieren autenticación
 */
export const authGuard: CanActivateFn = (route, state) => {
    const authService = inject(AuthService);
    const router = inject(Router);

    if (authService.isAuthenticated()) {
        console.log('Usuario autenticado, acceso permitido');
        return true;
    }

    console.warn('Usuario no autenticado, redirigiendo a login');

    // Guardar URL a la que intentaba acceder
    authService.setRedirectUrl(state.url);

    // Redirigir al login
    router.navigate(['/login']);
    return false;
};

/**
 * Guard inverso: redirige si YA está autenticado
 * Útil para páginas de login/registro
 */
export const noAuthGuard: CanActivateFn = () => {
    const authService = inject(AuthService);
    const router = inject(Router);

    if (!authService.isAuthenticated()) {
        return true;
    }

    console.log('ℹUsuario ya autenticado, redirigiendo a home');
    router.navigate(['/panel']);
    return false;
};