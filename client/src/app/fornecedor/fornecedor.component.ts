import { Component, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { IFornecedor } from '../core/types/Fornecedor';
import { FornecedorService } from './service/fornecedor.service';
import { PageEvent } from '@angular/material/paginator';

@Component({
  selector: 'app-fornecedor',
  templateUrl: './fornecedor.component.html',
  styleUrls: ['./fornecedor.component.scss']
})
export class FornecedorComponent implements OnInit{

  fornecedorSubscription: Subscription = new Subscription();
  fornecedores: IFornecedor[] = [];

  constructor(private fornecedorService: FornecedorService){}

  ngOnInit(): void {
    this.fornecedorSubscription = this.fornecedorService.fornecedores$.subscribe((f) => {
      this.fornecedores = f;
    })
    this.fornecedorService.findAll(0,10);
  }

  alteracaoPagina(event: PageEvent): void{
    this.fornecedorSubscription = this.fornecedorService.fornecedores$.subscribe((f) => {
      this.fornecedores = f;
    })
    this.fornecedorService.findAll(event.pageIndex, event.pageSize);
  }
}
