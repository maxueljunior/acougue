import { Injectable, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Injectable({
  providedIn: 'root'
})
export class FormBaseService{

  formBase!: FormGroup;
  
  constructor(private formBuilder: FormBuilder) { 
  }

  criarFormulario(){
    this.formBase = this.formBuilder.group({

    })
    return this.formBase;
    // return this.formBuilder.group({

    // });
  }

  adicionaCamposFornecedor(formGroup: FormGroup){
    formGroup = this.formBuilder.group({
      razaoSocial:['', Validators.required],
      cnpj:['', Validators.required],
      nomeContato:['', Validators.required],
      telefone:['', Validators.required]
    })
    this.formBase = formGroup;
    return this.formBase;

    // formGroup = this.formBuilder.group({
    //   razaoSocial:['', Validators.required],
    //   cnpj:['', Validators.required],
    //   nomeContato:['', Validators.required],
    //   telefone:['', Validators.required]
    // })
  }

  retornaCamposFornecedor(){
    return this.formBase;
  }
}
