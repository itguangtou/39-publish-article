import { request } from '@/utils/http'

import type { LoginParams, LoginResult } from './types'

export function loginApi(data: LoginParams) {
  return request<Api.Result<LoginResult>>({
    url: '/auth/login',
    method: 'post',
    data,
  })
}
