import { ElMessage, type MessageHandler, type MessageOptions } from 'element-plus'

type FeedbackType = 'success' | 'warning' | 'error' | 'info'

const DEFAULT_OPTIONS: MessageOptions = {
  duration: 2800,
  showClose: true,
  grouping: true,
  offset: 64,
  customClass: 'admin-toast',
}

let activeHandler: MessageHandler | null = null

function show(type: FeedbackType, message: string) {
  activeHandler?.close()
  activeHandler = ElMessage({
    ...DEFAULT_OPTIONS,
    type,
    message,
  })
  return activeHandler
}

export const notify = {
  success: (message: string) => show('success', message),
  warning: (message: string) => show('warning', message),
  error: (message: string) => show('error', message),
  info: (message: string) => show('info', message),
}
