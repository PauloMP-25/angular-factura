import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class Reniec {
  private apiUrl = 'https://apiperu.dev/api/dni';
  private token = '47cac599fc94d1f078fc3a186d581df626b57865d496d1ed9b613f22b0e0632f';
  constructor(private http: HttpClient) { }

  obtenerDatosPorDni(dni: string): Observable<any> {
    const headers = new HttpHeaders({
      Authorization: `Bearer ${this.token}`
    });

    return this.http.post(this.apiUrl, { dni }, { headers });
  }
}
