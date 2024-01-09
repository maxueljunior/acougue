import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { Fornecedor, IFornecedor } from '../core/types/Fornecedor';
import { FornecedorService } from './service/fornecedor.service';
import { PageEvent } from '@angular/material/paginator';
import { FormBaseService } from '../shared/service/form-base.service';

@Component({
  selector: 'app-fornecedor',
  templateUrl: './fornecedor.component.html',
  styleUrls: ['./fornecedor.component.scss']
})
export class FornecedorComponent implements OnInit, OnDestroy{

  fornecedorSubscription: Subscription = new Subscription();
  pageableSubscription: Subscription = new Subscription();

  fornecedores: Fornecedor[] = [];
  pageable: IFornecedor | null | undefined;
  pageIndex: number = 0;
  pageSize: number = 10;

  constructor(
    private fornecedorService: FornecedorService,
    private formBaseService: FormBaseService
    ){
    }

  ngOnInit(): void {
    this.fornecedorSubscription = this.fornecedorService.fornecedores$.subscribe((f) => {
      this.fornecedores = f;
    })
    this.pageableSubscription = this.fornecedorService.pageable$.subscribe((p) => {
      this.pageable = p;
    })
    this.fornecedorService.findAll(0,10);

  }

  ngOnDestroy(): void {
    this.fornecedorSubscription.unsubscribe();
    this.pageableSubscription.unsubscribe();
  }

  alteracaoPagina(event: PageEvent): void{
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.fornecedorService.findAll(this.pageIndex, this.pageSize);
  }

  criarFornecendor(event: boolean): void{
    if(event){
      this.fornecedorService.criar(this.formBaseService.formBase.value, this.pageSize);
      this.formBaseService.formBase.reset();
    }
  }
}
