import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../core/servicios/servicio-auth';

@Component({
  selector: 'app-side-nav',
  imports: [RouterModule],
  templateUrl: './side-nav.html',
  styleUrl: './side-nav.scss'
})
export class SideNav {
    constructor(
      private router: Router,
      private authService: AuthService
    ) { }

  cerrarSesion() {
    alert('Sesión cerrada');
    this.authService.logout();
    this.router.navigate(['/inicio']);
  }
}
