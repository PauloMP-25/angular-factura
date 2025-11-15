import { Routes } from '@angular/router';

export const routes: Routes = [
    { path: '', redirectTo: 'inicio', pathMatch: 'full' },
    { path: 'inicio', loadComponent: () => import('./features/inicio/inicio').then(m => m.Inicio) },
    { path: 'registro', loadComponent: () => import('./features/registro/registro').then(m => m.Registro) },
    { path: 'login', loadComponent: () => import('./features/login/login').then(m => m.Login) },
    { path: 'autenticacion', loadComponent: () => import('./features/auth-page/auth-page').then(m => m.AuthPage) },
    { path: 'panel', loadComponent: () => import('./features/dashboard/dashboard').then(m => m.Dashboard)},
    {
        path: 'panel',
        loadChildren: () => import('./features/dashboard/dashboard.routes').then(m => m.DASHBOARD_USUARIO_ROUTES),
    }
];
