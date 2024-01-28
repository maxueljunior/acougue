import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CompraEstoque } from 'src/app/core/types/ComprasEstoque';

@Injectable({
  providedIn: 'root'
})
export class CompraEstoqueService {

  private urlApi: string = '/api/itens/compras'

  constructor(
    private http: HttpClient
  ) { }

  // create(compraEstoque: CompraEstoque): Observable<CompraEstoque>{
  //   return this.http.post<CompraEstoque>(this.urlApi, compraEstoque);
  // }

  create(compraEstoque: CompraEstoque[]): Observable<CompraEstoque[]>{
    return this.http.post<CompraEstoque[]>(this.urlApi, compraEstoque);
  }
}
