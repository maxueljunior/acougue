import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-card-mensagem',
  templateUrl: './card-mensagem.component.html',
  styleUrls: ['./card-mensagem.component.scss']
})
export class CardMensagemComponent {

  @Input() estilo = 'card';
  @Input() mensagem = '';
  
}

