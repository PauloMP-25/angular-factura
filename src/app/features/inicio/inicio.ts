import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Flayer } from './flayer/flayer';
import { Body1 } from './body-1/body-1';
import { Body2 } from './body-2/body-2';
import { Body3 } from './body-3/body-3';

@Component({
  selector: 'app-inicio',
  standalone: true,
  imports: [CommonModule, Flayer, Body1, Body2, Body3],
  templateUrl: './inicio.html',
  styleUrl: './inicio.scss'
})
export class Inicio {

}

