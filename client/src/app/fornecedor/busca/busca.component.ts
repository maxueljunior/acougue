import { Component } from '@angular/core';
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
  
  constructor(
    private formBaseService: FormBaseService, 
    public dialog: MatDialog){
    this.formFornecedor = this.formBaseService.criarFormulario();
    this.formFornecedor = this.formBaseService.adicionaCamposFornecedor(this.formFornecedor);
    // console.log(this.formFornecedor.value);
  }

  openDialog(): void {
    this.dialog.open(ModalCriacaoComponent, {
      width: '40%',
      height: '60%'
    });
  }
}
