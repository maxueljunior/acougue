import { Component, OnInit } from '@angular/core';
import { Color, NgxChartsModule, ScaleType } from '@swimlane/ngx-charts';
import { ResumoLucratividadeService } from './service/resumo-lucratividade.service';
import { ResumoLucratividade } from '../core/types/Resumo';


@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit{

  showFiller = false;
  singles!: number[];
  single: ResumoLucratividade[] = [];
  
  view: [number, number] = [800, 600];

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

  colorScheme: Color = {
    name: 'myScheme',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#0D0D0D', '#D92929', '#D95323', '#8C4C27', '#ECF241'],
  };

  constructor(
    private resumoLucratividadeService: ResumoLucratividadeService
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
  }

}
