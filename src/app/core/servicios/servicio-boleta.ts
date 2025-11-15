// src/app/services/boleta.service.ts
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { environment } from '../../../environment/environment';

// Modelos
import {
  BoletaRequest,       
  BoletaResponse,     
  Boleta,             
  DetalleBoletaDTO, 
  BoletaDetailsResponse
} from '../models/boleta.models';

@Injectable({
  providedIn: 'root'
})
export class ServicioBoleta {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/boletas`;

  /**
  * POST /api/boletas
  * Crear boleta a partir de un BoletaRequest.
  */
  crearBoleta(request: BoletaRequest): Observable<BoletaResponse> {
    console.log('Creando boleta con', request.cartItems.length, 'items');
    return this.http.post<BoletaResponse>(this.apiUrl, request).pipe(
      tap((response) => console.log('Boleta creada', response)),
      catchError(this.handleError)
    );
  }

  /**
   * OBTENER MIS BOLETAS
   * GET /api/boletas
   * Requiere autenticación
   */
  obtenerMisBoletas(): Observable<Boleta[]> {
    console.log('Obteniendo mis boletas...');
    return this.http.get<Boleta[]>(this.apiUrl).pipe(
      tap((boletas) => {
        console.log(`${boletas.length} boletas obtenidas`);
      }),
      catchError(this.handleError)
    );
  }

  /**
   * OBTENER MIS BOLETAS ORDENADAS
   * GET /api/boletas/ordenadas
   */
  obtenerMisBoletasOrdenadas(): Observable<Boleta[]> {
    console.log('Obteniendo boletas ordenadas...');
    return this.http.get<Boleta[]>(`${this.apiUrl}/ordenadas`).pipe(
      tap((boletas) => {
        console.log(`${boletas.length} boletas obtenidas`);
      }),
      catchError(this.handleError)
    );
  }

  /**
   * OBTENER BOLETA POR ID
   * GET /api/boletas/{id}
   * Obtener una boleta específica por ID (incluye detalles).
   */
  obtenerBoletaPorId(idBoleta: number): Observable<BoletaDetailsResponse> {
    console.log('Obteniendo boleta ID:', idBoleta);
    return this.http.get<BoletaDetailsResponse>(`${this.apiUrl}/${idBoleta}`).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * OBTENER DETALLES DE BOLETA
   * GET /api/boletas/{id}/detalles
   * Obtener solo los detalles de una boleta específica.
   */
  obtenerDetallesBoleta(idBoleta: number): Observable<DetalleBoletaDTO[]> {
    console.log('Obteniendo detalles de boleta ID:', idBoleta);
    return this.http.get<DetalleBoletaDTO[]>(`${this.apiUrl}/${idBoleta}/detalles`).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * FORMATEAR PRECIO
   */
  formatearPrecio(precio: number): string {
    return new Intl.NumberFormat('es-PE', {
      style: 'currency',
      currency: 'PEN'
    }).format(precio);
  }

  /**
   * FORMATEAR FECHA (DD/MM/YYYY HH AM/PM)
   */
  formatearFecha(fecha: string): string {
    const dateString = fecha.replace(' ', 'T').substring(0, 19);
    const date = new Date(dateString);

    if (isNaN(date.getTime())) {
      console.warn('Fecha inválida:', fecha);
      return 'Fecha inválida';
    }

    // Formatear solo fecha (DD/MM/YYYY)
    const fechaParte = date.toLocaleDateString('es-PE', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
    }).replace(/\//g, '/');

    // Formatear solo hora con AM/PM
    const horaParte = date.toLocaleTimeString('es-PE', {
      hour: '2-digit',
      hour12: true, //Mostrar AM/PM
    });

    // Quitar minutos/segundos del formato de hora
    const match = horaParte.match(/(\d+)\s*([ap]\.\s*m\.)/i);

    if (match) {
      const horaSinMinutos = `${match[1]} ${match[2]}`.toUpperCase().replace(/\./g, '');
      return `${fechaParte} ${horaSinMinutos}`;
    }

    // Fallback
    return `${fechaParte} ${horaParte.split(':')[0]} ${horaParte.split(' ')[1]}`;
  }

  /**
   * Manejo de errores
   */
  private handleError(error: any): Observable<never> {
    console.error('❌ Error en BoletaService:', error);

    let errorMessage = 'Ocurrió un error al procesar la solicitud';

    if (error.error?.mensaje) {
      errorMessage = error.error.mensaje;
    } else if (error.error?.message) {
      errorMessage = error.error.message;
    } else if (error.message) {
      errorMessage = error.message;
    }

    // Errores específicos por código HTTP
    if (error.status === 401) {
      errorMessage = 'No tienes autorización. Por favor, inicia sesión nuevamente';
    } else if (error.status === 404) {
      errorMessage = 'Recurso no encontrado';
    } else if (error.status === 500) {
      errorMessage = 'Error del servidor. Intenta nuevamente más tarde';
    }

    return throwError(() => new Error(errorMessage));
  }
}
