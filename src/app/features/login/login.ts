// src/app/pages/login/login.component.ts
import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms'; // 👈 FormBuilder
import { Router } from '@angular/router';
import { AuthService } from '../../core/servicios/servicio-auth';
import { LoginCredentials } from '../../core/models/autenticacion.models';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.scss']
})
export class Login {
  private authService = inject(AuthService);
  private router = inject(Router);
  private fb = inject(FormBuilder);

  cargandoLogin = false;
  errorMessage = '';
  successMessage = '';

  loginForm = this.fb.group({
    usuarioLogin: ['', [Validators.required, Validators.email]],
    passwordLogin: ['', [Validators.required, Validators.minLength(6)]]
  });

  limpiarMensajes() {
    this.errorMessage = '';
    this.successMessage = '';
  }

  /**
   * INICIAR SESIÓN
   */
  iniciarSesion() {
    this.limpiarMensajes();
    this.loginForm.markAllAsTouched(); // Forzar visualización de errores

    if (this.loginForm.invalid) {
        this.errorMessage = 'Debe ingresar su correo y contraseña.';
        return;
    }
    this.cargandoLogin = true;
    this.loginForm.disable(); // Deshabilitar el formulario

    const loginData: LoginCredentials = {
        email: this.loginForm.value.usuarioLogin!,
        password: this.loginForm.value.passwordLogin!
    };

    // Llamar al servicio de autenticación
    this.authService.login(loginData).subscribe({
      next: (response) => {
        console.log('Login exitoso:', response);
        this.successMessage = 'Login exitoso';

        this.limpiarFormularioLogin();

        setTimeout(() => {
          const redirectUrl = this.authService.getAndClearRedirectUrl();
          this.router.navigate([redirectUrl || '/panel']);
        }, 1000);
      },
      error: (error) => {
        console.error('❌ Error en login:', error);
        this.errorMessage = error.message || 'Credenciales inválidas';
        this.cargandoLogin = false;
        this.loginForm.enable();
      }
    });
  }

  /**
   * Limpiar formulario de login
   */
  public limpiarFormularioLogin() {
    this.loginForm.reset();
    this.cargandoLogin = false;
    this.loginForm.enable();
    this.limpiarMensajes();
  }
}
