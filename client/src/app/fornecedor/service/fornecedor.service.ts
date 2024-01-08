import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Content, Fornecedor, IFornecedor } from 'src/app/core/types/Fornecedor';

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

  findAll(page: number, size: number): void{
    const options = new HttpParams().set('page',page).set('size',size);

    this.http.get<IFornecedor>(this.urlApi, { params: options}).subscribe(
      (f) => {
        let forns = this.fornecedorSubject.getValue().splice(1,1);
        forns = forns.concat(f);
        this.fornecedorSubject.next(forns);
      }
    )
  }

  criar(dados: Fornecedor): void{
    this.http.post<Fornecedor>(this.urlApi, dados).subscribe((f) => {
      let forns = this.fornecedorSubject.getValue();
      forns[0].content.unshift(f);
      this.fornecedorSubject.next(forns);
    })
  }
}
