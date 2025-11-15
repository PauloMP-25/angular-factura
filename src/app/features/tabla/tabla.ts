import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ServicioBoleta } from '../../core/servicios/servicio-boleta';
import { Boleta } from '../../core/models/boleta.models';
import { ModalBoleta } from '../modal-boleta/modal-boleta';

@Component({
  selector: 'app-tabla',
  imports: [CommonModule, ModalBoleta],
  templateUrl: './tabla.html',
  styleUrl: './tabla.scss'
})
export class Tabla {
  boletaService = inject(ServicioBoleta);
  private router = inject(Router);

  // Signals
  boletas = signal<Boleta[]>([]);
  loading = signal(true);
  errorMessage = signal('');
  // Nuevo signal para manejar el estado del modal/ID seleccionado
  selectedBoletaId = signal<number | null>(null);

  ngOnInit(): void {
    this.cargarBoletas();
  }

  /**
   * Cargar boletas del usuario
   */
  cargarBoletas(): void {
    this.loading.set(true);
    this.errorMessage.set('');

    this.boletaService.obtenerMisBoletasOrdenadas().subscribe({
      next: (boletas) => {
        console.log('✅ Boletas cargadas:', boletas);
        this.boletas.set(boletas);
        this.loading.set(false);
      },
      error: (error) => {
        console.error('❌ Error al cargar boletas:', error);
        this.errorMessage.set(error.message);
        this.loading.set(false);
      }
    });
  }

  /**
   * Ver detalle de boleta
   */
  verDetalle(idBoleta: number): void {
    this.router.navigate(['/detalles', idBoleta]);
  }

  /**
   * Mostrar el modal y cargar los detalles de la boleta
   * @param id El ID de la boleta a ver
   */
  verDetalles(id: number): void {
    console.log(`🔎 Abriendo detalles para Boleta ID: ${id}`);
    this.selectedBoletaId.set(id);
    // Lógica para abrir el modal (ej. usando un servicio de modal o una variable booleana)
  }
}

