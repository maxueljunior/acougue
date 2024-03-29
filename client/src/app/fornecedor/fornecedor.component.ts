import { Component, HostListener, OnDestroy, OnInit } from '@angular/core';
import { EMPTY, Observable, Subscription, catchError, debounceTime, distinctUntilChanged, filter, of, switchMap, tap } from 'rxjs';
import { Fornecedor, IFornecedor } from '../core/types/Fornecedor';
import { FornecedorService } from './service/fornecedor.service';
import { PageEvent } from '@angular/material/paginator';
import { FormBaseService } from '../shared/service/form-base.service';
import { FormControl, FormGroup } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { SnackMensagemComponent } from '../shared/snack-mensagem/snack-mensagem.component';
import { Responsivo } from '../core/types/Types';
import { ModalCriacaoComponent } from './modal-criacao/modal-criacao.component';
import { MatDialog } from '@angular/material/dialog';
import { ModalExclusaoComponent } from '../shared/modal-exclusao/modal-exclusao.component';

@Component({
  selector: 'app-fornecedor',
  templateUrl: './fornecedor.component.html',
  styleUrls: ['./fornecedor.component.scss']
})
export class FornecedorComponent implements OnInit, OnDestroy{

  fornecedorSubscription: Subscription = new Subscription();
  pageableSubscription: Subscription = new Subscription();
  // fornecedoresEncontrados$ = new Observable<any>();

  fornecedores: Fornecedor[] = [];
  pageable: IFornecedor | null | undefined;
  pageIndex: number = 0;
  pageSize: number = 10;
  razaoSocial: string = '';

  formFornecedor: FormGroup;

  colunas: Responsivo[] = [
    {nome: "Codigo", atributo: "id"},
    {nome: "Razão Social", atributo: "razaoSocial"},
  ]

  colunasResponsivas: Responsivo[] = [
    {nome: "Codigo", atributo: "id"},
    {nome: "Razão Social", atributo: "razaoSocial"},
    {nome: "CNPJ", atributo: "cnpj"},
    {nome: "Nome do Contato", atributo: "nomeContato"},
    {nome: "Telefone", atributo: "telefone"},
  ]

  displayedColumns: string[] = ['id', 'razaoSocial', 'cnpj', 'nomeContato', 'telefone', 'acoes'];
  displayedesColumns: string[] = ['id', 'razaoSocial', 'acoes'];

  constructor(
    private fornecedorService: FornecedorService,
    private formBaseService: FormBaseService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
    ){
      this.formFornecedor = this.formBaseService.criarFormulario();
      this.formFornecedor = this.formBaseService.adicionaCamposFornecedor(this.formFornecedor);
    }

  ngOnInit(): void {
    try{
      this.fornecedorSubscription = this.fornecedorService.fornecedores$.subscribe((f) => {
        this.fornecedores = f;
      })
      this.pageableSubscription = this.fornecedorService.pageable$.subscribe((p) => {
        this.pageable = p;
      })
      this.fornecedorService.findAll(0,10, this.razaoSocial);
    }catch(error){
      console.log('deu erro');
    }
  }

  ngOnDestroy(): void {
    this.fornecedorSubscription.unsubscribe();
    this.pageableSubscription.unsubscribe();
  }

  alteracaoPagina(event: PageEvent): void{
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.fornecedorService.findAll(this.pageIndex, this.pageSize, this.razaoSocial);
  }

  criarFornecendor(event: boolean): void{
    this.openDialogCriar();
  }

  editarFornecedor(event: Fornecedor){
    if(event){
      this.formBaseService.formBase.patchValue({
        razaoSocial: event.razaoSocial,
        cnpj: event.cnpj,
        nomeContato: event.nomeContato,
        telefone: event.telefone
      });
      this.openDialogEditar(event);
    }
  }

  openDialogCriar(): void{
    let tamWidth = window.innerWidth * 0.40;
    let tamHeigth = window.innerHeight * 0.60;

    let diaglogRef = this.dialog.open(ModalCriacaoComponent, {
      width: `${tamWidth}px`,
      height: `${tamHeigth}px`,
      data: {
        editar: false
      }
    });

    diaglogRef.componentInstance.criacao.subscribe((resposta) => {
      if(resposta === true){
        this.fornecedorService.criar(this.formBaseService.formBase.value, this.pageSize);
        this.formBaseService.resetarCampos();
      }
    })
  }

  openDialogEditar(fornecedor: Fornecedor): void {

    let tamWidth = window.innerWidth * 0.40;
    let tamHeigth = window.innerHeight * 0.60;

    let dialogRef = this.dialog.open(ModalCriacaoComponent, {
      width: `${tamWidth}px`,
      height: `${tamHeigth}px`,
      data:{
        editar: true,
        fornecedor: fornecedor
      }
    });

    dialogRef.componentInstance.edicao.subscribe((dados) => {
      this.fornecedorService.editar(fornecedor, this.formBaseService.formBase.value);
      this.formBaseService.resetarCampos();
    })
  }

  excluirFornecedor(event: Fornecedor){
    if(event){
      this.openDialogExcluir(event);
    }
  }

  openDialogExcluir(fornecedor: Fornecedor): void {
    let dialogRef = this.dialog.open(ModalExclusaoComponent, {
      // width: '20%',
      // height: '40%',
      data:{
        titulo: fornecedor.razaoSocial
      }
    });

    dialogRef.componentInstance.exclusao.subscribe((e) => {
      if(e === true){
        this.fornecedorService.delete(fornecedor.id, this.pageIndex, this.pageSize, this.razaoSocial);
      }
    })
  }

  buscaRazaoSocial(event: string){
    this.razaoSocial = event;
    this.fornecedorService.findAll(0,this.pageSize, this.razaoSocial);
  }
}
