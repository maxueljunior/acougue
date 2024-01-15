import { Component, EventEmitter, OnInit, Output} from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { debounceTime} from 'rxjs';
import { ModalCriacaoComponent } from 'src/app/shared/modal-criacao/modal-criacao.component';
import { FormBaseService } from 'src/app/shared/service/form-base.service';

const PAUSA = 300;

@Component({
  selector: 'app-busca',
  templateUrl: './busca.component.html',
  styleUrls: ['./busca.component.scss'],
})
export class BuscaComponent implements OnInit{

  campoBusca = new FormControl();
  formFornecedor: FormGroup;
  @Output() criar = new EventEmitter<boolean>();
  // @Output() buscaRazaoSocial = new EventEmitter<FormControl>();
  @Output() buscaRazaoSocial = new EventEmitter<string>();

  constructor(
    private formBaseService: FormBaseService,
    public dialog: MatDialog){
    this.formFornecedor = this.formBaseService.criarFormulario();
    this.formFornecedor = this.formBaseService.adicionaCamposFornecedor(this.formFornecedor);
  }

  ngOnInit(): void {
    this.campoBusca.valueChanges
      .pipe(debounceTime(300))
      .subscribe((valorDigitado) => {
        this.buscaRazaoSocial.emit(valorDigitado);
      });
  }

  openDialog(): void {
    let tamWidth = window.innerWidth * 0.40;
    let tamHeigth = window.innerHeight * 0.80;

    let diaglogRef = this.dialog.open(ModalCriacaoComponent, {
      width: `${tamWidth}px`,
      height: `${tamHeigth}px`,
      data: {
        editar: false
      }
    });

    diaglogRef.componentInstance.criacao.subscribe((resposta) => {
      if(resposta === true){
        this.criar.emit(resposta);
      }
    })
  }
}
