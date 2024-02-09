import { Injectable } from '@angular/core';
import { TokenService } from './token.service';
import { jwtDecode } from 'jwt-decode';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private usuario: string = '';

  constructor(private tokenService: TokenService) {
    if(this.tokenService.possuiToken()){
      this.decodificarJWT();
    }
   }

  decodificarJWT(){
    const token = this.tokenService.retornarToken();
    const user = jwtDecode(token);
    this.usuario = user.sub!;
  }

  getUsuario(){
    return this.usuario;
  }

  estaLogado(){
    return this.tokenService.possuiToken();
  }
}
