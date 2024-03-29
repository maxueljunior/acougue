import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { MaterialModule } from '../core/material/material.module';
import { FormLoginComponent } from './form-login/form-login.component';
import { AsideBarComponent } from './aside-bar/aside-bar.component';
import { ContainerComponent } from './container/container.component';
import { CardComponent } from './card/card.component';
import { TableBaseComponent } from './table-base/table-base.component';
import { ModalCriacaoComponent } from '../fornecedor/modal-criacao/modal-criacao.component';
import { ModalExclusaoComponent } from './modal-exclusao/modal-exclusao.component';
import { SnackMensagemComponent } from './snack-mensagem/snack-mensagem.component';
import { BuscaComponent } from './busca/busca.component';
import { CardItemComponent } from './card-item/card-item.component';
import { ModalProcuraComponent } from './modal-procura/modal-procura.component';
import { ModalFinalizarComponent } from './modal-finalizar/modal-finalizar.component';
import { CardMensagemComponent } from './card-mensagem/card-mensagem.component';

@NgModule({
  declarations: [
    FormLoginComponent,
    AsideBarComponent,
    ContainerComponent,
    CardComponent,
    TableBaseComponent,
    ModalExclusaoComponent,
    SnackMensagemComponent,
    BuscaComponent,
    CardItemComponent,
    ModalProcuraComponent,
    ModalFinalizarComponent,
    CardMensagemComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MaterialModule
  ],
  exports: [
    FormLoginComponent,
    AsideBarComponent,
    ContainerComponent,
    CardComponent,
    TableBaseComponent,
    SnackMensagemComponent,
    BuscaComponent,
    CardItemComponent,
    ModalProcuraComponent,
    CardMensagemComponent
  ]
})
export class SharedModule { }
