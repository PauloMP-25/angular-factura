// src/app/pages/auth/auth-page.component.ts (Nuevo Archivo)
import { Component, inject, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Registro } from '../registro/registro';
import { Login } from '../login/login';
import { Router } from '@angular/router';

@Component({
  selector: 'app-auth-page',
  standalone: true,
  imports: [CommonModule, FormsModule, Registro, Login],
  templateUrl: './auth-page.html',
  styleUrls: ['./auth-page.scss']
})
export class AuthPage {
  @ViewChild(Registro) registroComponent!: Registro;
  @ViewChild(Login) loginComponent!: Login;

  private router = inject(Router);

  isRightPanelActive = false;
  // Solo se mantiene la lógica del slider y los mensajes generales (opcional)
  errorMessage = '';
  successMessage = '';

  showSignUp() {
    this.isRightPanelActive = true;
    this.limpiarMensajes();
    if (this.loginComponent) {
      this.loginComponent.limpiarFormularioLogin();
    }
  }

  showSignIn() {
    this.isRightPanelActive = false;
    this.limpiarMensajes();
    if (this.registroComponent) {
      this.registroComponent.limpiarFormularioRegistro();
    }
  }

  limpiarMensajes() {
    this.errorMessage = '';
    this.successMessage = '';
  }
}