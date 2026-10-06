import type { App } from 'vue'

import 'element-plus/es/components/message/style/css'
import 'element-plus/es/components/message-box/style/css'
import 'element-plus/es/components/notification/style/css'

export function setupElementPlus(_app: App) {
  // 组件按需自动导入；Message/MessageBox 样式在此全局注册
}
