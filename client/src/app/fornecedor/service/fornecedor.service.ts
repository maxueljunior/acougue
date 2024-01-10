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

  private fornecedorSubject = new BehaviorSubject<Fornecedor[]>([]);
  private pageableSubject = new BehaviorSubject<IFornecedor | null>(null);

  fornecedores$ = this.fornecedorSubject.asObservable();
  pageable$ = this.pageableSubject.asObservable();


  constructor(
    private http: HttpClient
  ) { }

  findAll(page: number, size: number): void{
    const options = new HttpParams().set('page',page).set('size',size);

    this.http.get<IFornecedor>(this.urlApi, { params: options}).subscribe(
      (f) => {
        const fornecedorSimples = f.content.map((forn) => ({
          id: forn.id,
          razaoSocial: forn.razaoSocial,
          cnpj: forn.cnpj,
          nomeContato: forn.nomeContato,
          telefone: forn.telefone
        }));

        this.fornecedorSubject.next(fornecedorSimples);
        this.pageableSubject.next(f);
      }
    )
  }

  criar(dados: Fornecedor, pageSize: number): void{
    this.http.post<Fornecedor>(this.urlApi, dados).subscribe((f) => {
      let forns = this.fornecedorSubject.getValue();
      forns = forns.slice(0, pageSize - 1);
      forns.unshift(f);
      this.fornecedorSubject.next(forns);
    })
  }

  editar(dadosAntigo: Fornecedor, dadosEditado: Fornecedor): void{
    this.http.put<Fornecedor>(`${this.urlApi}/${dadosAntigo.id}`, dadosEditado).subscribe((f) => {
      let forns = this.fornecedorSubject.getValue();
      let index = forns.findIndex(dados => dados.id === dadosAntigo.id);

      if(index !== -1){
        // E necessário criar um novo array, pois se for o mesmo o Angular não dectecta que houve alguma alteração
        let novoForns = [...forns.slice(0, index), f,...forns.slice(index + 1)]
        this.fornecedorSubject.next(novoForns);
      }
    });
  }

  delete(id: number, pageIndex: number, pageSize: number): void{
    this.http.delete(`${this.urlApi}/${id}`).subscribe(() => {
      let forns = this.fornecedorSubject.getValue();
      let index = forns.findIndex(dados => dados.id === id);
      if(index !== -1){
        forns.splice(index, 1);
        let novoForm = [...forns];
        this.fornecedorSubject.next(novoForm);

        let totalElementos = this.pageableSubject.value!.totalElements;
        if(totalElementos > pageSize){
          this.findAll(pageIndex, pageSize);
          console.log(`${totalElementos} elementos, ${pageSize} tamanho da pagina`);
        }
      }
    })
  }
}
