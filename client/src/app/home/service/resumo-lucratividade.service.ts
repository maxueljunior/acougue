import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Resumo, ResumoLucratividade } from 'src/app/core/types/Resumo';

@Injectable({
  providedIn: 'root'
})
export class ResumoLucratividadeService {

  private urlApi: string = '/api/resumo/lucratividade';

  constructor(
    private http: HttpClient
  ) { }

  gerarResumo(): Observable<Resumo>{
    return this.http.get<Resumo>(this.urlApi);
  }
}
