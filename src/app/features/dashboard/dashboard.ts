import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { SideNav } from "../side-nav/side-nav";

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule,RouterOutlet, SideNav],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss'
})
export class Dashboard {

}
