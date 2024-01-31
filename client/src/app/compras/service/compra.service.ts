import { BehaviorSubject, Observable } from 'rxjs';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Compras, ICompras, Upload } from 'src/app/core/types/Compras';
import { Fornecedor } from 'src/app/core/types/Fornecedor';
import { MatSnackBar } from '@angular/material/snack-bar';
import { SnackMensagemComponent } from 'src/app/shared/snack-mensagem/snack-mensagem.component';

@Injectable({
  providedIn: 'root'
})
export class CompraService {

  private urlApi: string = '/api/compras';
  private urlApiUpload: string = '/api/arquivos/compras';
  private comprasSubject = new BehaviorSubject<Compras[]>([]);
  private pageableSubject = new BehaviorSubject<ICompras | null>(null);

  compras$ = this.comprasSubject.asObservable();
  pageable$ = this.pageableSubject.asObservable();

  constructor(
    private http: HttpClient,
    private snackBar: MatSnackBar
  ) { }

  create(fornecedor: Fornecedor | null): Observable<Compras>{
    return this.http.post<Compras>(this.urlApi, {
      "idFornecedor": fornecedor?.id
    });
  }

  uploadArchive(arquivo: File, idCompras: number): Observable<Upload>{
    const formData: FormData = new FormData();
    formData.append('file', arquivo, arquivo.name);

    return this.http.post<Upload>(`${this.urlApiUpload}/${idCompras}/one`, formData);
  }

  findAll(page: number, size: number, razaoSocial: string): void{
    const options = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('q', razaoSocial);

    this.http.get<ICompras>(this.urlApi, {params:options}).subscribe({
      next: (c) => {

        if(c.page.totalElements > 0){
          const comprasSimples = c._embedded.comprasDTOList.map((compra) => ({
            id: compra.id,
            valorTotal: compra.valorTotal,
            fornecedor: compra.fornecedor,
            data: compra.data,
            _links: compra?._links
          }));

          this.comprasSubject.next(comprasSimples);
        }else{
          this.comprasSubject.next([]);
        }
        this.pageableSubject.next(c);
      },
      error: (err) => {
        console.log(err);
      }
    })
  }

  delete(id: number, pageIndex: number, pageSize: number, razaoSocial: string): void{
    this.http.delete(`${this.urlApi}/${id}`).subscribe({
      next: () => {
        let c = this.comprasSubject.getValue();
        let index = c.findIndex(compra => compra.id === id);
        if(index !== -1){
          c.splice(index, 1);
          let novasCompras = [...c];
          this.comprasSubject.next(novasCompras);

          let totalElementos = this.pageableSubject.value!.page.totalElements;

          if(totalElementos > pageSize){
            this.findAll(pageIndex, pageSize, razaoSocial);
          }

          this.openSnackBar('Compra deletada com sucesso!', 'sucesso');
        }
      },
      error: (err) => {
        this.verificaStatusErro(err.status);
      }
    })
  }

  downloadArchive(urlDownload: string): void{

    let headers = new HttpHeaders({
      'Content-Disposition': 'attachment'
    });

    let url = urlDownload.replace('http://localhost:8080', '');

    this.http.get(url, { headers, responseType: 'blob', observe: 'response' }).subscribe({
      next: (response) => {
        const contentDispositionHeader = response.headers.get('Content-Disposition');
        let fileName = 'arquivo_desconhecido';

        if (contentDispositionHeader) {
          const matches = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/.exec(contentDispositionHeader);
          if (matches != null && matches[1]) {
            fileName = matches[1].replace(/['"]/g, '');
          }
        }

        if (response.body !== null) {
          const blob = new Blob([response.body], { type: 'application/octet-stream' });
          const url = window.URL.createObjectURL(blob);

          const link = document.createElement('a');
          link.href = url;
          link.download = fileName;
          link.click();

          window.URL.revokeObjectURL(url);
        }

        this.openSnackBar('Aguarde a inicialização do Download!', 'sucesso');
      },
      error: (err) => {
        this.verificaStatusErro(err.status);
      }
    });
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
