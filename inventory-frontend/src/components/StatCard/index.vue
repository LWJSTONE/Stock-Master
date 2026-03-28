<template>
  <div class="stat-card" :class="{'is-loading': loading}">
    <div class="stat-card__icon" :style="{ backgroundColor: color }">
      <i :class="icon"></i>
    </div>
    <div class="stat-card__content">
      <div class="stat-card__title">{{ title }}</div>
      <div class="stat-card__value">
        <span class="value-number" ref="valueRef">{{ displayValue }}</span>
        <span v-if="suffix" class="value-suffix">{{ suffix }}</span>
      </div>
      <div v-if="trend !== null" class="stat-card__trend" :class="trendClass">
        <i :class="trendIcon"></i>
        <span>{{ Math.abs(trend) }}%</span>
        <span class="trend-label">较昨日</span>
      </div>
    </div>
    <div class="stat-card__decoration"></div>
  </div>
</template>

<script>
export default {
  name: 'StatCard',
  props: {
    // 标题
    title: {
      type: String,
      required: true
    },
    // 数值
    value: {
      type: [Number, String],
      default: 0
    },
    // 图标类名
    icon: {
      type: String,
      default: 'el-icon-data-line'
    },
    // 图标背景色
    color: {
      type: String,
      default: '#409EFF'
    },
    // 数值后缀
    suffix: {
      type: String,
      default: ''
    },
    // 趋势百分比（正数为上升，负数为下降）
    trend: {
      type: Number,
      default: null
    },
    // 是否加载中
    loading: {
      type: Boolean,
      default: false
    },
    // 是否动画显示数字
    animate: {
      type: Boolean,
      default: true
    },
    // 动画持续时间（毫秒）
    duration: {
      type: Number,
      default: 1500
    }
  },
  data() {
    return {
      displayValue: 0,
      animationId: null
    }
  },
  computed: {
    trendClass() {
      if (this.trend > 0) return 'is-up'
      if (this.trend < 0) return 'is-down'
      return 'is-flat'
    },
    trendIcon() {
      if (this.trend > 0) return 'el-icon-top'
      if (this.trend < 0) return 'el-icon-bottom'
      return 'el-icon-minus'
    }
  },
  watch: {
    value: {
      handler(newVal) {
        if (this.animate && typeof newVal === 'number') {
          this.animateNumber(newVal)
        } else {
          this.displayValue = this.formatValue(newVal)
        }
      },
      immediate: true
    }
  },
  methods: {
    // 数字动画
    animateNumber(targetValue) {
      if (this.animationId) {
        cancelAnimationFrame(this.animationId)
      }
      
      const startValue = parseFloat(this.displayValue) || 0
      const startTime = performance.now()
      const endTime = startTime + this.duration
      
      const animate = (currentTime) => {
        if (currentTime >= endTime) {
          this.displayValue = this.formatValue(targetValue)
          return
        }
        
        const progress = (currentTime - startTime) / this.duration
        // 使用easeOutQuart缓动函数
        const easeProgress = 1 - Math.pow(1 - progress, 4)
        const currentValue = startValue + (targetValue - startValue) * easeProgress
        
        this.displayValue = this.formatValue(Math.round(currentValue))
        this.animationId = requestAnimationFrame(animate)
      }
      
      this.animationId = requestAnimationFrame(animate)
    },
    
    // 格式化数值
    formatValue(val) {
      if (typeof val === 'string') return val
      if (val >= 10000) {
        return (val / 10000).toFixed(1) + '万'
      }
      return val.toLocaleString()
    }
  },
  beforeDestroy() {
    if (this.animationId) {
      cancelAnimationFrame(this.animationId)
    }
  }
}
</script>

<style lang="scss" scoped>
.stat-card {
  position: relative;
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: flex-start;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
  }
  
  &.is-loading {
    .stat-card__value {
      opacity: 0.5;
    }
  }
  
  &__icon {
    width: 64px;
    height: 64px;
    border-radius: 16px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    
    i {
      font-size: 32px;
      color: #fff;
    }
  }
  
  &__content {
    flex: 1;
    margin-left: 20px;
    position: relative;
    z-index: 1;
  }
  
  &__title {
    font-size: 14px;
    color: #909399;
    margin-bottom: 8px;
  }
  
  &__value {
    display: flex;
    align-items: baseline;
    
    .value-number {
      font-size: 32px;
      font-weight: bold;
      color: #303133;
      line-height: 1;
    }
    
    .value-suffix {
      font-size: 14px;
      color: #909399;
      margin-left: 4px;
    }
  }
  
  &__trend {
    display: flex;
    align-items: center;
    margin-top: 12px;
    font-size: 13px;
    
    i {
      margin-right: 4px;
    }
    
    .trend-label {
      color: #909399;
      margin-left: 4px;
    }
    
    &.is-up {
      color: #67C23A;
    }
    
    &.is-down {
      color: #F56C6C;
    }
    
    &.is-flat {
      color: #909399;
    }
  }
  
  &__decoration {
    position: absolute;
    right: -20px;
    bottom: -20px;
    width: 100px;
    height: 100px;
    border-radius: 50%;
    background: linear-gradient(135deg, rgba(64, 158, 255, 0.1) 0%, rgba(64, 158, 255, 0.05) 100%);
    opacity: 0.5;
  }
}

// 响应式适配
@media (max-width: 1400px) {
  .stat-card {
    padding: 20px;
    
    &__icon {
      width: 56px;
      height: 56px;
      
      i {
        font-size: 28px;
      }
    }
    
    &__value .value-number {
      font-size: 28px;
    }
  }
}

@media (max-width: 1200px) {
  .stat-card {
    padding: 16px;
    
    &__icon {
      width: 48px;
      height: 48px;
      border-radius: 12px;
      
      i {
        font-size: 24px;
      }
    }
    
    &__content {
      margin-left: 16px;
    }
    
    &__value .value-number {
      font-size: 24px;
    }
    
    &__title {
      font-size: 13px;
    }
  }
}
</style>
