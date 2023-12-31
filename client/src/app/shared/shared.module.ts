import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { MaterialModule } from '../core/material/material.module';
import { FormLoginComponent } from './form-login/form-login.component';
import { AsideBarComponent } from './aside-bar/aside-bar.component';
import { ContainerComponent } from './container/container.component';
import { CardComponent } from './card/card.component';
import { TableBaseComponent } from './table-base/table-base.component';

@NgModule({
  declarations: [
    FormLoginComponent,
    AsideBarComponent,
    ContainerComponent,
    CardComponent,
    TableBaseComponent
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
    TableBaseComponent
  ]
})
export class SharedModule { }
