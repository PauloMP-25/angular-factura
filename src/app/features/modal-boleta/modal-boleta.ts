import { Component, Input, Output, EventEmitter, inject, signal, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ServicioBoleta } from '../../core/servicios/servicio-boleta';
import { BoletaDetailsResponse, DetalleBoletaDTO } from '../../core/models/boleta.models';

@Component({
  selector: 'app-modal-boleta',
  imports: [CommonModule, FormsModule],
  templateUrl: './modal-boleta.html',
  styleUrl: './modal-boleta.scss'
})
export class ModalBoleta {
  public boletaService = inject(ServicioBoleta);

  @Input() boletaId: number | null = null; //Entrada para el ID de la boleta
  @Output() closeModal = new EventEmitter<void>();

  // Signals para manejar el estado
  boleta = signal<BoletaDetailsResponse | null>(null);
  detalles = signal<DetalleBoletaDTO[]>([]);
  loading = signal(false);
  error = signal<string | null>(null);

  /**
   * Hook para detectar cambios en la propiedad de entrada (boletaId)
   */
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['boletaId'] && this.boletaId !== null) {
      this.cargarBoletaDetalles(this.boletaId);
    } else if (changes['boletaId'] && this.boletaId === null) {
      // Limpiar datos cuando el modal se cierra
      this.boleta.set(null);
      this.detalles.set([]);
      this.error.set(null);
    }
  }

  /**
   * Cargar la boleta completa y sus detalles
   */
  cargarBoletaDetalles(id: number): void {
    this.loading.set(true);
    this.error.set(null);

    this.boletaService.obtenerBoletaPorId(id).subscribe({
      next: (response) => {
        this.boleta.set(response);
        this.detalles.set(response.detalles);
        this.loading.set(false);
      },
      error: (e) => {
        console.error('❌ Error al cargar detalles de boleta:', e);
        this.error.set(e.message || 'Error al cargar los datos de la boleta.');
        this.loading.set(false);
      }
    });
  }

  cerrar(): void {
    this.closeModal.emit();
  }

  // Propiedad para visibilidad del modal en el template
  get isModalVisible(): boolean {
    return this.boletaId !== null;
  }
}
