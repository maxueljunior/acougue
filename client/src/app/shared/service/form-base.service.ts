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
  }

  adicionaCamposCliente(formGroup: FormGroup){
    formGroup = this.formBuilder.group({
      nome:['', Validators.required],
      sobrenome:['', Validators.required],
      telefone:['', Validators.required],
      dataNascimento:[new Date(), Validators.required],
      endereco: this.formBuilder.group({
        rua: ['',Validators.required],
        bairro:['',Validators.required],
        numero:[0, Validators.required]
      })
    })
    this.formBase = formGroup;
    return this.formBase;
  }

  retornaCampos(): FormGroup{
    return this.formBase;
  }

  resetarCampos(): void{
    this.formBase.reset();
  }
}
