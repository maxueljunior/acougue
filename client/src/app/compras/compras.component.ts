import { Component, ElementRef, Renderer2, ViewChild, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { CompraEstoque, CompraEstoqueTable } from '../core/types/ComprasEstoque';
import { Responsivo } from '../core/types/Types';
import { MatDialog } from '@angular/material/dialog';
import { ModalProcuraComponent } from '../shared/modal-procura/modal-procura.component';
import { Observable, map, startWith, Subscription, debounceTime } from 'rxjs';
import { FornecedorService } from '../fornecedor/service/fornecedor.service';
import { Fornecedor } from '../core/types/Fornecedor';
import { CompraService } from './service/compra.service';
import { Compras, ICompras } from '../core/types/Compras';
import { Produto } from '../core/types/Produto';
import { ProdutoService } from '../produtos/service/produto.service';
import { FormBaseService } from '../shared/service/form-base.service';
import { TableBaseComponent } from '../shared/table-base/table-base.component';
import { ModalFinalizarComponent } from '../shared/modal-finalizar/modal-finalizar.component';
import { CompraEstoqueService } from './service/compra-estoque.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { SnackMensagemComponent } from '../shared/snack-mensagem/snack-mensagem.component';
import { PageEvent } from '@angular/material/paginator';
import { MatAutocomplete } from '@angular/material/autocomplete';

@Component({
  selector: 'app-compras',
  templateUrl: './compras.component.html',
  styleUrls: ['./compras.component.scss']
})
export class ComprasComponent implements OnInit{

  compras: CompraEstoqueTable[] = [];

  displayedColumns: string[] = ['id', 'produto.descricao', 'quantidade', 'precoUnitario'];
  displayedesColumns: string[] = ['id', 'produto.descricao', 'quantidade', 'precoUnitario'];

  selectedFileName: string = 'Nenhum arquivo selecionado';
  @ViewChild('fileInput') fileInput: ElementRef | undefined;

  @ViewChild('tableBaseComponent') tableBaseComponent: TableBaseComponent | undefined;

  criarCompra: boolean = false;
  arquivoAnexado: boolean = false;
  arquivoFinalizado: boolean = false;
  value: number = 0;

  myControl = new FormControl<null | Fornecedor>(null, Validators.required);
  fornecedorControlBusca = new FormControl();

  filteredOptions!: Observable<Fornecedor[]>;
  filteredOptionsSearch!: Observable<Fornecedor[]>;

  produtoControl = new FormControl<null | Produto>(null, Validators.required);
  filteredOptionsProduto!: Observable<Produto[]>;

  fornecedores: Fornecedor[] = [];
  fornecedorSubscription: Subscription = new Subscription();

  produtos: Produto[] = [];
  produtoSubscription: Subscription = new Subscription();

  comprasRecuperadas: Compras[] = [];
  compraSubscription: Subscription = new Subscription();

  pageable?: ICompras | null;
  pageableSubscription: Subscription = new Subscription();

  compra!: Compras | null;
  produto!: Produto | null;
  unidade: string = "Unidade";
  codigo: string = "Codigo";
  valorTotal: number = 0;
  produtoExistente: boolean = false;
  podeEditar: boolean = false;
  podeExcluir: boolean = false;
  pageIndex: number = 0;
  pageSize: number = 10;
  razaoSocial: string = '';

  formCompraEstoque: FormGroup;

  colunas: Responsivo[] = [
    {nome: "Nº", atributo: "id"},
    {nome: "Descrição", atributo: "produto.descricao"},
    {nome: "Qnt.", atributo: "quantidade"},
    {nome: "Valor Unit. (R$)", atributo: "precoUnitario"},
  ]

  colunasResponsiva: Responsivo[] = [
    {nome: "Nº", atributo: "id"},
    {nome: "Descrição", atributo: "produto.descricao"},
    {nome: "Qnt.", atributo: "quantidade"},
    {nome: "Valor Unit. (R$)", atributo: "precoUnitario"},
  ]

  displayedColumnsComp: string[] = ['id', 'fornecedor.razaoSocial', 'fornecedor.cnpj', 'data' ,'valorTotal','download', 'acoes'];
  displayedesColumnsComp: string[] = ['id', 'fornecedor.razaoSocial', 'valorTotal','download', 'acoes'];

  colunasComp: Responsivo[] = [
    {nome: "Cod.", atributo: "id"},
    {nome: "Razão Fornecedor", atributo: "fornecedor.razaoSocial"},
    {nome: "Valor Total (R$)", atributo: "valorTotal"},
  ];

  colunasResponsivaComp: Responsivo[] = [
    {nome: "Cod.", atributo: "id"},
    {nome: "Razão Fornecedor", atributo: "fornecedor.razaoSocial"},
    {nome: "CNPJ", atributo: "fornecedor.cnpj"},
    {nome: "Data", atributo: "data"},
    {nome: "Valor Total (R$)", atributo: "valorTotal"},
  ]

  constructor(
    private renderer: Renderer2,
    public dialog: MatDialog,
    private fornecedorService: FornecedorService,
    private compraService: CompraService,
    private compraEstoqueService: CompraEstoqueService,
    private produtoService: ProdutoService,
    public formBaseService: FormBaseService,
    private snackBar: MatSnackBar
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

    this.compraSubscription = this.compraService.compras$.subscribe((c) => {
      this.comprasRecuperadas = c;
      // console.log(this.comprasRecuperadas);
    })

    this.pageableSubscription=this.compraService.pageable$.subscribe((p) => {
      this.pageable = p;
      // console.log(this.pageable);
    })

    this.compraService.findAll(this.pageIndex, this.pageSize, this.razaoSocial);

    this.fornecedorControlBusca.valueChanges
      .pipe(debounceTime(300))
      .subscribe((valorDigitado) => {
        this.razaoSocial = valorDigitado;
        this.compraService.findAll(this.pageIndex, this.pageSize, this.razaoSocial);
      });
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

  alteracaoNaPagina(event: PageEvent){
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.compraService.findAll(this.pageIndex, this.pageSize, this.razaoSocial);
  }

  downloadArquivo(event: any){
    this.compraService.downloadArchive(event);
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

      this.compraService.uploadArchive(file, this.compra!.id).subscribe({
        next: (u) => {
          let interval = setInterval(() => {
            this.value += 1;
            if(this.value === 100){
              this.arquivoAnexado = false;
              this.arquivoFinalizado = true;
              this.selectedFileName = file.name;
              clearInterval(interval);
            }
          }, 5);
        },
        error: (err) => {
          console.log(err);
        }
      });
    }
  }

  openDialog(): void{
    let tamWidth = window.innerWidth * 0.40;
    let tamHeigth = window.innerHeight * 0.60;

    let dialogRef = this.dialog.open(ModalFinalizarComponent, {
      width: `${tamWidth}px`,
      height: `${tamHeigth}px`,
      data:{
        texto: 'Compra',
        numero: this.compra?.id,
        total: this.valorTotal,
        arquivo: this.arquivoFinalizado
      }
    })

    dialogRef.componentInstance.finalizar.subscribe((p) => {
      if(p === true){

        let comprasConvertidas = this.compras.map((c) => ({
          precoUnitario: c.precoUnitario,
          quantidade: c.quantidade,
          idCompras: this.compra?.id,
          idEstoque: c.produto.id
        }) as CompraEstoque)

        this.compraEstoqueService.create(comprasConvertidas).subscribe({
          next: (c) => {
            console.log(c);
            this.openSnackBar('Compra finalizada com sucesso!', 'sucesso');
            this.limparTela();
          },
          error: (err) => {
            this.verificaStatusErro(err.status);
          }
        });
      }
    })
  }

  addProdutos(): void {
    this.formBaseService.formBase.patchValue({
      idCompras: this.compra!.id
    })

    // // pegar os dados para fazer a inserção no banco de dados
    // console.log(this.formBaseService.formBase.value);
    // console.log(this.produtoControl.value);

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
      // console.log(this.produtoExistente);
    }
  }

  updateProduto(): void{
    let id = this.formBaseService.formBase.get('idEstoque')?.value;

    let index = this.compras.findIndex((c) => c.produto.id === id);
    let compraEstoqueAtt = this.compras[index];

    compraEstoqueAtt.precoUnitario = this.formBaseService.formBase.get('precoUnitario')?.value;
    compraEstoqueAtt.quantidade = this.formBaseService.formBase.get('quantidade')?.value

    this.compras[index] = compraEstoqueAtt;
    this.limparCamposForm();
    this.desabilitaBotoes();
    this.atualizarValores();
  }

  deleteProduto(): void{
    let id = this.formBaseService.formBase.get('idEstoque')?.value;

    let index = this.compras.findIndex((c) => c.produto.id === id);
    console.log(`index do item é ${index}`);
    this.compras.splice(index, 1);

    console.log(this.compras);

    this.tableBaseComponent!.refreshDataSource(this.compras);
    this.limparCamposForm();
    this.desabilitaBotoes();
    this.atualizarValores();
    this.atualizarIndice();
  }

  finalizarCompra(): void{
    this.openDialog();
  }

  verificaExistente(compraEstoqueTabela: CompraEstoqueTable): boolean{
    let index = this.compras.findIndex((c) => c.produto.id === compraEstoqueTabela.produto.id);
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

  limparTela(): void{
    this.valorTotal = 0;
    this.compra = null;
    this.compras = [];
    this.myControl.reset();
    this.criarCompra = false;
    this.limparCamposForm();
  }

  recuperarDadosDoClique(compraEstoque: CompraEstoqueTable){
    this.formBaseService.formBase.patchValue({
      precoUnitario: compraEstoque.precoUnitario,
      quantidade: compraEstoque.quantidade,
      idEstoque: compraEstoque.produto.id,
      idCompras: this.compra!.id
    })

    this.codigo = compraEstoque.produto.id.toString();
    this.unidade = compraEstoque.produto.unidade;
    this.produtoControl.setValue(compraEstoque.produto);

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

  atualizarIndice(): void{
    for (let index = 0; index < this.compras.length; index++) {
      this.compras[index].id = index + 1;
    }
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

  limpar(event: FormControl){
    event.reset();
  }
}


