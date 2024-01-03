import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { Authentication } from 'src/app/core/types/Types';
import { TokenService } from './token.service';

@Injectable({
  providedIn: 'root'
})
export class AutenticacaoService {

  private apiUrl: string = '/api/login'

  constructor(
    private http: HttpClient,
    private tokenService: TokenService
    ) { }

  autenticar(username: string, password: string): Observable<HttpResponse<Authentication>>{
    return this.http.post<Authentication>(
        this.apiUrl, { username, password}, {observe: 'response'}
      ).pipe(
        tap((response) => {
          const authToken = response.body?.token || '';
          console.log(authToken);
          this.tokenService.salvarToken(authToken);
        })
      )
  }
}
