import { NumberInput } from '@angular/cdk/coercion';
import { AfterViewChecked, AfterViewInit, Component, Input, OnChanges, SimpleChanges, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { Fornecedor, IFornecedor } from 'src/app/core/types/Fornecedor';

@Component({
  selector: 'app-table-base',
  templateUrl: './table-base.component.html',
  styleUrls: ['./table-base.component.scss']
})
export class TableBaseComponent implements AfterViewInit, OnChanges{

  displayedColumns: string[] = ['id', 'razaoSocial', 'cnpj', 'nomeContato', 'telefone'];

  @Input() fornecedores!: IFornecedor[];

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  dataSource = new MatTableDataSource<Fornecedor>([]);
  size: number = 0;

  ngAfterViewInit() {
    // this.dataSource.paginator = this.paginator;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes?.['fornecedores'] && changes?.['fornecedores'].currentValue) {
      const fornecedores = changes?.['fornecedores'].currentValue;
      if (fornecedores.length > 0) {
        this.dataSource.data = fornecedores[0].content;
        this.size = fornecedores[0].totalElements;
      }
    }
    console.log('alguma coisa alterou');
  }
}

