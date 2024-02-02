import { DatasProdutos, Produto } from './../core/types/Produto';
import { Component, OnInit } from "@angular/core";
import { ClienteService } from "../clientes/service/cliente.service";
import { FormControl, FormGroup, Validators } from "@angular/forms";
import { Cliente } from "../core/types/Cliente";
import { Observable, Subscription, map, startWith } from "rxjs";
import { FormBaseService } from "../shared/service/form-base.service";
import { VendasEstoqueTable } from "../core/types/Produto";
import { ProdutoService } from '../produtos/service/produto.service';
import { VendaEstoqueService } from './service/venda-estoque.service';

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

  produtos: Produto[] = [];
  produtoSubscription: Subscription = new Subscription();
  produto!: Produto | null;

  produtoControl = new FormControl<null | Produto>(null, Validators.required);
  filteredOptionsProduto!: Observable<Produto[]>;

  datasEstoque: DatasProdutos[] = [];
  dataControl = new FormControl<null | DatasProdutos>(null, Validators.required);
  filteredOptionsData!: Observable<DatasProdutos[]>;


  podeEditar: boolean = false;
  podeExcluir: boolean = false;
  escolherData: boolean = false;
  produtoExistente: boolean = false;
  unidade: string = "Unidade";
  codigo: string = "Codigo";
  quantidadeDisponivel: string = "Qnt. Disponivel";

  vendasEstoque: VendasEstoqueTable[] = [];


  filteredOptions!: Observable<Cliente[]>;

  formVendasEstoque!: FormGroup;

  constructor(
    private clienteService: ClienteService,
    public formBaseService: FormBaseService,
    private produtoService: ProdutoService,
    private vendaEstoqueService: VendaEstoqueService
  ){
    this.formVendasEstoque = this.formBaseService.criarFormulario();
    this.formVendasEstoque = this.formBaseService.adicionaCamposVendasEstoque(this.formVendasEstoque);
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

    this.produtoSubscription = this.produtoService.produtos$.subscribe((p) => {
      this.produtos = p;
      console.log(p);
    })

    this.produtoService.findAll(0, 9999, '');

    this.filteredOptionsProduto = this.produtoControl.valueChanges.pipe(
      startWith(''),
      map(value => {
        const name = typeof value === 'string' ? value : value?.descricao;
        return name ? this._filterPn(name as string) : this.produtos.slice();
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

  displayPd(produto: Produto): string {
    return produto && produto.descricao ? produto.descricao : '';
  }

  private _filterPn(descricao: string): Produto[] {
    const filterValue = descricao.toLowerCase();

    return this.produtos.filter(option => option.descricao.toLowerCase().includes(filterValue));
  }

  displayDt(datasProdutos: DatasProdutos): string {
    return datasProdutos && datasProdutos.dataCompra ? datasProdutos.dataCompra : '';
  }

  capturarDados(event: any): void{

    this.produto = this.produtoControl.value;

    this.vendaEstoqueService.getDatesWithProduct(this.produto!.id).subscribe((datas) => {
      console.log(datas);
      this.datasEstoque = datas;
      this.quantidadeDisponivel = "Qnt. Disponivel";
      this.dataControl.reset();
    })

    this.codigo = this.produto!.id.toString();
    this.unidade = this.produto!.unidade;
    this.formBaseService.formBase.patchValue({
      idEstoque: this.codigo
    })
    this.produtoExistente = false;
  }

  capturarDadosData(event: any){
    let dados = this.dataControl.value as DatasProdutos;
    this.quantidadeDisponivel = dados.quantidade.toString();

    this.formBaseService.formBase.patchValue({
      dataEstoque: dados.dataCompra
    });

    console.log(this.formBaseService.formBase.value);
    this.verificaQuantidade();
  }

  verificaExistente(vendasEstoqueTabela: VendasEstoqueTable): boolean{
    let index = this.vendasEstoque.findIndex((c) => c.produto.id === vendasEstoqueTabela.produto.id);
    if(index === -1){
      return false;
    }

    return true;
  }

  verificaQuantidade(): boolean{
    let qntInserida = parseFloat(this.formBaseService.formBase.get('quantidade')?.value);
    let qntDisponivel = parseFloat(this.quantidadeDisponivel);

    if(qntInserida > qntDisponivel){
      this.formBaseService.formBase.setErrors({'invalid': true});
      return true;
    }

    return false;
  }
}
