import { Component, ElementRef, Input, OnChanges, OnInit } from '@angular/core';
import * as d3 from 'd3';
// import moment from 'moment-timezone';
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
  private svgInner: any;
  private chart: any;
  private margin: number = 30;
  private width: number = 0;
  private height: number = 0;
  private xScale: any;
  private yScale: any;
  private yAxis: any;
  private xAxis: any;
  private lineGroup: any;

  private format: any = d3.timeFormat("%Y-%m-%d %H:%M");

  // console.log(format(new Date(1075611600000))); // returns a string

  constructor(private chartElem: ElementRef) { }

  ngOnInit(): void {
    // this.data.map((d: StatisticsChartDataModel) => { return {timestamp: this.format(d.timestamp), count: d.numberOfTimes}});
    this.createSvg();
    this.drawChart();
  }

  ngOnChanges(): void {
    if (this.svgInner) {
      this.drawChart();
    }
  }

  private createSvg(): void {
    this.width = (document.getElementById('linechart') as HTMLInputElement).offsetWidth - (this.margin * 2);
    this.height = (document.getElementById('linechart') as HTMLInputElement).offsetHeight - (this.margin * 2);
    
    this.svg = d3
      .select('figure#linechart')
      .append('svg')
      .attr('width', this.width + (this.margin * 2))
      .attr('height', this.height + (this.margin * 2));
      // .renderArea(true)
      // .mouseZoomable(true);

    this.svgInner = this.svg
      .append('g')
      .attr('class', 'chart')
      .style('transform', 'translate(' + this.margin + 'px, ' + this.margin + 'px)');

    this.chart = this.svg.append('g')
      .attr('class', 'chart')
      .attr('transform', `translate(${this.margin}, ${this.margin})`);

    console.log("this.data", this.data);
    this.yScale = d3
      .scaleLinear()
      .domain([d3.extent(this.data, (d: StatisticsChartDataModel) => d.numberOfTimes)])
      .range([this.height - 2 * this.margin, 0]);

    this.xScale = d3.scaleTime()
      .domain(d3.extent(this.data, (d: StatisticsChartDataModel) => d.timestamp))
      .rangeRound([0, this.width]);

    this.yAxis = this.chart
      .append('g')
      .attr('id', 'y-axis')
      .attr('class', 'chart')
      .style('transform','translate(' + this.margin + 'px, 0)');

    this.xAxis = this.chart
      .append('g')
      .attr('id', 'x-axis')
      .attr('class', 'chart')
      .style('transform', 'translate(0, ' + (this.height - 2 * this.margin) + 'px)');

    console.log("createSVG");
  }

  private drawChart(): void {
    if (this.lineGroup) {
      this.lineGroup.remove();
    }
    console.log("drawChart");
    this.yScale = d3
    .scaleLinear()
    .domain([0, d3.max(this.data, (d: StatisticsChartDataModel) => d.numberOfTimes)])
    .range([this.height - 2 * this.margin, 0]);

    this.xScale = d3.scaleTime()
      .domain(d3.extent(this.data, (d: StatisticsChartDataModel) => d.timestamp))
      .rangeRound([0, this.width]);
    
      d3.select('svg').data(this.data).exit().remove();


    this.changeTimeFormat();

    const xAxis = d3.axisBottom(this.xScale)
      .ticks(10)
      .tickFormat(this.format);
    this.xScale.range([this.margin, this.width - 2 * this.margin]);
    this.xAxis.call(xAxis);

    const yAxis = d3.axisLeft(this.yScale);

    this.yAxis.call(yAxis);

    const tooltip: any = d3.select('body')
      .append('div')
      .style('position', 'absolute')
      .style('z-index', '10')
      .style('visibility', 'hidden')
      .style('padding', '10px')
      .style('background', '#000')
      .style('border-radius', '5px')
      .style('color', '#fff');
    
    const update: any = this.chart.selectAll('line').data(this.data);

    update.exit().remove();
    //============

    this.lineGroup = 
      update
      .enter()
      .append('g')
      .append('path')
      .attr('class', '.chart')
      .attr('id', 'line')
      .style('fill', 'none')
      .style('stroke', 'steelblue')
      .style('stroke-width', '2px');

    this.width = this.chartElem.nativeElement.getBoundingClientRect().width;
    this.svg.attr('width', this.width);


    const line = d3.line()
      .x((d: any) => d[0])
      .y((d: any) => d[1]);
      // .curve(d3.curveMonotoneX);

    const points: [number, number][] = this.data.map((d: StatisticsChartDataModel) => [
      this.xScale(d.timestamp),
      this.yScale(d.numberOfTimes),
    ]);

    this.lineGroup
      .attr('class', 'line')
      .attr('d', line(points))
      .on('mouseover', (d: StatisticsChartDataModel) =>
        tooltip
          .html(`Time: ${d.timestamp} <br> Count: ${d.numberOfTimes}`)
          .style('visibility', 'visible'))
      .on('mouseout', () => tooltip.html('').style('visibility', 'hidden'));
  }

  private changeTimeFormat() {
    const diffTime: number = d3.max(this.data, (d: StatisticsChartDataModel) => d.timestamp) - d3.min(this.data, (d: StatisticsChartDataModel) => d.timestamp);
    const diffDays: number = Math.ceil(diffTime / (1000 * 60 * 60 * 24)); 
    if (diffDays <= 1) {
      this.format = d3.timeFormat("%H:%M");
    } else if (diffDays < 14) {
      this.format = d3.timeFormat("%b-%d %H%p");
    } else if (diffDays <= 60) {
      this.format = d3.timeFormat("%b-%d");
    } else {
      this.format = d3.timeFormat("%y %b")
    }
  }

}
