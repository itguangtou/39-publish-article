const CHARS = 'abcdefghijklmnopqrstuvwxyz0123456789'

/** 生成随机 slug，用于推广链接，不易被猜测 */
export function generateRandomSlug(length = 12): string {
  const array = new Uint8Array(length)
  crypto.getRandomValues(array)
  return Array.from(array, (n) => CHARS[n % CHARS.length]).join('')
}
