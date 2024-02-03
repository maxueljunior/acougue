import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class VendaService {

  constructor(
    private http: HttpClient
  ) { }

  public create(): void{
    
  }
}
