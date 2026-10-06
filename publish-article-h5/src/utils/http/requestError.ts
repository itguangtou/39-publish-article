export class RequestError extends Error {
  isUnauthorized: boolean

  constructor(message: string, isUnauthorized = false) {
    super(message)
    this.name = 'RequestError'
    this.isUnauthorized = isUnauthorized
  }
}

export function isUnauthorizedError(error: unknown): boolean {
  return error instanceof RequestError && error.isUnauthorized
}
