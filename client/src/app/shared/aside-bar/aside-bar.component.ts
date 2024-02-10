import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { TokenService } from 'src/app/autenticacao/services/token.service';
import { UserService } from 'src/app/autenticacao/services/user.service';

@Component({
  selector: 'app-aside-bar',
  templateUrl: './aside-bar.component.html',
  styleUrls: ['./aside-bar.component.scss']
})
export class AsideBarComponent {

  usuario = '';

  constructor(
    private router: Router,
    private tokenService: TokenService,
    private userService: UserService){
      this.usuario = userService.getUsuario();
      console.log(this.userService.decodificarJWT());
      console.log(this.usuario);
  }

  navegadorParaProdutos(event: any){
    this.router.navigate(['produtos']);
  }

  navegadorParaClientes(event: any){
    this.router.navigate(['clientes']);
  }

  navegarParaFornecedores(event: any){
    this.router.navigate(['fornecedores']);
  }

  navegarParaCompras(event: any){
    this.router.navigate(['compras']);
  }

  navegarParaHome(event: any){
    this.router.navigate(['home']);
  }

  navegarParaVendas(event: any){
    this.router.navigate(['vendas']);
  }

  logout(event: any){
    this.tokenService.excluirToken();
    this.router.navigate(['login']);
  }
}
