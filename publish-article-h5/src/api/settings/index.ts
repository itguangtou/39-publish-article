import { request } from '@/utils/http'

import type { SystemSettings } from './types'

export function getSystemSettingsApi() {
  return request<Api.Result<SystemSettings>>({
    url: '/settings',
    method: 'get',
  })
}

export function updateSystemSettingsApi(formData: FormData) {
  return request<Api.Result<SystemSettings>>({
    url: '/settings',
    method: 'put',
    data: formData,
  })
}
