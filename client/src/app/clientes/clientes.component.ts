import { Component, OnInit } from '@angular/core';
import { FormBaseService } from '../shared/service/form-base.service';
import { FormGroup } from '@angular/forms';
import { Cliente, ICliente } from '../core/types/Cliente';
import { ClienteService } from './service/cliente.service';
import { Subscription } from 'rxjs';
import { Responsivo } from '../core/types/Types';
import { MatDialog } from '@angular/material/dialog';
import { ModalCriacaoComponent } from './modal-criacao/modal-criacao.component';
import { DatePipe } from '@angular/common';
import { PageEvent } from '@angular/material/paginator';

@Component({
  selector: 'app-clientes',
  templateUrl: './clientes.component.html',
  styleUrls: ['./clientes.component.scss']
})
export class ClientesComponent implements OnInit{

  formCliente: FormGroup;
  clientes: Cliente[] = []
  pageable: ICliente | null | undefined;
  pageIndex!: number
  pageSize!: number

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
    private clienteService: ClienteService,
    private dialog: MatDialog,
    private datePipe: DatePipe
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
  }

  alteracaoPagina(event: PageEvent): void{
    console.log(event);
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.clienteService.findAll(this.pageIndex, this.pageSize, '');
  }

  public criarCliente(event: any): void{
    this.openDialogCriar();
  }

  private openDialogCriar(): void{
    let dialogRef = this.dialog.open(ModalCriacaoComponent, {
      width: "50%",
      height: "50%",
      data:{
        editar: false
      }
    })

    dialogRef.componentInstance.criacao.subscribe((c) => {
      const dataNascimento = this.formBaseService.formBase.get('dataNascimento')?.value;

      let dataFormatada = this.formatarData(dataNascimento);

      this.formBaseService.formBase.patchValue({
        dataNascimento: dataFormatada
      })

      console.log(this.formBaseService.formBase.value)
    })
  }

  private formatarData(dataNascimento: string): string{
    let dataFormatada = this.datePipe.transform(dataNascimento, 'dd/MM/yyyy');
    return dataFormatada!;
  }

  // editarCliente(event: Cliente){
  //   if(event){
  //     this.formBaseService.formBase.patchValue({
  //       nome: event.nome,
  //       sobrenome: event.nome,
  //       telefone: event.telefone,
  //       dataNascimento: event.dataNascimento,
  //       endereco: event.endereco
  //     })
  //     this.openDialogEditar(event);
  //   }
  // }


}
