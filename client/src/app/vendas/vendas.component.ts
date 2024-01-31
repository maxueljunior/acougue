import { Component, OnInit } from "@angular/core";
import { ClienteService } from "../clientes/service/cliente.service";
import { FormControl, Validators } from "@angular/forms";
import { Cliente } from "../core/types/Cliente";
import { Observable, Subscription, map, startWith } from "rxjs";

@Component({
  selector: 'app-vendas',
  templateUrl: './vendas.component.html',
  styleUrls: ['./vendas.component.scss']
})
export class VendasComponent implements OnInit{

  myControl = new FormControl<null | Cliente>(null, Validators.required);
  criarVendas: boolean = false;

  clientes: Cliente[] = [];
  clienteSubscription: Subscription = new Subscription();

  filteredOptions!: Observable<Cliente[]>;

  constructor(
    private clienteService: ClienteService
  ){

  }
  ngOnInit(): void {
    this.clienteSubscription = this.clienteService.clientes$.subscribe((c) =>{
      console.log(c);
      this.clientes = c;
    });

    this.clienteService.findAll(0, 99999, '');

    this.filteredOptions = this.myControl.valueChanges.pipe(
      startWith(''),
      map(value => {
        const name = typeof value === 'string' ? value : value?.nome;
        return name ? this._filter(name as string) : this.clientes.slice();
      }),
    );
  }

  limpar(control: FormControl): void{
    control.reset();
  }

  displayFn(cliente: Cliente): string {
    return cliente && cliente.nome ? cliente.nome + " " + cliente.sobrenome : '';
  }

  private _filter(nome: string): Cliente[] {
    const filterValue = nome.toLowerCase();

    return this.clientes.filter(option => option.nome.toLowerCase().includes(filterValue));
  }
}
