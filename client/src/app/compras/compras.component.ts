import { Component, ElementRef, Renderer2, ViewChild } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { CompraEstoque } from '../core/types/ComprasEstoque';
import { Responsivo } from '../core/types/Types';

@Component({
  selector: 'app-compras',
  templateUrl: './compras.component.html',
  styleUrls: ['./compras.component.scss']
})
export class ComprasComponent {
  compras: CompraEstoque[] = [
    {id: 0, idEstoque:0, idCompras:0, quantidade:0, precoUnitario:0},
    {id: 0, idEstoque:0, idCompras:0, quantidade:0, precoUnitario:0},
    {id: 0, idEstoque:0, idCompras:0, quantidade:0, precoUnitario:0},
    {id: 0, idEstoque:0, idCompras:0, quantidade:0, precoUnitario:0},
    {id: 0, idEstoque:0, idCompras:0, quantidade:0, precoUnitario:0},
    {id: 0, idEstoque:0, idCompras:0, quantidade:0, precoUnitario:0},
    {id: 0, idEstoque:0, idCompras:0, quantidade:0, precoUnitario:0},
    {id: 0, idEstoque:0, idCompras:0, quantidade:0, precoUnitario:0},
    {id: 0, idEstoque:0, idCompras:0, quantidade:0, precoUnitario:0},
    {id: 0, idEstoque:0, idCompras:0, quantidade:0, precoUnitario:0},
    {id: 0, idEstoque:0, idCompras:0, quantidade:0, precoUnitario:0},
  ];
  displayedColumns: string[] = ['id', 'idEstoque', 'quantidade', 'precoUnitario'];
  displayedesColumns: string[] = ['id', 'idEstoque', 'quantidade', 'precoUnitario'];

  selectedFileName: string = '';
  @ViewChild('fileInput') fileInput: ElementRef | undefined;

  criarCompra: boolean = true;

  colunas: Responsivo[] = [
    {nome: "Nº", atributo: "id"},
    {nome: "Produto", atributo: "idEstoque"},
    {nome: "Qnt.", atributo: "quantidade"},
    {nome: "Valor Unit.", atributo: "precoUnitario"},
  ]

  colunasResponsiva: Responsivo[] = [
    {nome: "Nº", atributo: "id"},
    {nome: "Produto", atributo: "idEstoque"},
    {nome: "Qnt.", atributo: "quantidade"},
    {nome: "Valor Unit.", atributo: "precoUnitario"},
  ]

  constructor(
    private renderer: Renderer2
  ){

  }

  criarCompras(): void{
    this.criarCompra = true;
  }

  selectFile(): void {
    // Simula o clique no input de arquivo oculto
    if (this.fileInput) {
      this.renderer.selectRootElement(this.fileInput.nativeElement).click();
    }
  }

  onFileSelected(event: any): void {
    const file: File = event.target.files[0];

    if (file) {
      console.log('Arquivo selecionado:', file);

      // Atualiza o nome do arquivo exibido no campo
      this.selectedFileName = file.name;

      // Faça o que quiser com o arquivo aqui, como enviá-lo para o servidor, etc.
    }
  }
}

