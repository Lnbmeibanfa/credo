/** Injected by Taro defineConstants at build time */
declare const API_BASE_URL: string

export function getApiBaseUrl (): string {
  if (typeof API_BASE_URL !== 'undefined' && API_BASE_URL) {
    return API_BASE_URL
  }
  return 'http://localhost:8080'
}
