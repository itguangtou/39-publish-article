import axios, { type AxiosInstance, type AxiosRequestConfig } from 'axios'

import { HttpStatus } from '@/enums/httpEnum'
import { useUserStore } from '@/store/modules/user'

import { checkStatus } from './checkStatus'
import { handleUnauthorized } from './handleUnauthorized'
import { RequestError } from './requestError'

const service: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 15000,
})

service.interceptors.request.use((config) => {
  const userStore = useUserStore()
  if (userStore.token) {
    config.headers.Authorization = `Bearer ${userStore.token}`
  }
  return config
})

function rejectUnauthorized(message: string) {
  handleUnauthorized()
  return Promise.reject(new RequestError(message, true))
}

service.interceptors.response.use(
  (response) => {
    const data = response.data as Api.Result<unknown>
    if (data?.code !== undefined && data.code !== 200) {
      if (data.code === HttpStatus.UNAUTHORIZED) {
        return rejectUnauthorized(data.message || '登录已过期，请重新登录')
      }
      return Promise.reject(new RequestError(data.message || '请求失败'))
    }
    return response.data
  },
  (error) => {
    const status = error.response?.status as number | undefined
    const data = error.response?.data as Api.Result<unknown> | undefined
    const serverMessage = data?.message

    if (status === HttpStatus.UNAUTHORIZED) {
      return rejectUnauthorized(serverMessage || checkStatus(HttpStatus.UNAUTHORIZED))
    }

    const message =
      serverMessage ||
      (status ? checkStatus(status) : error.message || '网络异常，请稍后重试')
    return Promise.reject(new RequestError(message))
  },
)

export function request<T = unknown>(config: AxiosRequestConfig): Promise<T> {
  return service.request(config) as Promise<T>
}

export default service
