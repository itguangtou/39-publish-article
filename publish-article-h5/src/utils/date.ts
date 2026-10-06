import dayjs from 'dayjs'

export function formatDate(value: string | number | Date, pattern = 'YYYY-MM-DD HH:mm:ss') {
  return dayjs(value).format(pattern)
}
