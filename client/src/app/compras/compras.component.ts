import { Component, ElementRef, Renderer2, ViewChild, OnInit } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { CompraEstoque } from '../core/types/ComprasEstoque';
import { Responsivo } from '../core/types/Types';
import { MatDialog } from '@angular/material/dialog';
import { ModalProcuraComponent } from '../shared/modal-procura/modal-procura.component';
import { Observable, map, startWith, Subscription } from 'rxjs';
import { FornecedorService } from '../fornecedor/service/fornecedor.service';
import { Fornecedor } from '../core/types/Fornecedor';
import { CompraService } from './service/compra.service';
import { Compras } from '../core/types/Compras';

@Component({
  selector: 'app-compras',
  templateUrl: './compras.component.html',
  styleUrls: ['./compras.component.scss']
})
export class ComprasComponent implements OnInit{
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

  selectedFileName: string = 'Nenhum arquivo selecionado';
  @ViewChild('fileInput') fileInput: ElementRef | undefined;

  criarCompra: boolean = false;
  arquivoAnexado: boolean = false;
  arquivoFinalizado: boolean = false;
  value: number = 0;

  // myControl = new FormControl<string | Fornecedor>('');
  myControl = new FormControl<null | Fornecedor>(null);
  filteredOptions!: Observable<Fornecedor[]>;

  fornecedores: Fornecedor[] = [];
  fornecedorSubscription: Subscription = new Subscription();

  compra!: Compras;


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
    private renderer: Renderer2,
    public dialog: MatDialog,
    private fornecedorService: FornecedorService,
    private compraService: CompraService
  ){

  }

  ngOnInit() {

    this.fornecedorSubscription = this.fornecedorService.fornecedores$.subscribe((f) => {
      this.fornecedores = f;
      console.log(this.fornecedores);
    })

    this.fornecedorService.findAll(0, 9999, '');

    this.filteredOptions = this.myControl.valueChanges.pipe(
      startWith(''),
      map(value => {
        const name = typeof value === 'string' ? value : value?.razaoSocial;
        return name ? this._filter(name as string) : this.fornecedores.slice();
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
}

