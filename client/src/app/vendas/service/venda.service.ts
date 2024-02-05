import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Venda } from 'src/app/core/types/Vendas';

@Injectable({
  providedIn: 'root'
})
export class VendaService {

  private urlApi: string = 'api/vendas';

  constructor(
    private http: HttpClient
  ) { }

  public create(idCliente: number, condicaoPagamento: string): Observable<Venda>{
    return this.http.post<Venda>(this.urlApi, {
      idCliente: idCliente,
      condicaoPagamento: condicaoPagamento
    });
  }
}
