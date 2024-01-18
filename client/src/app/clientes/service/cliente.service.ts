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

  create(dados: Cliente, pageSize: number): void{
    this.http.post<Cliente>(this.urlApi, dados).subscribe({
      next: (c) => {
        let clientes = this.clienteSubject.getValue();
        clientes = clientes.slice(0, pageSize -1);
        clientes.unshift(c);
        this.clienteSubject.next(clientes);
      }
    })
  }

  edit(id: number, cliente: Cliente): void{
    this.http.put<Cliente>(`${this.urlApi}/${id}`, cliente).subscribe({
      next: (c) => {
        let cli = this.clienteSubject.getValue();
        let index = cli.findIndex(dados => dados.id === id);
        console.log(index);
        if(index !== -1){
          // E necessário criar um novo array, pois se for o mesmo o Angular não dectecta que houve alguma alteração
          let novoCliente = [...cli.slice(0, index), c,...cli.slice(index + 1)]
          this.clienteSubject.next(novoCliente);
          // this.openSnackBar('Fornecedor editado com sucesso!', 'sucesso');
        }
      }
    })
  }
}
