import { Component, EventEmitter, Inject, Input, Output } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { FormBaseService } from '../service/form-base.service';
import { FornecedorService } from 'src/app/fornecedor/service/fornecedor.service';
import { Fornecedor } from 'src/app/core/types/Fornecedor';

@Component({
  selector: 'app-modal-criacao',
  templateUrl: './modal-criacao.component.html',
  styleUrls: ['./modal-criacao.component.scss']
})
export class ModalCriacaoComponent {

  @Output() criacao = new EventEmitter<boolean>();
  @Output() edicao = new EventEmitter<Fornecedor>();

  constructor(
    public dialogRef: MatDialogRef<ModalCriacaoComponent>,
    public formFornecedor: FormBaseService,
    @Inject(MAT_DIALOG_DATA) public data: any
    ) {
      console.log(this.formFornecedor.formBase.value)
    }

  criarOuEditar(){
    if(!this.data.editar){
      this.criacao.emit(true);
    }else{
      this.edicao.emit(this.data.fornecedor);
    }
    this.dialogRef.close();
  }

  fechar(){
    this.formFornecedor.formBase.reset();
    this.dialogRef.close();
  }
}
