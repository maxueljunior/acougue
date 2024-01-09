import { Component, EventEmitter, Output } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ModalCriacaoComponent } from 'src/app/shared/modal-criacao/modal-criacao.component';
import { FormBaseService } from 'src/app/shared/service/form-base.service';

@Component({
  selector: 'app-busca',
  templateUrl: './busca.component.html',
  styleUrls: ['./busca.component.scss']
})
export class BuscaComponent {

  formFornecedor: FormGroup;
  @Output() criar = new EventEmitter<boolean>();

  constructor(
    private formBaseService: FormBaseService,
    public dialog: MatDialog){
    this.formFornecedor = this.formBaseService.criarFormulario();
    this.formFornecedor = this.formBaseService.adicionaCamposFornecedor(this.formFornecedor);
    // console.log(this.formFornecedor.value);
  }

  openDialog(): void {
    let diaglogRef = this.dialog.open(ModalCriacaoComponent, {
      width: '40%',
      height: '60%'
    });

    diaglogRef.componentInstance.criacao.subscribe((resposta) => {
      if(resposta === true){
        this.criar.emit(resposta);
      }
    })
  }
}
