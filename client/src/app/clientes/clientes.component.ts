import { Component, OnInit } from '@angular/core';
import { FormBaseService } from '../shared/service/form-base.service';
import { FormGroup } from '@angular/forms';
import { Cliente, ICliente } from '../core/types/Cliente';
import { ClienteService } from './service/cliente.service';
import { Subscription } from 'rxjs';
import { Responsivo } from '../core/types/Types';

@Component({
  selector: 'app-clientes',
  templateUrl: './clientes.component.html',
  styleUrls: ['./clientes.component.scss']
})
export class ClientesComponent implements OnInit{

  formCliente: FormGroup;
  clientes: Cliente[] = []
  pageable: ICliente | null | undefined;

  clienteSubscription = new Subscription();
  pageableSubscription = new Subscription();

  colunas: Responsivo[] = [
    {nome: "Codigo", atributo: "id"},
    {nome: "Nome", atributo: "nome"},
    {nome: "Telefone", atributo: "telefone"},
  ]

  colunasResponsivas: Responsivo[] = [
    {nome: "Codigo", atributo: "id"},
    {nome: "Nome", atributo: "nome"},
    {nome: "Sobrenome", atributo: "sobrenome"},
    {nome: "Telefone", atributo: "telefone"},
    {nome: "Sexo", atributo: "sexo"},
    {nome: "Data de Nascimento", atributo: "dataNascimento"},
    {nome: "Rua", atributo: "endereco.rua"},
    {nome: "Bairro", atributo: "endereco.bairro"},
    {nome: "Numero", atributo: "endereco.numero"},
  ]

  displayedColumns: string[] = ['id', 'nome', 'sobrenome', 'telefone', 'sexo', 'dataNascimento', 'endereco.rua', 'endereco.bairro', 'endereco.numero', 'acoes'];
  displayedesColumns: string[] = ['id', 'nome', 'telefone', 'acoes'];

  constructor(
    private formBaseService: FormBaseService,
    private clienteService: ClienteService
  ){
    this.formCliente = formBaseService.criarFormulario();
    this.formCliente = formBaseService.adicionaCamposCliente(this.formCliente);
    console.log(this.formCliente.value);
  }

  ngOnInit(): void {
    this.clienteSubscription = this.clienteService.clientes$.subscribe((c) => {
      console.log(c);
      this.clientes = c;
    })

    this.pageableSubscription = this.clienteService.pageable$.subscribe((p) => {
      this.pageable = p;
    })

    this.clienteService.findAll(0, 10, '');
    console.log(this.clientes);
  }


}
