import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { IProduto, Produto } from 'src/app/core/types/Produto';

@Injectable({
  providedIn: 'root'
})
export class ProdutoService {

  private urlApi: string = "/api/produtos";

  public produtoSubject = new BehaviorSubject<Produto[]>([]);
  public pageableSubject = new BehaviorSubject<IProduto | null>(null);

  produtos$ = this.produtoSubject.asObservable();
  pageable$ = this.pageableSubject.asObservable();

  constructor(
    private http: HttpClient
  ) { }

  findAll(page: number, size: number): void{
    const options = new HttpParams()
      .set('page', page)
      .set('size', size);

    this.http.get<IProduto>(this.urlApi, {params: options}).subscribe({
      next: (p) => {
        const produtosSimples = p.content.map((produtos) => ({
          id: produtos.id,
          descricao: produtos.descricao,
          unidade: produtos.unidade
        }))
        this.produtoSubject.next(produtosSimples);
        this.pageableSubject.next(p);
      }
    })
  }
}
