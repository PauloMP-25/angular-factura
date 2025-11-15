import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Reniec } from '../../core/servicios/servicio-reniec';
import { ServicioBoleta } from '../../core/servicios/servicio-boleta';
import { DetalleRequest, BoletaRequest } from '../../core/models/boleta.models';

// Interfaz local para los datos del cliente
interface ClienteData {
  dni: string;
  nombres: string;
  apellidos: string;
  email: string;
}

// Interfaz local para los items del formulario
interface ProductoItem {
  nombreProducto: string;
  precioUnitario: number;
  cantidad: number;
}

@Component({
  selector: 'app-formulario',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './formulario.html',
  styleUrl: './formulario.scss'
})
export class Formulario {
  private reniecService = inject(Reniec);
  private boletaService = inject(ServicioBoleta);

  // Signals para el estado
  cargandoReniec = signal(false);
  cargandoBoleta = signal(false);
  mensajeError = signal('');
  mensajeExito = signal('');

  // Datos del Cliente
  cliente = signal<ClienteData>({ dni: '', nombres: '', apellidos: '', email: '' });

  // Lista de Productos (Items)
  productos = signal<ProductoItem[]>([
    { nombreProducto: '', precioUnitario: 0, cantidad: 1 } // Item inicial
  ]);

  // Propiedades calculadas
  totalBoleta = signal(0);
  subtotalBoleta = signal(0);

  constructor() {
    this.calcularTotales();
  }

  /**
   * CALCULAR TOTALES
   * Se llama cada vez que cambia un producto.
   */
  calcularTotales(): void {
    const items = this.productos();
    const subtotal = items.reduce((sum, item) =>
      sum + (item.precioUnitario * item.cantidad), 0
    );
    // Asumimos que no hay descuento y total = subtotal
    this.subtotalBoleta.set(subtotal);
    this.totalBoleta.set(subtotal);
  }

  /**
   * GESTIÓN DE PRODUCTOS
   */
  agregarProducto(): void {
    this.productos.update(items => [
      ...items,
      { nombreProducto: '', precioUnitario: 0, cantidad: 1 }
    ]);
    this.calcularTotales();
  }

  eliminarProducto(index: number): void {
    this.productos.update(items => {
      items.splice(index, 1);
      return items;
    });
    this.calcularTotales();
  }

  /**
   * BUSCAR DNI (Usando Reniec Service)
   */
  /**
     * BUSCAR DNI (Usando Reniec Service) - Integración Final
     */
  buscarDni(): void {
    this.limpiarMensajes();
    const dni = this.cliente().dni;

    // Validación manual de DNI (obligatorio en FormsModule)
    if (dni.length !== 8) {
      this.mensajeError.set('Ingrese un DNI válido de 8 dígitos.');
      return;
    }

    this.cargandoReniec.set(true);

    this.reniecService.obtenerDatosPorDni(dni).subscribe({
      next: (response: any) => {
        this.cargandoReniec.set(false);
        if (response && response.data) {
          const nombres = response.data.nombres || '';
          const apellidoPaterno = response.data.apellido_paterno || '';
          const apellidoMaterno = response.data.apellido_materno || '';

          // Actualiza la Signal del cliente con los datos encontrados
          this.cliente.update(c => ({
            ...c,
            nombres: nombres,
            apellidos: `${apellidoPaterno} ${apellidoMaterno}`.trim()
          }));
          this.cargandoReniec.set(false);
          this.mensajeExito.set('Datos de cliente encontrados.');
        } else {
          // SI NO SE ENCUENTRA, LIMPIAR LOS CAMPOS DE NOMBRES Y APELLIDOS
          this.cliente.update(c => ({
            ...c,
            nombres: '',
            apellidos: '',
          }));
          this.mensajeError.set('No se encontraron datos para este DNI.');
        }
      },
      error: (err) => {
        this.cargandoReniec.set(false);
        this.mensajeError.set('Error al conectar con el servicio DNI.');
        console.error('Error Reniec:', err);
      }
    });
  }

  /**
   * CREAR BOLETA (Llamada al backend)
   */
  crearBoleta(): void {
    this.limpiarMensajes();

    // 1. Validaciones mínimas
    if (!this.cliente().dni || this.productos().length === 0 || this.totalBoleta() <= 0) {
      this.mensajeError.set('Debe completar el DNI del cliente y añadir productos válidos.');
      return;
    }

    this.cargandoBoleta.set(true);

    // Mapear ProductoItem a DetalleRequest (que espera el backend)
    const cartItems: DetalleRequest[] = this.productos().map(p => ({
      nombreProducto: p.nombreProducto,
      precioUnitario: p.precioUnitario,
      cantidad: p.cantidad
    }));

    // Construir el Request
    const request: BoletaRequest = {
      cartItems: cartItems,
      total: this.totalBoleta(),
      subtotal: this.subtotalBoleta(),
      // DATOS DEL CLIENTE ENVIADOS AL BACKEND
      nombreCliente: `${this.cliente().nombres} ${this.cliente().apellidos}`.trim(),
      documentoCliente: this.cliente().dni,
      emailCliente: this.cliente().email || 'example@gmail.com'
    };

    // Enviar al servicio
    this.boletaService.crearBoleta(request).subscribe({
      next: (response) => {
        this.cargandoBoleta.set(false);
        this.mensajeExito.set(`Boleta creada exitosamente.`);
        this.limpiarFormulario();
      },
      error: (error) => {
        this.cargandoBoleta.set(false);
        this.mensajeError.set(error.message || 'Error desconocido al crear la boleta.');
        console.error('Error al crear boleta:', error);
      }
    });
  }

  /**
   * Utilidades
   */
  private limpiarMensajes(): void {
    this.mensajeError.set('');
    this.mensajeExito.set('');
  }

  private limpiarFormulario(): void {
    this.cliente.set({ dni: '', nombres: '', apellidos: '', email: '' });
    this.productos.set([{ nombreProducto: '', precioUnitario: 0, cantidad: 1 }]);
    this.calcularTotales();
  }

  // Helper para el template
  formatearPrecio(precio: number): string {
    return this.boletaService.formatearPrecio(precio);
  }

  /**
   * Actualiza el DNI del cliente y mantiene el resto de sus datos.
   * @param dni Nuevo valor del DNI.
   */
  actualizarDni(dni: string): void {
    this.cliente.update(c => ({
      ...c,
      dni: dni
    }));
  }

  /**
   * Actualiza el email del cliente y mantiene el resto de sus datos.
   * @param email Nuevo valor del email.
   */
  actualizarEmail(email: string): void {
    this.cliente.update(c => ({
      ...c,
      email: email
    }));
  }

  /**
   * Actualiza el valor de un campo específico (nombre, precio o cantidad)
   * para un producto dado su índice y recalcula totales.
   */
  actualizarProductoCampo(index: number, campo: keyof ProductoItem, valor: any): void {
    this.productos.update(items => {
      // Usar la función de conversión de tipo si es numérico
      const valorCorregido = (campo === 'precioUnitario' || campo === 'cantidad') ? Number(valor) : valor;

      items[index][campo] = valorCorregido as never; // 'as never' para evitar error de tipado con 'campo'

      // Recalcular si cambiamos precio o cantidad
      if (campo === 'precioUnitario' || campo === 'cantidad') {
        this.calcularTotales();
      }
      return items;
    });
  }
}
