import { Component, EventEmitter, Inject, Input, Output } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-modal-finalizar',
  templateUrl: './modal-finalizar.component.html',
  styleUrls: ['./modal-finalizar.component.scss']
})
export class ModalFinalizarComponent {
  
  @Output() finalizar = new EventEmitter<boolean>();
  @Output() gerarCupom = new EventEmitter<boolean>();
  public gerouCupom: boolean = false;
 
  constructor(
    public dialogRef: MatDialogRef<ModalFinalizarComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ){
  }

  finalizarModal(): void{
    this.finalizar.emit(true);
  }

  geraCupom(): void{
    this.gerouCupom = true;
    this.gerarCupom.emit(true);
  }
}
