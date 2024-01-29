import { BehaviorSubject, Observable } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Compras, ICompras, Upload } from 'src/app/core/types/Compras';
import { Fornecedor } from 'src/app/core/types/Fornecedor';

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
    private http: HttpClient
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

}
