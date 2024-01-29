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
import { ModalExclusaoComponent } from '../shared/modal-exclusao/modal-exclusao.component';

@Component({
  selector: 'app-clientes',
  templateUrl: './clientes.component.html',
  styleUrls: ['./clientes.component.scss']
})
export class ClientesComponent implements OnInit{

  formCliente: FormGroup;
  clientes: Cliente[] = []
  pageable: ICliente | null | undefined;
  pageIndex: number = 0;
  pageSize: number = 10;
  nome: string = '';

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
    // console.log(event);
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.clienteService.findAll(this.pageIndex, this.pageSize, this.nome);
  }

  buscaNome(event: any){
    this.nome = event;
    this.clienteService.findAll(0, this.pageSize, this.nome);
  }

  public criarCliente(event: any): void{
    this.openDialogCriar();
  }

  public editarCliente(event: any): void{
    this.openDialogEditar(event);
  }

  excluirCliente(event: any): void{
    this.openDialogExcluir(event);
  }

  private openDialogCriar(): void{
    let tamWidth = window.innerWidth * 0.60;
    let tamHeigth = window.innerHeight * 0.60;

    if(tamWidth < 490){
      tamWidth = window.innerWidth * 0.85;
    }
    console.log(tamWidth);
    let dialogRef = this.dialog.open(ModalCriacaoComponent, {
      width: `${tamWidth}px`,
      height: `${tamHeigth}px`,
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

      this.clienteService.create(this.formBaseService.formBase.value, this.pageSize);
      this.formBaseService.resetarCampos();
    })
  }

  private openDialogEditar(event: Cliente){
    let tamWidth = window.innerWidth * 0.60;
    let tamHeigth = window.innerHeight * 0.60;

    if(tamWidth < 490){
      tamWidth = window.innerWidth * 0.85;
    }

    this.passarValoresParaFormulario(event);
    let dialogRef = this.dialog.open(ModalCriacaoComponent, {
      width: `${tamWidth}px`,
      height: `${tamHeigth}px`,
      data:{
        editar: true
      }
    })

    dialogRef.componentInstance.edicao.subscribe((c) => {
      const dataNascimento = this.formBaseService.formBase.get('dataNascimento')?.value;

      let dataFormatada = this.formatarData(dataNascimento);

      this.formBaseService.formBase.patchValue({
        dataNascimento: dataFormatada
      })

      console.log(this.formBaseService.formBase.value);
      this.clienteService.edit(event.id, this.formBaseService.formBase.value);
      this.formBaseService.resetarCampos();
    })
  }

  openDialogExcluir(cliente: Cliente): void {
    let dialogRef = this.dialog.open(ModalExclusaoComponent, {
      // width: '20%',
      // height: '40%',
      data:{
        titulo: cliente.nome
      }
    });

    dialogRef.componentInstance.exclusao.subscribe((e) => {
      if(e === true){
        this.clienteService.delete(cliente.id, this.pageIndex, this.pageSize, this.nome);
      }
    })
  }

  private formatarData(dataNascimento: string): string{
    let dataFormatada = this.datePipe.transform(dataNascimento, 'dd/MM/yyyy');
    return dataFormatada!;
  }

  private passarValoresParaFormulario(cliente: Cliente): void{

    const dateParts = cliente.dataNascimento.split('/');

    const parsedDate = new Date(+dateParts[2], +dateParts[1] - 1, +dateParts[0]);

    this.formBaseService.formBase.patchValue({
      nome: cliente.nome,
      sobrenome: cliente.sobrenome,
      sexo: cliente.sexo,
      telefone: cliente.telefone,
      dataNascimento: parsedDate,
      endereco: cliente.endereco
    })
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
