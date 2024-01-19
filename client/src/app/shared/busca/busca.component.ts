import { Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { debounceTime} from 'rxjs';
import { ModalCriacaoComponent } from 'src/app/fornecedor/modal-criacao/modal-criacao.component';
import { FormBaseService } from 'src/app/shared/service/form-base.service';

const PAUSA = 300;

@Component({
  selector: 'app-busca',
  templateUrl: './busca.component.html',
  styleUrls: ['./busca.component.scss'],
})
export class BuscaComponent implements OnInit{

  campoBusca = new FormControl();
  @Output() criar = new EventEmitter<boolean>();
  @Output() busca = new EventEmitter<string>();

  @Input() textoLabel: string = '';
  @Input() textoPlaceholder: string = '';
  @Input() titulo: string = '';

  constructor(
    public dialog: MatDialog){
  }

  ngOnInit(): void {
    this.campoBusca.valueChanges
      .pipe(debounceTime(300))
      .subscribe((valorDigitado) => {
        this.busca.emit(valorDigitado);
      });
  }

  criacao(): void {
    this.criar.emit(true);
    // let tamWidth = window.innerWidth * 0.40;
    // let tamHeigth = window.innerHeight * 0.80;

    // let diaglogRef = this.dialog.open(ModalCriacaoComponent, {
    //   width: `${tamWidth}px`,
    //   height: `${tamHeigth}px`,
    //   data: {
    //     editar: false
    //   }
    // });

    // diaglogRef.componentInstance.criacao.subscribe((resposta) => {
    //   if(resposta === true){
    //     this.criar.emit(resposta);
    //   }
    // })
  }
}
