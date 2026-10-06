export interface ArticleAuthor {
  avatar: string
  nickname: string
  intro: string
}

export interface ArticleItem {
  id: number
  title: string
  slug: string
  coverUrl?: string
  images?: string[]
  promotionLink: string
  createdAt: string
  author?: ArticleAuthor
}

export interface ArticleDetail extends ArticleItem {
  images: string[]
  publishedAgo?: string
}

export interface ImageMetaItem {
  type: 'keep' | 'new'
  path?: string
  index?: number
}

export interface ArticleSubmitPayload {
  title: string
  imageMeta: ImageMetaItem[]
  newFiles: File[]
}
