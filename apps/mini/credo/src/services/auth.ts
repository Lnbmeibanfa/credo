import Taro from '@tarojs/taro'
import { getApiBaseUrl } from '@/constants/api'
import { isUnauthorizedResponse } from '@/utils/httpAuth'

export const AUTH_TOKEN_KEY = 'credo_auth_token'

export interface WeChatLoginPayload {
  loginCode: string
}

export interface AuthUser {
  id: number
  phone: string | null
  nickname: string | null
  avatarUrl: string | null
}

export interface WeChatLoginData {
  token: string
  user: AuthUser
  isNewUser: boolean
}

export interface ApiResponse<T> {
  success: boolean
  code: string
  message: string
  data: T
}

export function buildWeChatLoginPayload (loginCode: string): WeChatLoginPayload {
  return { loginCode }
}

export function parseStoredToken (raw: unknown): string | null {
  if (typeof raw !== 'string' || raw.length === 0) {
    return null
  }
  return raw
}

export function getStoredToken (): string | null {
  return parseStoredToken(Taro.getStorageSync(AUTH_TOKEN_KEY))
}

export function setStoredToken (token: string): void {
  Taro.setStorageSync(AUTH_TOKEN_KEY, token)
}

export function clearStoredToken (): void {
  Taro.removeStorageSync(AUTH_TOKEN_KEY)
}

export function hasStoredToken (): boolean {
  return getStoredToken() !== null
}

export function handleUnauthorizedSession (): void {
  clearStoredToken()
  Taro.showToast({ title: '登录已过期，请重新登录', icon: 'none' })

  const pages = Taro.getCurrentPages()
  const currentRoute = pages[pages.length - 1]?.route ?? ''
  if (!currentRoute.includes('pages/index/index')) {
    setTimeout(() => {
      Taro.redirectTo({ url: '/pages/index/index' })
    }, 800)
  }
}

export function ensureAuthorized (
  statusCode: number,
  body: { code?: string } | undefined,
): void {
  if (isUnauthorizedResponse(statusCode, body)) {
    handleUnauthorizedSession()
    throw new Error('Unauthorized')
  }
}

export async function wechatLogin (): Promise<WeChatLoginData> {
  const loginResult = await Taro.login()
  if (!loginResult.code) {
    throw new Error('wx.login failed')
  }

  const payload = buildWeChatLoginPayload(loginResult.code)
  const response = await Taro.request<ApiResponse<WeChatLoginData>>({
    url: `${getApiBaseUrl()}/api/auth/mini/wechat-login`,
    method: 'POST',
    header: { 'Content-Type': 'application/json' },
    data: payload,
  })

  const body = response.data
  if (!body.success || !body.data?.token) {
    throw new Error(body.message || 'WeChat login failed')
  }

  setStoredToken(body.data.token)
  return body.data
}
