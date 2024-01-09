import { AfterViewInit, Component, EventEmitter, Input, OnChanges, Output, SimpleChanges, ViewChild } from '@angular/core';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { Fornecedor, IFornecedor } from 'src/app/core/types/Fornecedor';

@Component({
  selector: 'app-table-base',
  templateUrl: './table-base.component.html',
  styleUrls: ['./table-base.component.scss']
})
export class TableBaseComponent implements AfterViewInit, OnChanges{


  displayedColumns: string[] = ['id', 'razaoSocial', 'cnpj', 'nomeContato', 'telefone', 'acoes'];

  @Input() fornecedores!: Fornecedor[];
  @Input() pageable: IFornecedor | null | undefined;
  @Output() alteracaoPagina = new EventEmitter<PageEvent>();

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  dataSource = new MatTableDataSource<Fornecedor>([]);
  size: number = 0;

  ngAfterViewInit() {
    // this.dataSource.paginator = this.paginator;
    this.paginator.page.subscribe((event: PageEvent) => {
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

  pegarDados(dataSource: any){
    console.log(dataSource);
  }
}

