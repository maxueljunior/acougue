import { Component, EventEmitter, Inject, Output } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Cliente } from 'src/app/core/types/Cliente';
import { FormBaseService } from 'src/app/shared/service/form-base.service';

@Component({
  selector: 'app-modal-criacao',
  templateUrl: './modal-criacao.component.html',
  styleUrls: ['./modal-criacao.component.scss']
})
export class ModalCriacaoComponent {
  
  @Output() criacao = new EventEmitter<boolean>();
  @Output() edicao = new EventEmitter<Cliente>();

  constructor(
    public dialogRef: MatDialogRef<ModalCriacaoComponent>,
    public formBaseService: FormBaseService,
    @Inject(MAT_DIALOG_DATA) public data: any
    ) {
    }

  criarOuEditar(){
    if(!this.data.editar){
      this.criacao.emit(true);
    }else{
      this.edicao.emit(this.formBaseService.formBase.value);
    }
    this.dialogRef.close();
  }

  fechar(){
    this.formBaseService.resetarCampos();
    this.dialogRef.close();
  }
}
