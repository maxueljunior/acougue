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

  findAll(page: number, size: number, descricao: string): void{
    const options = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('q', descricao);

    this.http.get<IProduto>(this.urlApi, {params: options}).subscribe({
      next: (p) => {
        const produtosSimples = p.content.map((produtos) => ({
          id: produtos.id,
          descricao: produtos.descricao,
          unidade: produtos.unidade,
          totalQuantidade: produtos.totalQuantidade
        }))
        this.produtoSubject.next(produtosSimples);
        this.pageableSubject.next(p);
      }
    })
  }

  create(dados: Produto, pageSize: number): void{
    this.http.post<Produto>(this.urlApi, dados).subscribe({
      next: (p) => {
        let produtos = this.produtoSubject.getValue();
        produtos = produtos.slice(0, pageSize - 1);
        p.totalQuantidade = 0;
        produtos.unshift(p);
        this.produtoSubject.next(produtos);
      }
    });
  }

  edit(id:number, dados: Produto): void{
    this.http.patch<Produto>(`${this.urlApi}/${id}`, dados).subscribe({
      next: (p) => {
        let produtos = this.produtoSubject.getValue();
        let index = produtos.findIndex(prod => prod.id === id);
        p.totalQuantidade = produtos[index].totalQuantidade;
        if(index !== -1){
          let novoProduto = [...produtos.slice(0, index), p, ...produtos.slice(index + 1)]
          this.produtoSubject.next(novoProduto);
        }
      }
    })
  }

  delete(id: number, pageIndex: number, pageSize: number, descricao: string): void{
    this.http.delete(`${this.urlApi}/${id}`).subscribe({
      next: () => {
        let produtos = this.produtoSubject.getValue();
        let index = produtos.findIndex(prod => prod.id === id);

        if(index !== -1){
          produtos.splice(index, 1);
          let novoProduto = [...produtos];
          this.produtoSubject.next(novoProduto);

          let totalElementos = this.pageableSubject.value!.totalElements;
          if(totalElementos > pageSize){
            this.findAll(pageIndex, pageSize, descricao);
          }
        }
      }
    })
  }
}
