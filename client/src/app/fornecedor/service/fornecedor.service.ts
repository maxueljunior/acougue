import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Fornecedor, IFornecedor } from 'src/app/core/types/Fornecedor';

@Injectable({
  providedIn: 'root'
})
export class FornecedorService {

  private urlApi: string = '/api/fornecedor';
  // private fornecedorSubject = new BehaviorSubject<Fornecedor[]>([]);
  // fornecedores$ = this.fornecedorSubject.asObservable();

  private fornecedorSubject = new BehaviorSubject<IFornecedor[]>([]);
  fornecedores$ = this.fornecedorSubject.asObservable();

  constructor(
    private http: HttpClient
  ) { }

  findAll(): void{
    this.http.get<IFornecedor>(`${this.urlApi}`).subscribe(
      (f) => {
        let forns = this.fornecedorSubject.getValue();
        console.log(f.totalElements);
        console.log(f);
        forns = forns.concat(f);
        this.fornecedorSubject.next(forns);
      }
    )
  }

}
