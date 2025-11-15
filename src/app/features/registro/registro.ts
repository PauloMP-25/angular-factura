// src/app/pages/registro/registro.component.ts
import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormsModule,
  FormBuilder,
  Validators,
  AbstractControl,
  AsyncValidatorFn,
  ValidationErrors,
  ReactiveFormsModule
} from '@angular/forms';
import { debounceTime, switchMap, map, catchError, of, Observable } from 'rxjs';
import { Router } from '@angular/router';
import { Reniec } from '../../core/servicios/servicio-reniec';
import { AuthService } from '../../core/servicios/servicio-auth';
import { RegistroRequest } from '../../core/models/autenticacion.models';

@Component({
  selector: 'app-registro',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './registro.html',
  styleUrls: ['./registro.scss']
})
export class Registro {
  private reniec = inject(Reniec);
  private authService = inject(AuthService);
  private router = inject(Router);
  private fb = inject(FormBuilder);

  cargando = false;
  cargandoRegistro = false;
  passwordVisible: boolean = false;
  errorMessage = '';
  successMessage = '';

  // Definición del formulario de registro: Se utilizará para el correo y la contraseña
  registroForm = this.fb.group({
    dni: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(8)]],
    nombres: [{ value: '', disabled: true }],
    apellidoPaterno: [{ value: '', disabled: true }],
    apellidoMaterno: [{ value: '', disabled: true }],
    correo: ['', {
      validators: [Validators.required, Validators.email],
      updateOn: 'blur'
    }],
    password: ['', [Validators.required, Validators.minLength(6)]]
  });



  limpiarMensajes() {
    this.errorMessage = '';
    this.successMessage = '';
  }

  /**
   * Buscar datos por DNI usando RENIEC
   */
  buscarDni() {
    const dniControl = this.registroForm.get('dni');
    const dniValue = dniControl?.value;

    this.limpiarMensajes();

    if (dniControl?.invalid || dniValue?.length !== 8) {
      this.errorMessage = 'Ingrese un DNI válido de 8 dígitos';
      dniControl?.markAsTouched();
      return;
    }

    this.cargando = true;
    this.registroForm.disable();

    this.reniec.obtenerDatosPorDni(dniValue!).subscribe({
      next: (response: any) => {
        if (response && response.data) {
          // Actualiza los valores del FormGroup
          this.registroForm.patchValue({
            nombres: response.data.nombres || '',
            apellidoPaterno: response.data.apellido_paterno || '',
            apellidoMaterno: response.data.apellido_materno || '',
          });
          this.successMessage = 'Datos encontrados';
        } else {
          this.registroForm.patchValue({ nombres: '', apellidoPaterno: '', apellidoMaterno: '' });
          this.errorMessage = 'No se encontraron datos para este DNI';
        }
        this.registroForm.enable();
        this.cargando = false
      },
      error: (err) => {
        console.error('Error en API RENIEC:', err);
        this.errorMessage = 'No se encontró información del DNI';
        this.registroForm.enable();
        this.cargando = false;
      }
    });
  }
  /**
  * REGISTRAR usuario en el backend
  */
  registrar() {
    this.limpiarMensajes();


    // Validar autocompletado (solo si el DNI es válido y el campo nombres está vacío)
    if (this.registroForm.get('dni')!.valid && !this.registroForm.value.nombres) {
      this.errorMessage = 'Por favor busque primero los datos del DNI';
      return;
    }

    this.cargandoRegistro = true;
    this.registroForm.disable();

    // Crear objeto de registro
    const registroData: RegistroRequest = {
        email: this.registroForm.value.correo!,
        password: this.registroForm.value.password!,
        nombres: this.registroForm.value.nombres!,
        apellidos: `${this.registroForm.value.apellidoPaterno} ${this.registroForm.value.apellidoMaterno}`.trim(),
        numeroDocumento: this.registroForm.value.dni!
    };

    // Llamar al servicio de autenticación
    this.authService.register(registroData).subscribe({
      next: (response) => {
        console.log('Usuario registrado exitosamente:', response);
        this.successMessage = 'Usuario registrado exitosamente';
        this.limpiarFormularioRegistro();

        setTimeout(() => {
          this.router.navigate(['/panel']);
        }, 1500);
      },
      error: (error) => {
        // ERROR: Mostrar mensaje y RE-HABILITAR el formulario
        console.error('HUBO UN ERROR:', error);
        this.errorMessage = error.message || 'Error al registrar usuario';
        this.cargandoRegistro = false;
        this.registroForm.enable();
      }
    });
  }

  /**
 * Limpiar formulario de registro (SIMPLIFICADO)
 */
  public limpiarFormularioRegistro() {
    if (this.registroForm.disabled) {
      this.registroForm.enable();
    }
    this.registroForm.reset();
    this.cargandoRegistro = false;
    this.limpiarMensajes();
  }

  //Alternar la visibilidad de la contraseña
  togglePasswordVisibility(): void {
    this.passwordVisible = !this.passwordVisible;
  }
}