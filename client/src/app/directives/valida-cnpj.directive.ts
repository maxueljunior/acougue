import { Directive, Input } from '@angular/core';
import { AbstractControl, NG_VALIDATORS, ValidationErrors, Validator, ValidatorFn } from '@angular/forms';

@Directive({
  selector: '[validaCnpj]',
  providers: [
    {
      provide: NG_VALIDATORS,
      useExisting: ValidaCnpjDirective,
      multi: true
    }
  ]
})
export class ValidaCnpjDirective implements Validator{

  validate(control: AbstractControl): ValidationErrors | null {
    const cnpj = control.value;
    if(cnpj !== ""){
      let regex: RegExp = /^\d{2}\.\d{3}\.\d{3}\/\d{4}\-\d{2}$/;
      let ativa: boolean = regex.test(cnpj);
      return ativa ? null : {'validaCnpj': true};
    }
    return null;
  }

}
