import { BehaviorSubject, Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Compras, ICompras } from 'src/app/core/types/Compras';
import { Fornecedor } from 'src/app/core/types/Fornecedor';

@Injectable({
  providedIn: 'root'
})
export class CompraService {

  private urlApi: string = '/api/compras';
  private comprasSubject = new BehaviorSubject<Compras[]>([]);
  private pageableSubject = new BehaviorSubject<ICompras | null>(null);

  compras$ = this.comprasSubject.asObservable();
  pageable$ = this.pageableSubject.asObservable();

  constructor(
    private http: HttpClient
  ) { }

  create(fornecedor: Fornecedor | null): Observable<Compras>{
    return this.http.post<Compras>(this.urlApi, {
      "idFornecedor": fornecedor?.id
    });
  }
}
