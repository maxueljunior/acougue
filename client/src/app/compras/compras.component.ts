import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { CompraEstoque } from '../core/types/ComprasEstoque';
import { Responsivo } from '../core/types/Types';

@Component({
  selector: 'app-compras',
  templateUrl: './compras.component.html',
  styleUrls: ['./compras.component.scss']
})
export class ComprasComponent {
  compras: CompraEstoque[] = [
    {id: 0, idEstoque:0, idCompras:0, quantidade:0, precoUnitario:0},
    {id: 0, idEstoque:0, idCompras:0, quantidade:0, precoUnitario:0},
    {id: 0, idEstoque:0, idCompras:0, quantidade:0, precoUnitario:0},
    {id: 0, idEstoque:0, idCompras:0, quantidade:0, precoUnitario:0},
    {id: 0, idEstoque:0, idCompras:0, quantidade:0, precoUnitario:0},
    {id: 0, idEstoque:0, idCompras:0, quantidade:0, precoUnitario:0},
    {id: 0, idEstoque:0, idCompras:0, quantidade:0, precoUnitario:0},
    {id: 0, idEstoque:0, idCompras:0, quantidade:0, precoUnitario:0},
    {id: 0, idEstoque:0, idCompras:0, quantidade:0, precoUnitario:0},
    {id: 0, idEstoque:0, idCompras:0, quantidade:0, precoUnitario:0},
    {id: 0, idEstoque:0, idCompras:0, quantidade:0, precoUnitario:0},
  ];
  displayedColumns: string[] = ['id', 'idEstoque', 'quantidade', 'precoUnitario'];
  displayedesColumns: string[] = ['id', 'idEstoque', 'quantidade', 'precoUnitario'];

  criarCompra: boolean = false;

  colunas: Responsivo[] = [
    {nome: "Nº", atributo: "id"},
    {nome: "Produto", atributo: "idEstoque"},
    {nome: "Qnt.", atributo: "quantidade"},
    {nome: "Valor Unit.", atributo: "precoUnitario"},
  ]

  colunasResponsiva: Responsivo[] = [
    {nome: "Nº", atributo: "id"},
    {nome: "Produto", atributo: "idEstoque"},
    {nome: "Qnt.", atributo: "quantidade"},
    {nome: "Valor Unit.", atributo: "precoUnitario"},
  ]

  criarCompras(): void{
    this.criarCompra = true;
  }
}

