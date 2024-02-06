import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-modal-procura',
  templateUrl: './modal-procura.component.html',
  styleUrls: ['./modal-procura.component.scss']
})
export class ModalProcuraComponent {

  constructor(
    public dialogRef: MatDialogRef<ModalProcuraComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ){
  }

  fechar(): void{
    this.dialogRef.close();
  }
}
