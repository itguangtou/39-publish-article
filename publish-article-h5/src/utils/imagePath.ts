/** 从公开 URL 提取 data 目录下的相对路径，如 articles/12/xxx.webp */
export function toRelativeImagePath(url: string): string {
  const marker = '/data/'
  const index = url.indexOf(marker)
  if (index >= 0) {
    return url.substring(index + marker.length)
  }
  return url.replace(/^\/+/, '')
}
