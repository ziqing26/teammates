import { Component, Input, OnChanges, OnInit } from '@angular/core';
import * as d3 from 'd3';
import { StatisticsChartDataModel } from './ststistics-chart-model';

@Component({
  selector: 'tm-statistics-chart',
  templateUrl: './statistics-chart.component.html',
  styleUrls: ['./statistics-chart.component.scss']
})
export class StatisticsChartComponent implements OnInit, OnChanges {

  @Input()
  data: StatisticsChartDataModel[] = [];

  private svg: any;
  private chart: any;
  private margin: number = 30;
  private width: number = 0;
  private height: number = 0;
  private xScale: any;
  private yScale: any;
  private yAxis: any;

  // private format: any = d3.time.format("%Y-%m-%d");
  // console.log(format(new Date(1075611600000))); // returns a string

  constructor() { }

  ngOnInit(): void {
    this.createSvg();
    this.drawChart();
  }

  ngOnChanges(): void {
    if (this.chart) {
      this.drawChart();
    }
  }

  private createSvg(): void {
    this.width = (document.getElementById('linechart') as HTMLInputElement).offsetWidth - (this.margin * 2);
    this.height = (document.getElementById('linechart') as HTMLInputElement).offsetHeight - (this.margin * 2);

    this.svg = d3.select('figure#chart')
      .append('svg')
      .attr('width', this.width + (this.margin * 2))
      .attr('height', this.height + (this.margin * 2));

    this.chart = this.svg.append('g')
      .attr('class', 'linechart')
      .attr('transform', `translate(${this.margin}, ${this.margin})`);

    this.xScale = d3.scaleTime()
      .domain(this.data.map((d: StatisticsChartDataModel) => new Date(d.timestamp)))
      .range([0, this.width])
      .padding(0.2);

    this.yScale = d3.scaleLinear()
      .domain([0, d3.max(this.data, (d: StatisticsChartDataModel) => d.numberOfTimes)])
      .range([this.height, 0]);

    this.svg.append('g')
      .attr('class', 'axis axis-x')
      .attr('transform', `translate(${this.margin}, ${this.margin + this.height})`);

    this.yAxis = this.svg.append('g')
      .attr('class', 'axis axis-y')
      .attr('transform', `translate(${this.margin}, ${this.margin})`)
      .call(d3.axisLeft(this.yScale));
  }

  private drawChart(): void {
    this.xScale.domain(this.data.map((d: StatisticsChartDataModel) => new Date(d.timestamp)));
    this.yScale.domain([0, d3.max(this.data, (d: StatisticsChartDataModel) => d.numberOfTimes)]);
    this.yAxis.call(d3.axisLeft(this.yScale));

    const tooltip: any = d3.select('body')
      .append('div')
      .style('position', 'absolute')
      .style('z-index', '10')
      .style('visibility', 'hidden')
      .style('padding', '10px')
      .style('background', '#000')
      .style('border-radius', '5px')
      .style('color', '#fff');

      const update: any = this.chart.selectAll('.linechart').data(this.data);

      // remove exiting bars
      update.exit().remove();
  
      this.chart.selectAll('.linechart')
        .attr('x', (d: StatisticsChartDataModel) => this.xScale(new Date(d.timestamp)))
        .attr('y', (d: StatisticsChartDataModel) => this.yScale(d.numberOfTimes))
        .attr('height', (d: StatisticsChartDataModel) => this.height - this.yScale(d.numberOfTimes))
        .attr('width', this.xScale.bandwidth());
  
      update
        .enter()
        .append('rect')
        .attr('class', 'linechart')
        .attr('x', (d: StatisticsChartDataModel) => this.xScale(new Date(d.timestamp)))
        .attr('y', (d: StatisticsChartDataModel) => this.yScale(d.numberOfTimes))
        .attr('height', (d: StatisticsChartDataModel) => this.height - this.yScale(d.numberOfTimes))
        .attr('width', this.xScale.bandwidth())
        .style('fill', 'steelblue')
        .on('mouseover', (d: StatisticsChartDataModel) =>
          tooltip
            .html(`Date time: ${new Date(d.timestamp).toString()} <br> Frequency: ${d.numberOfTimes}`)
            .style('visibility', 'visible'))
        .on('mousemove', () => {
          const top: number = d3.event.pageY - 10;
          const left: number = d3.event.pageX + 10;
          tooltip
            .style('top', `${top}px`)
            .style('left', `${left}px`);
        })
        .on('mouseout', () => tooltip.html('').style('visibility', 'hidden'));

    // this.svg.append("path")
    //     .datum(this.data)
    //     .attr("fill", "none")
    //     .attr("stroke", "steelblue")
    //     .attr("stroke-width", 1.5)
    //     .attr("d", d3.line()
    //       .x((d: any) => this.xScale(d3.timeParse("%Y-%m-%d")(d.timestamp)))
    //       .y((d: any) => this.yScale(d.numberOfTimes))
    //       )
  }

}
