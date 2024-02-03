import { DatasProdutos, Produto } from './../core/types/Produto';
import { Component, OnInit, ViewChild } from "@angular/core";
import { ClienteService } from "../clientes/service/cliente.service";
import { FormControl, FormGroup, Validators } from "@angular/forms";
import { Cliente } from "../core/types/Cliente";
import { Observable, Subscription, map, startWith } from "rxjs";
import { FormBaseService } from "../shared/service/form-base.service";
import { ProdutoService } from '../produtos/service/produto.service';
import { VendaEstoqueService } from './service/venda-estoque.service';
import { TableBaseComponent } from '../shared/table-base/table-base.component';
import { Responsivo } from '../core/types/Types';
import { Venda, Vendas } from '../core/types/Vendas';
import { VendaEstoqueTable } from '../core/types/VendasEstoque';
import { ModalFinalizarComponent } from '../shared/modal-finalizar/modal-finalizar.component';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-vendas',
  templateUrl: './vendas.component.html',
  styleUrls: ['./vendas.component.scss']
})
export class VendasComponent implements OnInit{

  vendas: VendaEstoqueTable[] = [];
  venda: Venda = {
    id: 1,
    condicaoPagamento: "DEBITO",
    dataVenda: "20/03/2023",
    idCliente: 2,
    valorTotal: 0.00
  };

  @ViewChild('tableBaseComponent') tableBaseComponent: TableBaseComponent | undefined;

  displayedColumns: string[] = ['id', 'produto.descricao', 'quantidade', 'valorUnitario'];
  displayedesColumns: string[] = ['id', 'produto.descricao', 'quantidade', 'valorUnitario'];

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

  valorTotal: number = 0;
  podeEditar: boolean = false;
  podeExcluir: boolean = false;
  escolherData: boolean = false;
  produtoExistente: boolean = false;
  unidade: string = "Unidade";
  codigo: string = "Codigo";
  quantidadeDisponivel: string = "Qnt. Disponivel";
  dataProduto!: DatasProdutos;

  filteredOptions!: Observable<Cliente[]>;

  formVendasEstoque!: FormGroup;

  colunas: Responsivo[] = [
    {nome: "Nº", atributo: "id"},
    {nome: "Descrição", atributo: "produto.descricao"},
    {nome: "Qnt.", atributo: "quantidade"},
    {nome: "Valor Unit. (R$)", atributo: "valorUnitario"},
  ]

  colunasResponsiva: Responsivo[] = [
    {nome: "Nº", atributo: "id"},
    {nome: "Descrição", atributo: "produto.descricao"},
    {nome: "Qnt.", atributo: "quantidade"},
    {nome: "Valor Unit. (R$)", atributo: "valorUnitario"},
  ]

  constructor(
    private clienteService: ClienteService,
    public formBaseService: FormBaseService,
    private produtoService: ProdutoService,
    private vendaEstoqueService: VendaEstoqueService,
    private dialog: MatDialog
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

    this.dataProduto = dados;

    this.quantidadeDisponivel = dados.quantidade.toString();

    this.formBaseService.formBase.patchValue({
      dataEstoque: dados.dataCompra
    });

    this.verificaQuantidade();
  }

  verificaExistente(vendasEstoqueTabela: VendaEstoqueTable): boolean{
    let index = this.vendas.findIndex((c) => c.produto.id === vendasEstoqueTabela.produto.id);
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

  addProdutos(): void {
    this.formBaseService.formBase.patchValue({
      idVendas: this.venda.id
    })

    let id = this.vendas.length + 1;
    let vendaEstoqueTable: VendaEstoqueTable = {
      id: id,
      produto: this.produtoControl.value as Produto,
      quantidade: this.formBaseService.formBase.get('quantidade')?.value,
      valorUnitario: this.formBaseService.formBase.get('valorUnitario')?.value,
      dataEstoque: this.dataProduto
    }

    if(!this.verificaExistente(vendaEstoqueTable)){
      this.vendas.push(vendaEstoqueTable);
      this.tableBaseComponent!.refreshDataSource(this.vendas);
      this.limparCamposForm();
      this.atualizarValores();
    }else{
      this.produtoExistente = true;
    }
  }

  updateProduto(): void{
    let id = this.formBaseService.formBase.get('idEstoque')?.value;

    let index = this.vendas.findIndex((v) => v.produto.id === id);
    let vendaEstoqueAtt = this.vendas[index];

    vendaEstoqueAtt.valorUnitario = this.formBaseService.formBase.get('valorUnitario')?.value;
    vendaEstoqueAtt.quantidade = this.formBaseService.formBase.get('quantidade')?.value

    this.vendas[index] = vendaEstoqueAtt;
    this.limparCamposForm();
    this.desabilitaBotoes();
    this.atualizarValores();
  }

  deleteProduto(): void{
    let id = this.formBaseService.formBase.get('idEstoque')?.value;

    let index = this.vendas.findIndex((v) => v.produto.id === id);
    console.log(`index do item é ${index}`);
    this.vendas.splice(index, 1);

    this.tableBaseComponent!.refreshDataSource(this.vendas);
    this.limparCamposForm();
    this.desabilitaBotoes();
    this.atualizarValores();
    this.atualizarIndice();
  }

  atualizarIndice(): void{
    for (let index = 0; index < this.vendas.length; index++) {
      this.vendas[index].id = index + 1;
    }
  }

  limparCamposForm(): void{
    this.formBaseService.resetarCampos();
    this.produtoControl.reset();
    this.codigo = "Codigo";
    this.unidade = "Unidade";
    this.produto = null;
    this.quantidadeDisponivel = "Qnt. Disponivel";
    this.dataControl.reset();
  }

  atualizarValores(): void{
    this.valorTotal = this.vendas.reduce((acumulador, vendaAtual) => {
      return acumulador + (vendaAtual.valorUnitario * vendaAtual.quantidade);
    }, 0);
  }

  recuperarDadosDoClique(vendaEstoque: VendaEstoqueTable){
    this.formBaseService.formBase.patchValue({
      valorUnitario: vendaEstoque.valorUnitario,
      quantidade: vendaEstoque.quantidade,
      idEstoque: vendaEstoque.produto.id,
      idCompras: this.venda!.id,
      dataEstoque: vendaEstoque.dataEstoque
    })

    this.codigo = vendaEstoque.produto.id.toString();
    this.unidade = vendaEstoque.produto.unidade;
    this.produtoControl.setValue(vendaEstoque.produto);
    this.produto = vendaEstoque.produto;

    this.quantidadeDisponivel = vendaEstoque.dataEstoque.quantidade.toString();
    this.dataControl.setValue(vendaEstoque.dataEstoque);
    
    this.vendaEstoqueService.getDatesWithProduct(this.produto!.id).subscribe((datas) => {
      this.datasEstoque = datas;
    })

    this.habilitaBotoes();
  }

  habilitaBotoes(): void{
    this.podeEditar = true;
    this.podeExcluir = true;
  }
  
  desabilitaBotoes(): void{
    this.podeEditar = false;
    this.podeExcluir = false;
  }

  finalizarCompra(): void{
    this.openDialog();
  }

  openDialog(): void{
    let tamWidth = window.innerWidth * 0.40;
    let tamHeigth = window.innerHeight * 0.60;

    let dialogRef = this.dialog.open(ModalFinalizarComponent, {
      width: `${tamWidth}px`,
      height: `${tamHeigth}px`,
      data:{
        texto: 'Venda',
        numero: this.venda!.id,
        total: this.valorTotal.toFixed(2),
        arquivo: true
      }
    })
  }

  criarVenda(): void{
    
  }
}
