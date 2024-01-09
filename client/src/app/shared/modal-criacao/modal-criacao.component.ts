import { Component, EventEmitter, Output } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { FormBaseService } from '../service/form-base.service';
import { FornecedorService } from 'src/app/fornecedor/service/fornecedor.service';

@Component({
  selector: 'app-modal-criacao',
  templateUrl: './modal-criacao.component.html',
  styleUrls: ['./modal-criacao.component.scss']
})
export class ModalCriacaoComponent {

  @Output() criacao = new EventEmitter<boolean>();

  constructor(
    public dialogRef: MatDialogRef<ModalCriacaoComponent>,
    public formFornecedor: FormBaseService,
    public fornecedorService: FornecedorService
    ) {
    }

  criar(){
    this.criacao.emit(true);
    this.dialogRef.close();
  }

  fechar(){
    this.dialogRef.close();
  }
}
