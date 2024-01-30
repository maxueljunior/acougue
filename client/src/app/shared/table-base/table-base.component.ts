import { AfterViewInit, Component, ElementRef, EventEmitter, HostListener, Input, OnChanges, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';
import { MatPaginator, MatPaginatorIntl, PageEvent } from '@angular/material/paginator';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import { Fornecedor, IFornecedor } from 'src/app/core/types/Fornecedor';
import { FormBaseService } from '../service/form-base.service';
import { MatDialog } from '@angular/material/dialog';
import { ModalCriacaoComponent } from '../../fornecedor/modal-criacao/modal-criacao.component';
import { ModalExclusaoComponent } from '../modal-exclusao/modal-exclusao.component';
import { CustomPaginatorIntl } from 'src/app/core/utils/CustomPaginatorIntl';
import { Responsivo } from 'src/app/core/types/Types';
import { debounceTime, fromEvent, map, startWith } from 'rxjs';

@Component({
  selector: 'app-table-base',
  templateUrl: './table-base.component.html',
  styleUrls: ['./table-base.component.scss'],
  providers: [
    {
      provide: MatPaginatorIntl,
      useClass: CustomPaginatorIntl
    }
  ]
})
export class TableBaseComponent implements AfterViewInit, OnChanges, OnInit{


  @Input() displayedColumns: string[] = [];
  @Input() displayedesColumns: string[] = [];

  @Input() dados!: any[];
  @Input() pageable: any | null | undefined;
  @Input() visualizacaoDeAdicao: boolean = false;
  @Input() contemEdicao: boolean = true;

  @Output() clickNaTabela = new EventEmitter<any>();
  @Output() alteracaoPagina = new EventEmitter<PageEvent>();
  @Output() edicao = new EventEmitter<any>();
  @Output() exclusao = new EventEmitter<any>();
  @Output() download = new EventEmitter<any>();

  // Os atributos abaixo tem como objetivo de tentar transformar a tabela em responsiva....
  @Input() colunas!: Responsivo[];
  @Input() colunasResponsivas!: Responsivo[];
  // Fim responsividade
  @ViewChild(MatPaginator) paginator!: MatPaginator;

  dataSource = new MatTableDataSource<any>([]);
  clickedRows = new Set<any>();
  size: number = 0;
  tamanhoTela!: number;
  telaResponsiva: boolean = true;

  constructor(
    private formularioService: FormBaseService,
    public dialog: MatDialog
  ){

  }

  getNestedPropertyValue(obj: any, path: string): any {
    return path.split('.').reduce((acc, current) => acc[current], obj);
  }

  ngOnInit(): void {
    this.tamanhoTela = window.innerWidth;
    if(this.tamanhoTela >= 769){
      this.telaResponsiva = true;
    }else{
      this.telaResponsiva = false;
    }
  }

  @HostListener('window:resize', ['$event'])
  onResize(event: Event) {
    this.tamanhoTela = window.innerWidth;
    if(this.tamanhoTela >= 769){
      this.telaResponsiva = true;
    }else{
      this.telaResponsiva = false;
    }

    console.log(this.telaResponsiva);
  }

  ngAfterViewInit() {
    if(!this.visualizacaoDeAdicao){
      this.paginator._intl.itemsPerPageLabel = 'Registros por pagina';
      this.paginator?.['page'].subscribe((event: PageEvent) => {
        this.alteracaoPagina.emit(event);
      });
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes?.['dados'] && changes?.['dados'].currentValue) {
      const dados = changes?.['dados'].currentValue;
      if (dados.length > 0) {
        this.dataSource.data = dados;
      }
      if(changes?.['pageable'] && changes?.['pageable'].currentValue){
        const pageable = changes?.['pageable'].currentValue;
        this.size = pageable?.['totalElements'];
        if(this.size === undefined || this.size === null || this.size === 0){
          this.size = pageable?.['page']?.['totalElements'];
        }
      }
    }
  }

  editar(data: any){
    this.edicao.emit(data);
  }

  excluir(data: any){
    this.exclusao.emit(data);
  }

  baixarArquivo(data: any){
    this.download.emit(data._links.download.href);
  }

  refreshDataSource(dados: any): void {
    this.dataSource.data = dados;
    this.dataSource._updateChangeSubscription(); // Garante que as alterações são detectadas
  }

  clicouNaTabela(event: any): void{
    this.clickNaTabela.emit(event);
  }
}

