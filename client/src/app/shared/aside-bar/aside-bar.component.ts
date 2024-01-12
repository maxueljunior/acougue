import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { TokenService } from 'src/app/autenticacao/services/token.service';

@Component({
  selector: 'app-aside-bar',
  templateUrl: './aside-bar.component.html',
  styleUrls: ['./aside-bar.component.scss']
})
export class AsideBarComponent {

  constructor(
    private router: Router,
    private tokenService: TokenService){
  }

  navegarParaFornecedores(event: any){
    this.router.navigate(['fornecedores'])
  }

  navegarParaHome(event: any){
    this.router.navigate(['home'])
  }

  logout(event: any){
    this.tokenService.excluirToken();
    this.router.navigate(['login'])
  }
}
