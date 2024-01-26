import { Component, ElementRef, Renderer2, ViewChild, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { CompraEstoque, CompraEstoqueTable } from '../core/types/ComprasEstoque';
import { Responsivo } from '../core/types/Types';
import { MatDialog } from '@angular/material/dialog';
import { ModalProcuraComponent } from '../shared/modal-procura/modal-procura.component';
import { Observable, map, startWith, Subscription } from 'rxjs';
import { FornecedorService } from '../fornecedor/service/fornecedor.service';
import { Fornecedor } from '../core/types/Fornecedor';
import { CompraService } from './service/compra.service';
import { Compras } from '../core/types/Compras';
import { Produto } from '../core/types/Produto';
import { ProdutoService } from '../produtos/service/produto.service';
import { FormBaseService } from '../shared/service/form-base.service';
import { TableBaseComponent } from '../shared/table-base/table-base.component';

@Component({
  selector: 'app-compras',
  templateUrl: './compras.component.html',
  styleUrls: ['./compras.component.scss']
})
export class ComprasComponent implements OnInit{

  // produtoteste: Produto = {
  //   id: 1,
  //   descricao: 'MAMINHA',
  //   unidade: 'KG',
  //   totalQuantidade: 50
  // }

  compras: CompraEstoqueTable[] = [
    // {id:0, produto: this.produtoteste, quantidade:2, precoUnitario: 5.35}
  ];

  displayedColumns: string[] = ['id', 'produto.descricao', 'quantidade', 'precoUnitario'];
  displayedesColumns: string[] = ['id', 'produto.descricao', 'quantidade', 'precoUnitario'];

  selectedFileName: string = 'Nenhum arquivo selecionado';
  @ViewChild('fileInput') fileInput: ElementRef | undefined;

  @ViewChild('tableBaseComponent') tableBaseComponent: TableBaseComponent | undefined;

  criarCompra: boolean = false;
  arquivoAnexado: boolean = false;
  arquivoFinalizado: boolean = false;
  value: number = 0;

  // myControl = new FormControl<string | Fornecedor>('');
  myControl = new FormControl<null | Fornecedor>(null, Validators.required);
  filteredOptions!: Observable<Fornecedor[]>;

  produtoControl = new FormControl<null | Produto>(null, Validators.required);
  filteredOptionsProduto!: Observable<Produto[]>;

  fornecedores: Fornecedor[] = [];
  fornecedorSubscription: Subscription = new Subscription();

  produtos: Produto[] = [];
  produtoSubscription: Subscription = new Subscription();

  compra!: Compras;
  produto!: Produto | null;
  unidade: string = "Unidade";
  codigo: string = "Codigo";
  valorTotal: number = 0;
  produtoExistente: boolean = false;

  formCompraEstoque: FormGroup;

  colunas: Responsivo[] = [
    {nome: "Nº", atributo: "id"},
    // {nome: "Produto", atributo: "idEstoque"},
    {nome: "Descrição", atributo: "produto.descricao"},
    {nome: "Qnt.", atributo: "quantidade"},
    {nome: "Valor Unit. (R$)", atributo: "precoUnitario"},
  ]

  colunasResponsiva: Responsivo[] = [
    {nome: "Nº", atributo: "id"},
    // {nome: "Produto", atributo: "idEstoque"},
    {nome: "Descrição", atributo: "produto.descricao"},
    {nome: "Qnt.", atributo: "quantidade"},
    {nome: "Valor Unit. (R$)", atributo: "precoUnitario"},
  ]

  constructor(
    private renderer: Renderer2,
    public dialog: MatDialog,
    private fornecedorService: FornecedorService,
    private compraService: CompraService,
    private produtoService: ProdutoService,
    public formBaseService: FormBaseService
  ){
    this.formCompraEstoque = this.formBaseService.criarFormulario();
    this.formCompraEstoque = this.formBaseService.adicionaCamposComprasEstoque(this.formCompraEstoque);
  }

  ngOnInit() {

    this.fornecedorSubscription = this.fornecedorService.fornecedores$.subscribe((f) => {
      this.fornecedores = f;
    })

    this.fornecedorService.findAll(0, 9999, '');

    this.filteredOptions = this.myControl.valueChanges.pipe(
      startWith(''),
      map(value => {
        const name = typeof value === 'string' ? value : value?.razaoSocial;
        return name ? this._filter(name as string) : this.fornecedores.slice();
      }),
    );

    this.produtoSubscription = this.produtoService.produtos$.subscribe((p) => {
      this.produtos = p;
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

  displayFn(fornecedor: Fornecedor): string {
    return fornecedor && fornecedor.razaoSocial ? fornecedor.razaoSocial : '';
  }

  private _filter(razaoSocial: string): Fornecedor[] {
    const filterValue = razaoSocial.toLowerCase();

    return this.fornecedores.filter(option => option.razaoSocial.toLowerCase().includes(filterValue));
  }

  displayPd(produto: Produto): string {
    return produto && produto.descricao ? produto.descricao : '';
  }

  private _filterPn(descricao: string): Produto[] {
    const filterValue = descricao.toLowerCase();

    return this.produtos.filter(option => option.descricao.toLowerCase().includes(filterValue));
  }

  capturarDados(event: any): void{
    this.produto = this.produtoControl.value;
    this.codigo = this.produto!.id.toString();
    this.unidade = this.produto!.unidade;
    this.formBaseService.formBase.patchValue({
      idEstoque: this.codigo
    })
    this.produtoExistente = false;
  }

  criarCompras(): void{
    this.criarCompra = true;
    this.compraService.create(this.myControl.value).subscribe({
      next: (c) => {
        this.compra = c;
      }
    });
  }

  selectFile(): void {
    if (this.fileInput) {
      this.renderer.selectRootElement(this.fileInput.nativeElement).click();
    }
  }

  onFileSelected(event: any): void {
    const file: File = event.target.files[0];

    if (file) {
      this.value = 0;
      this.arquivoFinalizado = false;
      console.log('Arquivo selecionado:', file);

      this.arquivoAnexado = true;

      let interval = setInterval(() => {
        this.value += 1;
        if(this.value === 100){
          this.arquivoAnexado = false;
          this.arquivoFinalizado = true;
          this.selectedFileName = file.name;
          clearInterval(interval);
        }
      }, 5);
    }
  }

  openDialog(): void{
    let tamWidth = window.innerWidth * 0.40;
    let tamHeigth = window.innerHeight * 0.60;
    const dialogRef = this.dialog.open(ModalProcuraComponent, {
      width: `${tamWidth}px`,
      height: `${tamHeigth}px`
    })
  }

  addProdutos(): void {
    this.formBaseService.formBase.patchValue({
      idCompras: this.compra.id
    })

    // pegar os dados para fazer a inserção no banco de dados
    console.log(this.formBaseService.formBase.value);
    console.log(this.produtoControl.value);

    let id = this.compras.length + 1;
    let compraEstoqueTable: CompraEstoqueTable = {
      id: id,
      produto: this.produtoControl.value as Produto,
      quantidade: this.formBaseService.formBase.get('quantidade')?.value,
      precoUnitario: this.formBaseService.formBase.get('precoUnitario')?.value
    }

    if(!this.verificaExistente(compraEstoqueTable)){
      this.compras.push(compraEstoqueTable);
      this.tableBaseComponent!.refreshDataSource(this.compras);
      this.limparCamposForm();
      this.atualizarValores();
    }else{
      this.produtoExistente = true;
      console.log(this.produtoExistente);
    }

  }

  verificaExistente(compraEstoqueTabela: CompraEstoqueTable): boolean{
    let index = this.compras.findIndex((c) => c.produto.id === compraEstoqueTabela.produto.id);
    console.log(index);
    if(index === -1){
      return false;
    }

    return true;
  }

  atualizarValores(): void{
    this.valorTotal = this.compras.reduce((acumulador, compraAtual) => {
      return acumulador + (compraAtual.precoUnitario * compraAtual.quantidade);
    }, 0);
  }

  limparCamposForm(): void{
    this.formBaseService.resetarCampos();
    this.produtoControl.reset();
    this.codigo = "Codigo";
    this.unidade = "Unidade";
    this.produto = null;
  }

  recuperarDadosDoClique(compraEstoque: CompraEstoqueTable){
    console.log(`Antes ->`);
    console.log(this.formBaseService.formBase.value);
    this.formBaseService.formBase.patchValue({
      precoUnitario: compraEstoque.precoUnitario,
      quantidade: compraEstoque.quantidade,
      idEstoque: compraEstoque.produto.id
    })

    this.codigo = compraEstoque.produto.id.toString();
    this.unidade = compraEstoque.produto.unidade;
    this.produtoControl.setValue(compraEstoque.produto);

    console.log(`Depois ->`);
    console.log(this.formBaseService.formBase.value);
  }
}

