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
  }
}
