/**
 * ECharts 配置工具
 * 包含图表主题配置、通用配置和颜色配置
 */

// 主题颜色配置
export const colors = {
  primary: '#409EFF',
  success: '#67C23A',
  warning: '#E6A23C',
  danger: '#F56C6C',
  info: '#909399',
  // 图表专用颜色系列
  chartColors: [
    '#409EFF',
    '#67C23A',
    '#E6A23C',
    '#F56C6C',
    '#909399',
    '#9b59b6',
    '#3498db',
    '#1abc9c',
    '#e74c3c',
    '#f39c12'
  ],
  // 渐变色配置
  gradient: {
    primary: ['#409EFF', '#79bbff'],
    success: ['#67C23A', '#95d475'],
    warning: ['#E6A23C', '#eebe77'],
    danger: ['#F56C6C', '#f89898']
  }
}

// 图表主题配置
export const theme = {
  // 背景色
  backgroundColor: 'transparent',
  // 文本样式
  textStyle: {
    color: '#303133',
    fontFamily: 'Microsoft YaHei, Arial, sans-serif'
  },
  // 标题配置
  title: {
    textStyle: {
      color: '#303133',
      fontSize: 16,
      fontWeight: 'bold'
    },
    subtextStyle: {
      color: '#909399',
      fontSize: 12
    }
  },
  // 图例配置
  legend: {
    textStyle: {
      color: '#606266'
    }
  },
  // 提示框配置
  tooltip: {
    backgroundColor: 'rgba(50, 50, 50, 0.9)',
    borderColor: '#333',
    textStyle: {
      color: '#fff'
    }
  },
  // 坐标轴配置
  categoryAxis: {
    axisLine: {
      lineStyle: {
        color: '#DCDFE6'
      }
    },
    axisTick: {
      lineStyle: {
        color: '#DCDFE6'
      }
    },
    axisLabel: {
      color: '#606266'
    },
    splitLine: {
      lineStyle: {
        color: '#EBEEF5'
      }
    }
  },
  valueAxis: {
    axisLine: {
      lineStyle: {
        color: '#DCDFE6'
      }
    },
    axisTick: {
      lineStyle: {
        color: '#DCDFE6'
      }
    },
    axisLabel: {
      color: '#606266'
    },
    splitLine: {
      lineStyle: {
        color: '#EBEEF5',
        type: 'dashed'
      }
    }
  }
}

// 通用图表基础配置
export const baseOption = {
  backgroundColor: theme.backgroundColor,
  textStyle: theme.textStyle,
  tooltip: {
    ...theme.tooltip,
    trigger: 'axis'
  },
  legend: {
    ...theme.legend,
    top: 10
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    top: 60,
    containLabel: true
  }
}

// 折线图配置
export const getLineOption = (options = {}) => {
  return {
    ...baseOption,
    xAxis: {
      type: 'category',
      boundaryGap: false,
      ...theme.categoryAxis,
      ...options.xAxis
    },
    yAxis: {
      type: 'value',
      ...theme.valueAxis,
      ...options.yAxis
    },
    series: options.series || [],
    ...options.extra
  }
}

// 柱状图配置
export const getBarOption = (options = {}) => {
  return {
    ...baseOption,
    xAxis: {
      type: 'category',
      ...theme.categoryAxis,
      ...options.xAxis
    },
    yAxis: {
      type: 'value',
      ...theme.valueAxis,
      ...options.yAxis
    },
    series: options.series || [],
    ...options.extra
  }
}

// 横向柱状图配置
export const getHorizontalBarOption = (options = {}) => {
  return {
    ...baseOption,
    xAxis: {
      type: 'value',
      ...theme.valueAxis,
      ...options.xAxis
    },
    yAxis: {
      type: 'category',
      ...theme.categoryAxis,
      ...options.yAxis
    },
    series: options.series || [],
    ...options.extra
  }
}

// 饼图配置
export const getPieOption = (options = {}) => {
  return {
    backgroundColor: theme.backgroundColor,
    textStyle: theme.textStyle,
    tooltip: {
      ...theme.tooltip,
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      ...theme.legend,
      orient: 'vertical',
      left: 'left',
      top: 'center'
    },
    series: [
      {
        type: 'pie',
        radius: options.radius || ['40%', '70%'],
        center: options.center || ['60%', '50%'],
        avoidLabelOverlap: true,
        itemStyle: {
          borderRadius: 8,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: true,
          formatter: '{b}: {d}%'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 14,
            fontWeight: 'bold'
          },
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        },
        data: options.data || []
      }
    ],
    ...options.extra
  }
}

// 仪表盘配置
export const getGaugeOption = (options = {}) => {
  return {
    backgroundColor: theme.backgroundColor,
    series: [
      {
        type: 'gauge',
        startAngle: 180,
        endAngle: 0,
        min: options.min || 0,
        max: options.max || 100,
        splitNumber: 5,
        radius: options.radius || '100%',
        center: options.center || ['50%', '70%'],
        axisLine: {
          lineStyle: {
            width: 20,
            color: [
              [0.3, colors.success],
              [0.7, colors.warning],
              [1, colors.danger]
            ]
          }
        },
        pointer: {
          itemStyle: {
            color: 'auto'
          }
        },
        axisTick: {
          distance: -20,
          length: 8,
          lineStyle: {
            color: '#fff',
            width: 2
          }
        },
        splitLine: {
          distance: -20,
          length: 20,
          lineStyle: {
            color: '#fff',
            width: 3
          }
        },
        axisLabel: {
          color: 'inherit',
          distance: 30,
          fontSize: 12
        },
        detail: {
          valueAnimation: true,
          formatter: '{value}%',
          color: 'inherit',
          fontSize: 20
        },
        data: options.data || []
      }
    ],
    ...options.extra
  }
}

// 创建渐变色
export const createGradient = (echarts, color1, color2, direction = 'vertical') => {
  return new echarts.graphic.LinearGradient(
    direction === 'vertical' ? 0 : 0,
    direction === 'vertical' ? 0 : 0,
    direction === 'vertical' ? 0 : 1,
    direction === 'vertical' ? 1 : 0,
    [
      { offset: 0, color: color1 },
      { offset: 1, color: color2 }
    ]
  )
}

// 图表resize防抖
export const debounce = (fn, delay = 300) => {
  let timer = null
  return function(...args) {
    if (timer) clearTimeout(timer)
    timer = setTimeout(() => {
      fn.apply(this, args)
    }, delay)
  }
}

// 数值格式化
export const formatNumber = (num) => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + '万'
  }
  return num.toString()
}

// 图表resize监听器
export const addResizeListener = (chart) => {
  const resize = debounce(() => {
    chart && chart.resize()
  }, 300)
  window.addEventListener('resize', resize)
  return () => window.removeEventListener('resize', resize)
}

export default {
  colors,
  theme,
  baseOption,
  getLineOption,
  getBarOption,
  getHorizontalBarOption,
  getPieOption,
  getGaugeOption,
  createGradient,
  debounce,
  formatNumber,
  addResizeListener
}
