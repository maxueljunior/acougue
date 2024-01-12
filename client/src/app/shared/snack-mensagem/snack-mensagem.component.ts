import { Component, Inject, InjectionToken, Input, inject } from '@angular/core';
import { MAT_SNACK_BAR_DATA, MatSnackBarRef } from '@angular/material/snack-bar';

@Component({
  selector: 'app-snack-mensagem',
  templateUrl: './snack-mensagem.component.html',
  styleUrls: ['./snack-mensagem.component.scss'],
  host: {
    '[class.custom-snackbar-container]': 'true'
  }
})
export class SnackMensagemComponent {

  constructor(@Inject(MAT_SNACK_BAR_DATA) public data: any){
  }
  snackBarRef = inject(MatSnackBarRef);
}
