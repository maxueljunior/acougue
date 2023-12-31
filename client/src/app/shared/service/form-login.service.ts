import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AutenticacaoService } from 'src/app/autenticacao/services/autenticacao.service';

@Injectable({
  providedIn: 'root'
})
export class FormLoginService {

  formLogin: FormGroup;
  isAutenticate: boolean = true;

  constructor(
    private autenticacaoService: AutenticacaoService,
    private router: Router
    ) {
    this.formLogin = new FormGroup({
      username: new FormControl('', [Validators.required]),
      password: new FormControl('', [Validators.required])
    });
  }

  autentica(){
    if(this.formLogin.valid){
      this.isAutenticate = true;
      this.autenticacaoService.autenticar(
        this.formLogin.get('username')?.value,
        this.formLogin.get('password')?.value
        ).subscribe({
          next: (value) => {
            console.log(value);
            this.isAutenticate = true;
            this.router.navigateByUrl('/home');
            this.formLogin.reset();
          },
          error: (err) => {
            this.isAutenticate = false;
            console.log(err);
          }
        })
    }
  }
}
