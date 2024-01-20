import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { Content, Fornecedor, IFornecedor } from 'src/app/core/types/Fornecedor';
import { SnackMensagemComponent } from 'src/app/shared/snack-mensagem/snack-mensagem.component';

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
    private http: HttpClient,
    private snackBar: MatSnackBar
  ) { }

  findAll(page: number, size: number, razao: string): void{
    const options = new HttpParams()
      .set('page',page)
      .set('size',size)
      .set('q', razao);

    this.http.get<IFornecedor>(this.urlApi, { params: options}).subscribe({
      next: (f) => {
        const fornecedorSimples = f.content.map((forn) => ({
          id: forn.id,
          razaoSocial: forn.razaoSocial,
          cnpj: forn.cnpj,
          nomeContato: forn.nomeContato,
          telefone: forn.telefone
        }));
        this.fornecedorSubject.next(fornecedorSimples);
        this.pageableSubject.next(f);
      },
      error: (err) => {
        console.log(err.status);
        this.verificaStatusErro(err.status);
      }
    })
  }

  criar(dados: Fornecedor, pageSize: number): void{
    this.http.post<Fornecedor>(this.urlApi, dados).subscribe({
      next: (f) => {
        let forns = this.fornecedorSubject.getValue();
        forns = forns.slice(0, pageSize - 1);
        forns.unshift(f);
        this.fornecedorSubject.next(forns);
        this.openSnackBar('Fornecedor cadastrado com sucesso!', 'sucesso');
      },
      error: (err) => {
        console.log(err.status);
        this.verificaStatusErro(err.status);
      }
    })
  }

  editar(dadosAntigo: Fornecedor, dadosEditado: Fornecedor): void{
    this.http.put<Fornecedor>(`${this.urlApi}/${dadosAntigo.id}`, dadosEditado).subscribe({
      next: (f) => {
        let forns = this.fornecedorSubject.getValue();
        let index = forns.findIndex(dados => dados.id === dadosAntigo.id);

        if(index !== -1){
          // E necessário criar um novo array, pois se for o mesmo o Angular não dectecta que houve alguma alteração
          let novoForns = [...forns.slice(0, index), f,...forns.slice(index + 1)]
          this.fornecedorSubject.next(novoForns);
          this.openSnackBar('Fornecedor editado com sucesso!', 'sucesso');
        }
      },
      error: (err) => {
        console.log(err.status);
        this.verificaStatusErro(err.status);
      }
    });
  }

  delete(id: number, pageIndex: number, pageSize: number, razao: string): void{
    this.http.delete(`${this.urlApi}/${id}`).subscribe({
      next: () => {
        let forns = this.fornecedorSubject.getValue();
        let index = forns.findIndex(dados => dados.id === id);
        if(index !== -1){
          forns.splice(index, 1);
          let novoForm = [...forns];
          this.fornecedorSubject.next(novoForm);

          let totalElementos = this.pageableSubject.value!.totalElements;
          if(totalElementos > pageSize){
            this.findAll(pageIndex, pageSize, razao);
            // console.log(`${totalElementos} elementos, ${pageSize} tamanho da pagina`);
          }
          this.openSnackBar('Fornecedor deletado com sucesso!', 'sucesso');
        }
      },
      error: (err) => {
        console.log(err.status);
        this.verificaStatusErro(err.status);
      }
    })
  }

  verificaStatusErro(statusErro: number): void{
    if(statusErro === 403){
      this.openSnackBar('Sua sessão expirou!', 'falha')
    }else{
      this.openSnackBar('Ocorreu um erro inesperado!', 'falha')
    }
  }

  openSnackBar(mensagem: string, estilo: string){
    this.snackBar.openFromComponent(SnackMensagemComponent, {
      duration: 5000,
      horizontalPosition: 'right',
      verticalPosition: 'top',
      data: {
        mensagem,
        estilo
      }
    })
  }
}
