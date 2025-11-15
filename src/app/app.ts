import { Component, signal, HostListener, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Header } from './shared/header/header';
import { Footer } from "./shared/footer/footer";
import { AuthService } from './core/servicios/servicio-auth';
@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Header, Footer],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected readonly title = signal('frontend');
  private authService = inject(AuthService);

  /**
   * HostListener que escucha el evento 'beforeunload' del objeto Window.
   * Este evento se dispara justo antes de que el documento se descargue (cierre de pestaña/navegador, recarga).
   */
  @HostListener('window:beforeunload', ['$event'])
  unloadHandler(event: Event) {
    console.log('Detectado cierre/recarga de página. Eliminando token...');
    this.authService.logout();
  }
}
