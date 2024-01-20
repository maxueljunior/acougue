import { Component, Input } from '@angular/core';
import { Produto } from 'src/app/core/types/Produto';

@Component({
  selector: 'app-card-item',
  templateUrl: './card-item.component.html',
  styleUrls: ['./card-item.component.scss']
})
export class CardItemComponent {

  @Input() produto!: Produto;
  
}
