import { Component } from '@angular/core';
import { FormLoginService } from '../service/form-login.service';

@Component({
  selector: 'app-form-login',
  templateUrl: './form-login.component.html',
  styleUrls: ['./form-login.component.scss']
})
export class FormLoginComponent {
  
  constructor(public formLoginService: FormLoginService){
    
  }

  public submit(): void{
    console.log(this.formLoginService.formLogin.value);
  }
}
