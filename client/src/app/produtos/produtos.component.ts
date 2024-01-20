import { Component, OnInit } from '@angular/core';
import { FormBaseService } from '../shared/service/form-base.service';
import { ProdutoService } from './service/produto.service';
import { IProduto, Produto } from '../core/types/Produto';
import { Subscription } from 'rxjs';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'app-produtos',
  templateUrl: './produtos.component.html',
  styleUrls: ['./produtos.component.scss']
})
export class ProdutosComponent implements OnInit{

  produtos: Produto[] = [];
  pageable: IProduto | null | undefined;

  produtosSubscription: Subscription = new Subscription();
  pageableSubscription: Subscription = new Subscription();

  pageIndex: number = 0;
  pageSize: number = 10;

  formProdutos: FormGroup;

  constructor(
    private formBaseService: FormBaseService,
    private produtoService: ProdutoService
    )
  {
    this.formProdutos = this.formBaseService.criarFormulario();
    this.formProdutos = this.formBaseService.adicionaCamposCliente(this.formProdutos);
  }
  
  ngOnInit(): void {
    this.produtosSubscription = this.produtoService.produtos$.subscribe((p) => {
      console.log(p);
      this.produtos = p;
    })

    this.pageableSubscription = this.produtoService.pageable$.subscribe((p) => {
      this.pageable = p;
    })

    this.produtoService.findAll(0, 20);
  }
  
}
