import { DatasProdutos, Produto } from './../core/types/Produto';
import { Component, OnInit, ViewChild } from "@angular/core";
import { ClienteService } from "../clientes/service/cliente.service";
import { FormControl, FormGroup, Validators } from "@angular/forms";
import { Cliente } from "../core/types/Cliente";
import { Observable, Subscription, debounceTime, map, startWith } from "rxjs";
import { FormBaseService } from "../shared/service/form-base.service";
import { ProdutoService } from '../produtos/service/produto.service';
import { VendaEstoqueService } from './service/venda-estoque.service';
import { TableBaseComponent } from '../shared/table-base/table-base.component';
import { Responsivo } from '../core/types/Types';
import { VendasDTO, Vendas } from '../core/types/Vendas';
import { InsertVendaEstoque, VendaEstoqueTable } from '../core/types/VendasEstoque';
import { ModalFinalizarComponent } from '../shared/modal-finalizar/modal-finalizar.component';
import { MatDialog } from '@angular/material/dialog';
import { VendaService } from './service/venda.service';
import { SnackMensagemComponent } from '../shared/snack-mensagem/snack-mensagem.component';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatOptionSelectionChange } from '@angular/material/core';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { PageEvent } from '@angular/material/paginator';
import { ModalProcuraComponent } from '../shared/modal-procura/modal-procura.component';
import { ModalExclusaoComponent } from '../shared/modal-exclusao/modal-exclusao.component';

@Component({
  selector: 'app-vendas',
  templateUrl: './vendas.component.html',
  styleUrls: ['./vendas.component.scss']
})
export class VendasComponent implements OnInit{

  vendas: VendaEstoqueTable[] = [];
  venda!: VendasDTO | null;

  @ViewChild('tableBaseComponent') tableBaseComponent: TableBaseComponent | undefined;

  displayedColumns: string[] = ['id', 'produto.descricao', 'quantidade', 'valorUnitario'];
  displayedesColumns: string[] = ['id', 'produto.descricao', 'quantidade', 'valorUnitario'];

  myControl = new FormControl<null | Cliente>(null, Validators.required);
  condPagamentoControl = new FormControl<null | string>(null, Validators.required);

  clientesBuscaControl = new FormControl();
  
  criarVendas: boolean = false;

  clientes: Cliente[] = [];
  clienteSubscription: Subscription = new Subscription();

  produtos: Produto[] = [];
  produtoSubscription: Subscription = new Subscription();
  produto!: Produto | null;

  vendasRecuperadas: VendasDTO[] = [];
  vendaSubscription: Subscription = new Subscription();

  pageable: Vendas | null = null;
  pageableSubscription: Subscription = new Subscription();

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

  pageIndex: number = 0;
  pageSize: number = 10;
  nome: string = '';
  gerarCupom: boolean = false;

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


  displayedColumnsVendas: string[] = ['id', 'cliente.nome', 'cliente.sobrenome', 'dataVenda' ,'valorTotal','download', 'acoes'];
  displayedesColumnsVendas: string[] = ['id', 'cliente.nome', 'cliente.sobrenome', 'valorTotal','download', 'acoes'];

  colunasVendas: Responsivo[] = [
    {nome: "Cod.", atributo: "id"},
    {nome: "Nome", atributo: "cliente.nome"},
    {nome: "Sobrenome", atributo: "cliente.sobrenome"},
    {nome: "Valor Total (R$)", atributo: "valorTotal"},
  ];

  colunasResponsivaVendas: Responsivo[] = [
    {nome: "Cod.", atributo: "id"},
    {nome: "Nome", atributo: "cliente.nome"},
    {nome: "Sobrenome", atributo: "cliente.sobrenome"},
    {nome: "Data", atributo: "dataVenda"},
    {nome: "Valor Total (R$)", atributo: "valorTotal"},
  ]

  constructor(
    private clienteService: ClienteService,
    public formBaseService: FormBaseService,
    private produtoService: ProdutoService,
    private vendaEstoqueService: VendaEstoqueService,
    private dialog: MatDialog,
    private vendaService: VendaService,
    private snackBar: MatSnackBar
  ){
    this.formVendasEstoque = this.formBaseService.criarFormulario();
    this.formVendasEstoque = this.formBaseService.adicionaCamposVendasEstoque(this.formVendasEstoque);
  }
  ngOnInit(): void {
    this.clienteSubscription = this.clienteService.clientes$.subscribe((c) =>{
      // console.log(c);
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
      // console.log(p);
    })

    this.produtoService.findAll(0, 9999, '');

    this.filteredOptionsProduto = this.produtoControl.valueChanges.pipe(
      startWith(''),
      map(value => {
        const name = typeof value === 'string' ? value : value?.descricao;
        return name ? this._filterPn(name as string) : this.produtos.slice();
      }),
    );

    this.clientesBuscaControl.valueChanges
      .pipe(debounceTime(300))
      .subscribe((valorDigitado) => {
        this.nome = valorDigitado;
        this.vendaService.findAll(this.pageIndex, this.pageSize, this.nome);
      });

    this.vendaSubscription = this.vendaService.$vendas.subscribe((v) => {
      this.vendasRecuperadas = v;
    })

    this.pageableSubscription = this.vendaService.$pageable.subscribe((p) => {
      this.pageable = p;
    })
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
      this.desabilitaBotoes();
      return true;
    }

    this.habilitaBotoes();
    return false;
  }

  addProdutos(): void {
    this.formBaseService.formBase.patchValue({
      idVendas: this.venda!.id
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
    // console.log(`index do item é ${index}`);
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
    this.gerarCupom = false;
    let tamWidth = window.innerWidth * 0.40;
    let tamHeigth = window.innerHeight * 0.60;

    let dialogRef = this.dialog.open(ModalFinalizarComponent, {
      width: `${tamWidth}px`,
      height: `${tamHeigth}px`,
      data:{
        texto: 'Venda',
        numero: this.venda!.id,
        total: this.valorTotal.toFixed(2),
        arquivo: true,
        gerarCupom: true
      }
    })

    dialogRef.componentInstance.gerarCupom.subscribe((g) => {
      if(g === true){
        this.gerarCupom = true;
      }
    })

    dialogRef.componentInstance.finalizar.subscribe((p) => {
      if(p === true){

        let vendasConvertidas = this.vendas.map((v) => ({
          idEstoque: v.produto.id,
          idVendas: this.venda!.id,
          quantidade: v.quantidade,
          valorUnitario: v.valorUnitario,
          dataEstoque: v.dataEstoque.dataCompra
        }) as InsertVendaEstoque);

        this.vendaEstoqueService.create(vendasConvertidas).subscribe({
          next: (v) => {
            if(this.gerarCupom){
              this.vendaService.downloadArchive(`api/vendas/gerar-cupom/${v[0].idVendas}`);
            }
            this.openSnackBar('Venda finalizada com sucesso!', 'sucesso');
            this.limparTela();
          },
          error: (err) => {
            this.verificaStatusErro(err.status);
          }
        })
      }
    })

    
  }

  criarVenda(): void{
    let cliente: Cliente = this.myControl.value as Cliente;
    let condicaoPagamento = this.condPagamentoControl.value!;
    this.vendaService.create(cliente.id, condicaoPagamento).subscribe((v) => {
      this.venda = v;
      this.criarVendas = true;
    });
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

  verificaStatusErro(statusErro: number): void{
    if(statusErro === 403){
      this.openSnackBar('Sua sessão expirou!', 'falha')
    }else{
      this.openSnackBar('Ocorreu um erro inesperado!', 'falha')
    }
  }

  limparTela(): void{
    this.valorTotal = 0;
    this.venda = null;
    this.vendas = [];
    this.myControl.reset();
    this.condPagamentoControl.reset();
    this.criarVendas = false;
    this.limparCamposForm();
  }

  selecionaClienteBusca(){
    // console.log(this.clientesBuscaControl.value)
  }

  trocaDeTab(event: MatTabChangeEvent): void{
    if(event.index === 1){
      // console.log(`Index -> ${this.pageIndex}, Size -> ${this.pageSize}, Nome -> ${this.nome}`);
      this.vendaService.findAll(this.pageIndex, this.pageSize, this.nome);
    }
  }

  alteracaoNaPagina(event: PageEvent){
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.vendaService.findAll(this.pageIndex, this.pageSize, this.nome);
  }

  downloadArquivo(event: any){
    this.vendaService.downloadArchive(event);
  }

  abrirVendas(event: VendasDTO){

    this.vendaEstoqueService.getProductsWithVendas(event.id).subscribe({
      next: (ve) => {
        // console.log(ve);

        let vendaTable = ve.content.map((v) => ({
          id: v.id,
          produto: this.produtos.find((p) => p.id === v.idEstoque),
          valorUnitario: v.valorUnitario,
          quantidade: v.quantidade
        }) as VendaEstoqueTable);

        this.openDialogRelacaoProdutos(event, vendaTable);
      },
      error: (err) => {
        console.log(err);
      }
    })
  }

  openDialogRelacaoProdutos(vendas: VendasDTO, vendasEstoque: VendaEstoqueTable[]): void{
    let tamWidth = window.innerWidth * 0.60;
    let tamHeigth = window.innerHeight * 0.80;

    let contemNotaFiscal = "Sim";
    if(vendas._links?.download?.href === undefined){
      contemNotaFiscal = "Não";
    }

    this.dialog.open(ModalProcuraComponent, {
      width: `${tamWidth}px`,
      // height: `${tamHeigth}px`,
      data:{
        dadosLado1: [
          {nome: "Codigo Venda", attributo: vendas.id},
          {nome: "Cliente", attributo: vendas.cliente.nome},
          {nome: "Sobrenome", attributo: vendas.cliente.nome},
        ],
        dadosLado2: [
          {nome: "Data", attributo: vendas.dataVenda},
          {nome: "Valor Total (R$)", attributo: vendas.valorTotal},
          {nome: "Cupom não Fiscal", attributo: vendas._links?.download?.href},
        ],
        dados: vendasEstoque,
        colunas: this.colunas,
        colunasResponsivas: this.colunasResponsiva,
        displayedColumns: this.displayedColumns,
        displayedesColumns: this.displayedesColumns
      }
    })
  }

  excluirVendas(event: any): void{
    let dialogRef = this.dialog.open(ModalExclusaoComponent, {
      data:{
        titulo: `Venda nº ${event.id}`
      }
    });

    dialogRef.componentInstance.exclusao.subscribe((e) => {
      if(e === true){
        this.vendaService.delete(event.id, this.pageIndex, this.pageSize, this.nome);
        // this.compraService.delete(event.id, this.pageIndex, this.pageSize, this.razaoSocial);
      }
    })
  }
}
