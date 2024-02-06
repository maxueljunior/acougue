import { Observable } from 'rxjs';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { DatasProdutos } from 'src/app/core/types/Produto';
import { InsertVendaEstoque, VendasEstoque } from 'src/app/core/types/VendasEstoque';

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

  public getProductsWithVendas(id: number): Observable<VendasEstoque>{
    let options = new HttpParams()
      .set('page', 0)
      .set('size', 999);

    return this.http.get<VendasEstoque>(`${this.urlApi}/${id}`, {params: options});
  }
}
