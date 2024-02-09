import { Component, HostListener, OnInit } from '@angular/core';
import { Color, NgxChartsModule, ScaleType } from '@swimlane/ngx-charts';
import { ResumoLucratividadeService } from './service/resumo-lucratividade.service';
import { ResumoLucratividade } from '../core/types/Resumo';
import { ProdutoService } from '../produtos/service/produto.service';
import { Subscription } from 'rxjs';
import { Produto } from '../core/types/Produto';
import { UserService } from '../autenticacao/services/user.service';


@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit{

  showFiller = false;
  singles!: number[];
  single: ResumoLucratividade[] = [];
  
  tamWidth = 800;
  tamHeigth = 600;

  tamanhoTela: number = 0.00;
  telaResponsiva: boolean = false;

  view: [number, number] = [this.tamWidth, this.tamHeigth];

  produtosSubscription: Subscription = new Subscription();
  produtos: Produto[] = [];

  // options
  showXAxis: boolean = true;
  showYAxis: boolean = true;
  gradient: boolean = false;
  showLegend: boolean = true;
  showXAxisLabel: boolean = true;
  yAxisLabel: string = 'Produto';
  showYAxisLabel: boolean = false;
  xAxisLabel: string = 'Lucratividade (%)';
  legendTitle: string = 'Legenda';
  mostrarMensagem: boolean = false;

  colorScheme: Color = {
    name: 'myScheme',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#0D0D0D', '#D92929', '#D95323', '#8C4C27', '#ECF241'],
  };

  constructor(
    private resumoLucratividadeService: ResumoLucratividadeService,
    private produtosService: ProdutoService,
    private userService: UserService
  ) {
    Object.assign(this, { single: this.single });
  }

  ngOnInit(): void {
    this.resumoLucratividadeService.gerarResumo().subscribe((r) => {
      this.single = r.content.map((rl) => ({
        name: rl.descricao,
        value: rl.total
      }) as ResumoLucratividade)
      console.log(this.single);
    })

    this.produtosSubscription = this.produtosService.produtos$.subscribe((p) => {

      let produtosAbaixoDaFaixa = p.filter((prod) => prod.totalQuantidade <= 5.00);

      this.produtos = produtosAbaixoDaFaixa;
      console.log(this.produtos);
    })

    this.produtosService.findAll(0, 9999, '');

    this.userService.decodificarJWT();
  }

  defineClasse(produto: Produto): string{
    if(produto.totalQuantidade === 0){
      return 'card';
    }

    return 'card-warning';
  }

  defineMensagem(produto: Produto): string{
    if(produto.totalQuantidade === 0){
      return `${produto.descricao} acabou!`;
    }

    return `${produto.descricao} contem ${produto.totalQuantidade} no estoque!`;
  }

  showMessages(): void{
    if(this.mostrarMensagem) {
      this.mostrarMensagem = false
    }else{
      this.mostrarMensagem = true;
    }
  }

  @HostListener('window:resize', ['$event'])
  onResize(event: Event) {
    this.tamanhoTela = window.innerWidth;
    if(this.tamanhoTela >= 769){
      this.telaResponsiva = true;
      this.showLegend = true;
      this.view = [this.tamWidth,this.tamHeigth];
    }else{
      this.telaResponsiva = false;
      this.showLegend = false;
      let tamWidth = window.innerWidth * 0.60;
      let tamHeigth = window.innerHeight * 0.40;
      this.view = [tamWidth,tamHeigth];
    }

    console.log(this.telaResponsiva);
  }
}
