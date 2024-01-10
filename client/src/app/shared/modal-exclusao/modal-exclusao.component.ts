import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-modal-exclusao',
  templateUrl: './modal-exclusao.component.html',
  styleUrls: ['./modal-exclusao.component.scss']
})
export class ModalExclusaoComponent {

  constructor(
    public dialogRef: MatDialogRef<ModalExclusaoComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ){
  }
}
