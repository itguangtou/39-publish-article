export enum HttpStatus {
  OK = 200,
  UNAUTHORIZED = 401,
  FORBIDDEN = 403,
  NOT_FOUND = 404,
  SERVER_ERROR = 500,
}

export enum ContentTypeEnum {
  JSON = 'application/json;charset=UTF-8',
  FORM_DATA = 'multipart/form-data',
}
