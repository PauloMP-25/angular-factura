import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-flayer',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './flayer.html',
  styleUrl: './flayer.scss'
})
export class Flayer {
  constructor(private router: Router) { }

  irARegistro() {
    this.router.navigate(['/autenticacion']);
  }
}
