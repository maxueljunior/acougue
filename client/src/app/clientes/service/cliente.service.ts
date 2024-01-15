import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { BehaviorSubject } from 'rxjs';
import { Cliente, ICliente } from 'src/app/core/types/Cliente';
import { IFornecedor } from 'src/app/core/types/Fornecedor';

@Injectable({
  providedIn: 'root'
})
export class ClienteService {

  private urlApi: string = '/api/clientes'

  private clienteSubject = new BehaviorSubject<Cliente[]>([]);
  private pageableSubject = new BehaviorSubject<ICliente | null>(null);

  clientes$ = this.clienteSubject.asObservable();
  pageable$ = this.pageableSubject.asObservable();

  constructor(
    private http: HttpClient,
    private snackBar: MatSnackBar
  ) { }

  findAll(page: number, size: number, nome: string): void{
    const options = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('q', nome);
    
    this.http.get<ICliente>(this.urlApi, { params: options}).subscribe({
      next: (c) => {
        const clientesSimples = c.content.map((cliente) => ({
          id: cliente.id,
          nome: cliente.nome,
          sobrenome: cliente.sobrenome,
          telefone: cliente.telefone,
          sexo: cliente.sexo,
          dataNascimento: cliente.dataNascimento,
          // endereco: cliente.endereco
          endereco: {
            rua: cliente.endereco.rua,
            bairro: cliente.endereco.bairro,
            numero: cliente.endereco.numero
          }
        }));
        this.clienteSubject.next(clientesSimples);
        this.pageableSubject.next(c);
      },
      error: (err) => {
        console.log(err.status);
      }
    })
  }
}
