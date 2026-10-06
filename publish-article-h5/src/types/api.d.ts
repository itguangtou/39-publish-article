declare namespace Api {
  interface Result<T = unknown> {
    code: number
    message: string
    data: T
  }

  interface PageResult<T = unknown> {
    code: number
    message: string
    data: {
      records: T[]
      total: number
      current: number
      size: number
    }
  }
}
