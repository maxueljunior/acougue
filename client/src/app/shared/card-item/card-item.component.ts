import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Produto } from 'src/app/core/types/Produto';

@Component({
  selector: 'app-card-item',
  templateUrl: './card-item.component.html',
  styleUrls: ['./card-item.component.scss']
})
export class CardItemComponent {

  @Input() produto!: Produto;
  @Output() edicao = new EventEmitter<Produto>();
  @Output() exclusao = new EventEmitter<Produto>();

  editar(produto: Produto){
    this.edicao.emit(produto);
  }

  excluir(produto: Produto){
    this.exclusao.emit(produto);
  }
}
