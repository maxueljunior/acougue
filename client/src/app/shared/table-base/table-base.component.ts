import { AfterViewInit, Component, ElementRef, EventEmitter, HostListener, Input, OnChanges, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';
import { MatPaginator, MatPaginatorIntl, PageEvent } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { Fornecedor, IFornecedor } from 'src/app/core/types/Fornecedor';
import { FormBaseService } from '../service/form-base.service';
import { MatDialog } from '@angular/material/dialog';
import { ModalCriacaoComponent } from '../modal-criacao/modal-criacao.component';
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
export class TableBaseComponent implements AfterViewInit, OnChanges{


  displayedColumns: string[] = ['id', 'razaoSocial', 'cnpj', 'nomeContato', 'telefone', 'acoes'];
  displayedesColumns: string[] = ['id', 'razaoSocial', 'acoes'];

  @Input() fornecedores!: Fornecedor[];
  @Input() pageable: IFornecedor | null | undefined;
  @Output() alteracaoPagina = new EventEmitter<PageEvent>();
  @Output() edicao = new EventEmitter<Fornecedor>();
  @Output() exclusao = new EventEmitter<Fornecedor>();

  // Os atributos abaixo tem como objetivo de tentar transformar a tabela em responsiva....
  @Input() colunas!: Responsivo[];
  @Input() colunasResponsivas!: Responsivo[];
  // Fim responsividade

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  dataSource = new MatTableDataSource<Fornecedor>([]);
  size: number = 0;
  tamanhoTela!: number;
  telaResponsiva: boolean = true;

  constructor(
    private formularioService: FormBaseService,
    public dialog: MatDialog
  ){

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
    this.paginator._intl.itemsPerPageLabel = 'Registros por pagina';
    this.paginator?.['page'].subscribe((event: PageEvent) => {
      this.alteracaoPagina.emit(event);
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes?.['fornecedores'] && changes?.['fornecedores'].currentValue) {
      const fornecedores = changes?.['fornecedores'].currentValue;
      if (fornecedores.length > 0) {
        this.dataSource.data = fornecedores;
      }
      if(changes?.['pageable'] && changes?.['pageable'].currentValue){
        const pageable = changes?.['pageable'].currentValue;
        this.size = pageable?.['totalElements'];
      }
    }
  }

  editar(dataSource: Fornecedor){
    this.formularioService.formBase.patchValue({
      razaoSocial: dataSource.razaoSocial,
      cnpj: dataSource.cnpj,
      nomeContato: dataSource.nomeContato,
      telefone: dataSource.telefone
    });
    this.openDialogEditar(dataSource);
  }

  excluir(dataSource: Fornecedor){
    this.openDialogExcluir(dataSource);
  }

  openDialogEditar(fornecedor: Fornecedor): void {
    let dialogRef = this.dialog.open(ModalCriacaoComponent, {
      width: '40%',
      height: '60%',
      data:{
        editar: true,
        fornecedor: fornecedor
      }
    });

    dialogRef.componentInstance.edicao.subscribe((dados) => {
      this.edicao.emit(dados);
    })
  }

  openDialogExcluir(fornecedor: Fornecedor): void {
    let dialogRef = this.dialog.open(ModalExclusaoComponent, {
      // width: '20%',
      // height: '40%',
      data:{
        fornecedor: fornecedor
      }
    });

    dialogRef.componentInstance.exclusao.subscribe((e) => {
      this.exclusao.emit(fornecedor);
    })
  }
}

