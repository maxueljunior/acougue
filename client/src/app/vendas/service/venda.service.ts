import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { VendasDTO, Vendas } from 'src/app/core/types/Vendas';

@Injectable({
  providedIn: 'root'
})
export class VendaService {

  private vendasSubject = new BehaviorSubject<VendasDTO[]>([]);
  private pageableSubject = new BehaviorSubject<Vendas | null>(null);

  $vendas = this.vendasSubject.asObservable();
  $pageable = this.pageableSubject.asObservable();

  private urlApi: string = 'api/vendas';

  constructor(
    private http: HttpClient
  ) { }

  public create(idCliente: number, condicaoPagamento: string): Observable<VendasDTO>{
    return this.http.post<VendasDTO>(this.urlApi, {
      idCliente: idCliente,
      condicaoPagamento: condicaoPagamento
    });
  }

  public findAll(page: number, size: number, nome: string): void{
    const options = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('q', nome);

    this.http.get<Vendas>(this.urlApi, {params: options}).subscribe({
      next: (v) => {
        if(v._embedded.vendasDTOList.length > 0){
          let vendas = v._embedded.vendasDTOList;
          let vendasConvertidas = vendas.map((ve) => ({
            id: ve.id,
            dataVenda: ve.dataVenda,
            cliente: ve.cliente,
            condicaoPagamento: ve.condicaoPagamento,
            valorTotal: ve.valorTotal,
            _links: ve._links
          }) as VendasDTO);
          console.log(vendasConvertidas);
          this.vendasSubject.next(vendasConvertidas);
        }
        else{
          this.vendasSubject.next([]);
        }

        this.pageableSubject.next(v);
      },
      error: (err) => {
        console.log(err);
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

        // this.openSnackBar('Aguarde a inicialização do Download!', 'sucesso');
      },
      error: (err) => {
        // this.verificaStatusErro(err.status);
      }
    });
  }
}
