import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { DatasProdutos } from 'src/app/core/types/Produto';
import { InsertVendaEstoque } from 'src/app/core/types/VendasEstoque';

@Injectable({
  providedIn: 'root'
})
export class VendaEstoqueService {

  private urlApi: string = 'api/itens/vendas';

  constructor(
    private http: HttpClient
  ) { }

  public getDatesWithProduct(id: number): Observable<DatasProdutos[]>{
    return this.http.get<DatasProdutos[]>(`${this.urlApi}/datas/${id}`);
  }

  public create(dados: InsertVendaEstoque[]): Observable<InsertVendaEstoque[]>{
    return this.http.post<InsertVendaEstoque[]>(this.urlApi, dados);
  }
}
