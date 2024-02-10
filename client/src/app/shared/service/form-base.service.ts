import { Injectable, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ValidaQuantidadeDirective } from 'src/app/directives/valida-quantidade.directive';

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
      cnpj:['', [Validators.required]],
      nomeContato:['', Validators.required],
      telefone:['', [Validators.required, Validators.minLength(11)]]
    })
    this.formBase = formGroup;
    return this.formBase;
  }

  adicionaCamposCliente(formGroup: FormGroup){
    formGroup = this.formBuilder.group({
      nome:['', Validators.required],
      sobrenome:['', Validators.required],
      sexo: ['', Validators.required],
      telefone:['', [Validators.required, Validators.minLength(11)]],
      dataNascimento:[new Date(), Validators.required],
      endereco: this.formBuilder.group({
        bairro:[''],
        numero:[],
        rua: ['']
      })
    })
    this.formBase = formGroup;
    return this.formBase;
  }

  adicionaCamposProduto(formGroup: FormGroup){
    formGroup = this.formBuilder.group({
      descricao:['', Validators.required],
      unidade:['',Validators.required]
    })
    this.formBase = formGroup;
    return this.formBase;
  }

  adicionaCamposComprasEstoque(formGroup: FormGroup){
    formGroup = this.formBuilder.group({
      precoUnitario:['', Validators.required],
      quantidade:['', Validators.required],
      idCompras:[''],
      idEstoque:['', Validators.required]
    })

    this.formBase = formGroup;
    return this.formBase;
  }

  adicionaCamposVendasEstoque(formGroup: FormGroup){
    formGroup = this.formBuilder.group({
      idEstoque: ['', Validators.required],
      idVendas: [''],
      quantidade: ['', Validators.required],
      valorUnitario: ['', Validators.required],
      dataEstoque: ['', Validators.required]
    });

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
