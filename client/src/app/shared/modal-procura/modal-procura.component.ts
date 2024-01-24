import { Component } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-modal-procura',
  templateUrl: './modal-procura.component.html',
  styleUrls: ['./modal-procura.component.scss']
})
export class ModalProcuraComponent {

  constructor(
    public dialogRef: MatDialogRef<ModalProcuraComponent>
  ){

  }
}
