import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBaseService } from '../shared/service/form-base.service';
import { ProdutoService } from './service/produto.service';
import { IProduto, Produto } from '../core/types/Produto';
import { Subscription } from 'rxjs';
import { FormGroup } from '@angular/forms';
import { CustomPaginatorIntl } from '../core/utils/CustomPaginatorIntl';
import { MatPaginator, MatPaginatorIntl } from '@angular/material/paginator';
import { MatDialog } from '@angular/material/dialog';
import { ModalCriacaoComponent } from './modal-criacao/modal-criacao.component';
import { ModalExclusaoComponent } from '../shared/modal-exclusao/modal-exclusao.component';

@Component({
  selector: 'app-produtos',
  templateUrl: './produtos.component.html',
  styleUrls: ['./produtos.component.scss'],
  providers: [
    {
      provide: MatPaginatorIntl,
      useClass: CustomPaginatorIntl
    }
  ]
})
export class ProdutosComponent implements OnInit{

  produtos: Produto[] = [];
  pageable: IProduto | null | undefined;

  produtosSubscription: Subscription = new Subscription();
  pageableSubscription: Subscription = new Subscription();

  pageIndex: number = 0;
  pageSize: number = 10;

  formProdutos: FormGroup;
  descricao: string = '';

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(
    private formBaseService: FormBaseService,
    private produtoService: ProdutoService,
    private dialog: MatDialog
    )
  {
    this.formProdutos = this.formBaseService.criarFormulario();
    this.formProdutos = this.formBaseService.adicionaCamposProduto(this.formProdutos);
  }

  ngOnInit(): void {
    this.produtosSubscription = this.produtoService.produtos$.subscribe((p) => {
      this.produtos = p;
    })

    this.pageableSubscription = this.produtoService.pageable$.subscribe((p) => {
      this.pageable = p;
    })

    this.produtoService.findAll(0, 10, this.descricao);
  }

  getCardsForPage(event: any) {
    this.pageSize = event.pageSize;
    this.pageIndex = event.pageIndex;
    this.produtoService.findAll(this.pageIndex, this.pageSize, this.descricao);
  }

  criarProdutos(event: any){
    this.openDialogCriar();
  }

  buscarProdutos(event: any){
    this.descricao = event;
    console.log(this.descricao);
    this.produtoService.findAll(0, this.pageSize, this.descricao);
  }

  editarProduto(event: any){

    this.formBaseService.formBase.patchValue({
      descricao: event.descricao,
      unidade: event.unidade
    })

    this.openDialogEditar(event);
  }

  openDialogCriar(): void{
    let tamWidth = window.innerWidth * 0.40;
    let tamHeigth = window.innerHeight * 0.60;
    let dialogRef = this.dialog.open(ModalCriacaoComponent, {
      width: `${tamWidth}px`,
      height: `${tamHeigth}px`,
      data: {
        editar: false
      }
    })

    dialogRef.componentInstance.criacao.subscribe((p) => {
      console.log(this.formBaseService.formBase.value);
      this.produtoService.create(this.formBaseService.formBase.value, this.pageSize);
      this.formBaseService.resetarCampos();
    })
  }

  openDialogEditar(produto: Produto): void{
    let tamWidth = window.innerWidth * 0.40;
    let tamHeigth = window.innerHeight * 0.60;
    let dialogRef = this.dialog.open(ModalCriacaoComponent, {
      width: `${tamWidth}px`,
      height: `${tamHeigth}px`,
      data: {
        editar: true
      }
    })

    dialogRef.componentInstance.edicao.subscribe((p) => {
      console.log(this.formBaseService.formBase.value);
      this.produtoService.edit(produto.id, this.formBaseService.formBase.value);
      this.formBaseService.resetarCampos();
    })
  }

  openDialogExcluir(produto: Produto){
    let tamWidth = window.innerWidth * 0.40;
    let tamHeigth = window.innerHeight * 0.60;
    let dialogRef = this.dialog.open(ModalExclusaoComponent, {
      // width: `${tamWidth}px`,
      // height: `${tamHeigth}px`,
      data: {
        titulo: produto.descricao
      }
    })

    dialogRef.componentInstance.exclusao.subscribe((p) => {
      this.produtoService.delete(produto.id, this.pageIndex, this.pageSize, this.descricao);
    })
  }

  excluirProduto(event: any){
    this.openDialogExcluir(event);
  }
}
