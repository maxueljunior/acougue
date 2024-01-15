import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ProdutosRoutingModule } from './produtos-routing.module';
import { ProdutosComponent } from './produtos.component';
import { SharedModule } from '../shared/shared.module';
import { MaterialModule } from '../core/material/material.module';


@NgModule({
  declarations: [
    ProdutosComponent
  ],
  imports: [
    CommonModule,
    ProdutosRoutingModule,
    SharedModule,
    MaterialModule
  ],
  exports: [
    ProdutosComponent
  ]
})
export class ProdutosModule { }
