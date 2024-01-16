import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ClientesRoutingModule } from './clientes-routing.module';
import { ClientesComponent } from './clientes.component';
import { SharedModule } from '../shared/shared.module';
import { MaterialModule } from '../core/material/material.module';
import { ModalCriacaoComponent } from './modal-criacao/modal-criacao.component';
import { ReactiveFormsModule } from '@angular/forms';


@NgModule({
  declarations: [
    ClientesComponent,
    ModalCriacaoComponent
  ],
  imports: [
    CommonModule,
    ClientesRoutingModule,
    SharedModule,
    MaterialModule,
    ReactiveFormsModule
  ],
  exports: [
    ClientesComponent
  ]
})
export class ClientesModule { }
