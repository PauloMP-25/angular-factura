import { Routes } from '@angular/router';
// IMPORTACIONES
import { Dashboard } from './dashboard';
import { Tabla } from '../tabla/tabla';
import {Formulario} from '../formulario/formulario';

export const DASHBOARD_USUARIO_ROUTES: Routes = [
    {
        path: '',  // Ruta base para el lazy-load
        component: Dashboard,  // Carga el componente padre con side-nav y outlet
        children: [
            // Rutas hijas (coinciden con routerLink en side-nav)
            { path: 'tabla-boletas', component: Tabla }, 
            { path: 'formulario-boletas', component: Formulario },
            // Opcional: Ruta 404 para sub-rutas inválidas
            { path: '**', redirectTo: 'index' }
        ]
    }
];