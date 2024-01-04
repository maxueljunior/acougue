import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { FornecedorRoutingModule } from './fornecedor-routing.module';
import { FornecedorComponent } from './fornecedor.component';
import { SharedModule } from '../shared/shared.module';
import { MaterialModule } from '../core/material/material.module';
import { BuscaComponent } from './busca/busca.component';
import { ReactiveFormsModule } from '@angular/forms';


@NgModule({
  declarations: [
    FornecedorComponent,
    BuscaComponent
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
