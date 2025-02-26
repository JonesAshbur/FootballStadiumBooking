<template>
  <div ref="echart" style="width: 100%; height: 100%;"></div>
</template>

<script>
  import * as echarts from "echarts";

  export default {
  props: {
    chartData: {
      type: Object,
      default() {
        return {
          xData: [],
          series: []
        };
      }
    },
    isChart: {
      type: String,
      default: ""
    }
  },
  watch: {
    chartData: {
      handler: function () {
        this.$nextTick(() => {
          this.initChart();
        });
      },
      deep: true,
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initChart();
      
      // 添加窗口大小改变时的自适应
      window.addEventListener('resize', this.resizeChart);
    });
  },
  beforeDestroy() {
    // 清理事件监听
    window.removeEventListener('resize', this.resizeChart);
    if (this.echart) {
      this.echart.dispose();
    }
  },
  computed: {
    options() {
      let _this = this;
      if(_this.isChart === "line"){
          return _this.lineOption;
      }else if(_this.isChart === "bar"){
          return _this.barOption;
      }else if(_this.isChart === "pie"){
        return _this.pieOption;
      }
      return _this.lineOption;
    }
  },
  methods: {
    initChart() {
      if (!this.$refs.echart) {
        return;
      }

      if (!this.echart) {
        this.echart = echarts.init(this.$refs.echart);
      }

      this.initChartData();
      this.echart.setOption(this.options);
    },
    resizeChart() {
      if (this.echart) {
        this.echart.resize();
      }
    },
    initChartData() {
        let _this = this;
        if(_this.isChart === 'line'){
           // 初始化折线图数据
           _this.lineOption.xAxis.data = _this.chartData.xData;
           _this.lineOption.series = _this.chartData.series;
        }else if(_this.isChart === 'bar'){
           // 初始化柱状图数据
           _this.barOption.xAxis.data = _this.chartData.xData;
          _this.barOption.series = _this.chartData.series;
        }else if(_this.isChart === 'pie'){
          // 初始化饼状图数据
          _this.pieOption.series = _this.chartData.series;
        }
    }
  },
  data() {
    return {
      lineOption: {
        title: {
          text: '近五天预约场地数和租借器材数',
          x: 'center'
        },
        tooltip: {
          trigger: 'axis'
        },
        grid: {
          top: '20%',
          bottom: '25%'
        },
        color: ['#00EE00', '#FF9F7F'],
        legend: {
          // orient 设置布局方式，默认水平布局，可选值：'horizontal'（水平） ¦ 'vertical'（垂直）
          orient: 'horizontal',
          // x 设置水平安放位置，默认全图居中，可选值：'center' ¦ 'left' ¦ 'right' ¦ {number}（x坐标，单位px）
          x: 'center',
          // y 设置垂直安放位置，默认全图顶端，可选值：'top' ¦ 'bottom' ¦ 'center' ¦ {number}（y坐标，单位px）
          y: 'bottom',
          data: ['租借器材','预约场地']
        },
        xAxis: {
          data: [],
          axisLabel:{
            interval: 0
          }
        },
        yAxis: {
          interval: 1
        },
        series: []
      },
      barOption:{
        title: {
          text: '',
          x: 'center'
        },
        tooltip: {
          trigger: 'axis'
        },
        xAxis: {
          data: [],
        },
        yAxis: {
        },
        series: [
          {
            type: 'bar',
            data: []
          }
        ]
      },
      pieOption: {
        title: {
          text: '预约场地/租借器材占比',
          x: 'center'
        },
        legend: {
          // orient 设置布局方式，默认水平布局，可选值：'horizontal'（水平） ¦ 'vertical'（垂直）
          orient: 'horizontal',
          icon: 'circle',
          // x 设置水平安放位置，默认全图居中，可选值：'center' ¦ 'left' ¦ 'right' ¦ {number}（x坐标，单位px）
          x: 'center',
          // y 设置垂直安放位置，默认全图顶端，可选值：'top' ¦ 'bottom' ¦ 'center' ¦ {number}（y坐标，单位px）
          y: 'bottom',
          data: ['租借器材','预约场地']
        },
        tooltip: {
          trigger: 'item',
          formatter: "{b} : {d}%"
        },
        color: ['#00EE00', '#FF9F7F'],
        series: [
          {
            type: 'pie',
            data: [],
          }
        ]
      },
      echart: null,
    };
  },
};
</script>

<style scoped>
div {
  width: 100%;
  height: 100%;
}
</style>
