export function getStorage<T>(key: string): T | null {
  const value = localStorage.getItem(key)
  if (!value) return null
  try {
    return JSON.parse(value) as T
  } catch {
    return value as T
  }
}

export function setStorage(key: string, value: unknown) {
  localStorage.setItem(key, typeof value === 'string' ? value : JSON.stringify(value))
}

export function removeStorage(key: string) {
  localStorage.removeItem(key)
}
