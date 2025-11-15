import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ServicioBoletaUtils {


  private readonly IGV_RATE = 0.18;

  constructor() { }

  calcularLineaTotal(qty: number, price: number, discount: number): number {
    const lineTotal = (qty * price) - discount;
    return Number(lineTotal.toFixed(2));
  }

  calcularTotalesFinales(subtotalNeto: number) {
    const opGravadas = subtotalNeto / (1 + this.IGV_RATE);
    const igv = subtotalNeto - opGravadas;
    const finalTotal = subtotalNeto;

    return {
      opGravadas: opGravadas.toFixed(2),
      igv: igv.toFixed(2),
      finalTotal: finalTotal.toFixed(2)
    };
  }

  calcularTotalesBoleta(productos: { qty: number; price: number; discount: number }[]) {
    const subtotal = productos.reduce((acc, p) => acc + this.calcularLineaTotal(p.qty, p.price, p.discount), 0);
    return this.calcularTotalesFinales(subtotal);
  }
}

