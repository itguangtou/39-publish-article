import { HttpStatus } from '@/enums/httpEnum'

export function checkStatus(status: number): string {
  switch (status) {
    case HttpStatus.UNAUTHORIZED:
      return '登录已过期，请重新登录'
    case HttpStatus.FORBIDDEN:
      return '拒绝访问'
    case HttpStatus.NOT_FOUND:
      return '请求资源不存在'
    case HttpStatus.SERVER_ERROR:
      return '服务器错误'
    default:
      return `请求失败(${status})`
  }
}
