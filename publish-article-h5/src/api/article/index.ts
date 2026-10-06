import { request } from '@/utils/http'

import type { ArticleDetail, ArticleItem } from './types'

export interface ArticleListParams {
  current?: number
  size?: number
  keyword?: string
}

export function getArticleListApi(params?: ArticleListParams) {
  return request<Api.PageResult<ArticleItem>>({
    url: '/articles',
    method: 'get',
    params,
  })
}

export function getArticleBySlugApi(slug: string) {
  return request<Api.Result<ArticleDetail>>({
    url: `/articles/slug/${slug}`,
    method: 'get',
  })
}

export function createArticleApi(formData: FormData) {
  return request<Api.Result<ArticleItem>>({
    url: '/articles',
    method: 'post',
    data: formData,
  })
}

export function updateArticleApi(id: number, formData: FormData) {
  return request<Api.Result<ArticleItem>>({
    url: `/articles/${id}`,
    method: 'put',
    data: formData,
  })
}

export function deleteArticleApi(id: number) {
  return request<Api.Result<void>>({
    url: `/articles/${id}`,
    method: 'delete',
  })
}
