import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { FornecedorRoutingModule } from './fornecedor-routing.module';
import { FornecedorComponent } from './fornecedor.component';
import { SharedModule } from '../shared/shared.module';
import { MaterialModule } from '../core/material/material.module';
import { ReactiveFormsModule } from '@angular/forms';
import { ModalCriacaoComponent } from './modal-criacao/modal-criacao.component';


@NgModule({
  declarations: [
    FornecedorComponent,
    ModalCriacaoComponent
  ],
  imports: [
    CommonModule,
    FornecedorRoutingModule,
    SharedModule,
    MaterialModule,
    ReactiveFormsModule
  ],
  exports: [
    FornecedorComponent
  ]
})
export class FornecedorModule { }
