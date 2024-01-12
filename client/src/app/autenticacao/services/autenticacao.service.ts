import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { EMPTY, Observable, catchError, of, tap } from 'rxjs';
import { Authentication } from 'src/app/core/types/Types';
import { TokenService } from './token.service';
import { SnackMensagemComponent } from 'src/app/shared/snack-mensagem/snack-mensagem.component';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root'
})
export class AutenticacaoService {

  private apiUrl: string = '/api/login'

  constructor(
    private http: HttpClient,
    private tokenService: TokenService,
    private snackBar: MatSnackBar
    ) { }

  autenticar(username: string, password: string): Observable<HttpResponse<Authentication>>{
    return this.http.post<Authentication>(
        this.apiUrl, { username, password}, {observe: 'response'}
      ).pipe(
        tap((response) => {
          const authToken = response.body?.token || '';
          this.tokenService.salvarToken(authToken);
        }),
        catchError((err) => {
          this.verificaStatusErro(err.status);
          return EMPTY;
        })
      )
  }

  verificaStatusErro(statusErro: number): void{
    if(statusErro !== 403){
      this.openSnackBar('Ocorreu um erro inesperado!', 'falha')
    }else{
      this.openSnackBar('Usuario e senha estão incorretos!', 'falha')
    }
  }

  openSnackBar(mensagem: string, estilo: string){
    this.snackBar.openFromComponent(SnackMensagemComponent, {
      duration: 5000,
      horizontalPosition: 'right',
      verticalPosition: 'top',
      data: {
        mensagem,
        estilo
      }
    })
  }
}
