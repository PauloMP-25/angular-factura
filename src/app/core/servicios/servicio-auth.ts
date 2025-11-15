// src/app/services/auth.service.ts
import { Injectable, inject, signal } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, BehaviorSubject, throwError } from 'rxjs';
import { tap, catchError, map } from 'rxjs/operators';
import { environment } from '../../../environment/environment';

// Modelos
import {
  AuthResponse, UserData,
  RegistroRequest,
  LoginCredentials
} from '../models/autenticacion.models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);

  private apiUrl = `${environment.apiUrl}/api/usuarios`;

  // Estado del usuario actual
  private currentUserSubject = new BehaviorSubject<UserData | null>(this.getUserFromStorage());
  public currentUser$ = this.currentUserSubject.asObservable();

  // Signal para componentes (opcional)
  public currentUserSignal = signal<UserData | null>(this.getUserFromStorage());

  // URL de redirección
  private redirectUrl: string | null = null;

  constructor() {
    // Verificar token al iniciar
    this.verifyTokenOnInit();
  }

  /**
   * Verificar token guardado al iniciar la aplicación
   */
  private verifyTokenOnInit(): void {
    const token = this.getToken();
    if (token) {
      this.verificarToken().subscribe({
        next: () => console.log('Token válido'),
        error: () => {
          console.warn('Token inválido, limpiando sesión');
          this.cerrarSession();
        }
      });
    }
  }

  /**
   * Obtener usuario desde localStorage
   */
  private getUserFromStorage(): UserData | null {
    const userJson = localStorage.getItem('currentUser');
    return userJson ? JSON.parse(userJson) : null;
  }

  /**
   * Obtener token desde localStorage
   */
  getToken(): string | null {
    return localStorage.getItem('authToken');
  }

  /**
   * Verificar si el usuario está autenticado
   */
  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  /**
   * Obtener usuario actual
   */
  getCurrentUser(): UserData | null {
    return this.currentUserSubject.value;
  }

  /**
   * REGISTRO
   * POST /api/usuarios/registro
   */
  register(registerData: RegistroRequest): Observable<AuthResponse> {
    console.log('Registrando usuario...');

    return this.http.post<AuthResponse>(`${this.apiUrl}/registro`, registerData).pipe(
      tap((response) => {
        console.log('Usuario registrado:', response);
        this.saveAuthData(response);
      }),
      catchError(this.handleError)
    );
  }

  /**
   * LOGIN
   * POST /api/usuarios/login
   */
  login(credentials: LoginCredentials): Observable<AuthResponse> {
    console.log('Iniciando sesión...');

    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap((response) => {
        console.log('Login exitoso:', response);
        this.saveAuthData(response);
      }),
      catchError(this.handleError)
    );
  }

  /**
   * LOGOUT
   */
  logout(): void {
    console.log('Cerrando sesión...');
    this.cerrarSession();
    this.router.navigate(['/inicio']);
  }

  /**
   * VERIFICAR TOKEN
   * POST /api/usuarios/verificar-token
   */
  verificarToken(): Observable<AuthResponse> {
    const token = this.getToken();

    if (!token) {
      return throwError(() => new Error('No hay token'));
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.http.post<AuthResponse>(`${this.apiUrl}/verificar-token`, {}, { headers }).pipe(
      tap((response) => {
        console.log('Token válido');
        this.updateUserData(response);
      }),
      catchError(this.handleError)
    );
  }

  /**
   * REFRESCAR TOKEN
   * POST /api/usuarios/refrescar-token
   */
  refrescarToken(): Observable<AuthResponse> {
    const token = this.getToken();

    if (!token) {
      return throwError(() => new Error('No hay token'));
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.http.post<AuthResponse>(`${this.apiUrl}/refrescar-token`, {}, { headers }).pipe(
      tap((response) => {
        console.log('🔄 Token refrescado');
        this.saveAuthData(response);
      }),
      catchError(this.handleError)
    );
  }

  /**
  * Verificar si un email está disponible para el registro
  * GET /api/usuarios/verificar-email/{email}
  * @param email Email a verificar
  */
  verificarEmailDisponible(email: string): Observable<boolean> {
    const url = `${environment.apiUrl}/api/usuarios/verificar-email/${email}`;
    // Usamos .pipe(map()) para transformar la respuesta a un booleano (true si está disponible)
    return this.http.get<{ disponible: boolean }>(url).pipe(
      map(response => response.disponible),
      catchError(error => {
        // Si hay un error (ej. 409 Conflict), significa que NO está disponible.
        console.warn('Email no disponible:', error);
        return throwError(() => new Error('El email ya se encuentra registrado'));
      })
    );
  }

  /**
   * Guardar datos de autenticación
   */
  private saveAuthData(response: AuthResponse): void {
    // Guardar token
    localStorage.setItem('authToken', response.token);

    // Crear y guardar usuario
    const userData: UserData = {
      idUsuario: response.idUsuario,
      email: response.email,
      nombreCompleto: response.nombreCompleto,
      documento: response.documento
    };

    localStorage.setItem('currentUser', JSON.stringify(userData));

    // Actualizar observables
    this.currentUserSubject.next(userData);
    this.currentUserSignal.set(userData);

    console.log('Datos guardados en localStorage');
  }

  /**
   * Actualizar datos del usuario (sin cambiar token)
   */
  private updateUserData(response: AuthResponse): void {
    const userData: UserData = {
      idUsuario: response.idUsuario,
      email: response.email,
      nombreCompleto: response.nombreCompleto,
      documento: response.documento
    };

    localStorage.setItem('currentUser', JSON.stringify(userData));
    this.currentUserSubject.next(userData);
    this.currentUserSignal.set(userData);
  }


  /**
   * Limpiar sesión
   */
  private cerrarSession(): void {
    localStorage.removeItem('authToken');
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
    this.currentUserSignal.set(null);
    console.log('Sesión limpiada');
  }

  public limpiarSesionManual(): void {
    this.cerrarSession();
  }

  /**
   * Manejo de errores
   */
  private handleError(error: any): Observable<never> {
    console.error('❌ Error en AuthService:', error);

    let errorMessage = 'Ocurrió un error';

    if (error.error?.mensaje) {
      errorMessage = error.error.mensaje;
    } else if (error.error?.message) {
      errorMessage = error.error.message;
    } else if (error.message) {
      errorMessage = error.message;
    }

    return throwError(() => new Error(errorMessage));
  }

  /**
   * Establecer URL de redirección
   */
  setRedirectUrl(url: string): void {
    this.redirectUrl = url;
  }

  /**
   * Obtener y limpiar URL de redirección
   */
  getAndClearRedirectUrl(): string | null {
    const url = this.redirectUrl;
    this.redirectUrl = null;
    return url;
  }
}