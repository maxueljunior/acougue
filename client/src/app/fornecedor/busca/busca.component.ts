import { Component, EventEmitter, Output } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Observable, debounceTime, distinctUntilChanged, filter, switchMap, tap } from 'rxjs';
import { ModalCriacaoComponent } from 'src/app/shared/modal-criacao/modal-criacao.component';
import { FormBaseService } from 'src/app/shared/service/form-base.service';

const PAUSA = 300;

@Component({
  selector: 'app-busca',
  templateUrl: './busca.component.html',
  styleUrls: ['./busca.component.scss']
})
export class BuscaComponent {

  campoBusca = new FormControl();
  formFornecedor: FormGroup;
  @Output() criar = new EventEmitter<boolean>();
  @Output() buscaRazaoSocial = new EventEmitter<FormControl>();

  constructor(
    private formBaseService: FormBaseService,
    public dialog: MatDialog){
    this.formFornecedor = this.formBaseService.criarFormulario();
    this.formFornecedor = this.formBaseService.adicionaCamposFornecedor(this.formFornecedor);
  }

  realizandoBusca(): void{
    this.buscaRazaoSocial.emit(this.campoBusca);
  }

  openDialog(): void {
    let diaglogRef = this.dialog.open(ModalCriacaoComponent, {
      width: '40%',
      height: '60%',
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
