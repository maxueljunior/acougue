import { Directive, Input } from '@angular/core';
import { AbstractControl, NG_VALIDATORS, ValidationErrors } from '@angular/forms';

@Directive({
  selector: '[appValidaQuantidade]',
  providers: [
    {
      provide: NG_VALIDATORS,
      useExisting: ValidaQuantidadeDirective,
      multi: true
    }
  ]
})
export class ValidaQuantidadeDirective {

  @Input('appValidaQuantidade') customValidation!: string;

  constructor() { }

  validate(control: AbstractControl): ValidationErrors | null {
    console.log(this.customValidation);
    if (this.customValidation === 'positive' && control.value <= 0) {
      return { 'appValidaQuantidade': true };
    }
    console.log(control);
    return null;
  }
}
