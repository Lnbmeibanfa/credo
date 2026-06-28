import Taro from '@tarojs/taro'
import { getApiBaseUrl } from '@/constants/api'

export const AUTH_TOKEN_KEY = 'credo_auth_token'

export interface PhoneLoginPayload {
  loginCode: string
  phoneCode: string
}

export interface AuthUser {
  id: number
  phone: string
  nickname: string | null
  avatarUrl: string | null
}

export interface PhoneLoginData {
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

export function buildPhoneLoginPayload (loginCode: string, phoneCode: string): PhoneLoginPayload {
  return { loginCode, phoneCode }
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

export function hasStoredToken (): boolean {
  return getStoredToken() !== null
}

export async function phoneLogin (phoneCode: string): Promise<PhoneLoginData> {
  const loginResult = await Taro.login()
  if (!loginResult.code) {
    throw new Error('wx.login failed')
  }

  const payload = buildPhoneLoginPayload(loginResult.code, phoneCode)
  const response = await Taro.request<ApiResponse<PhoneLoginData>>({
    url: `${getApiBaseUrl()}/api/auth/mini/phone-login`,
    method: 'POST',
    header: { 'Content-Type': 'application/json' },
    data: payload,
  })

  const body = response.data
  if (!body.success || !body.data?.token) {
    throw new Error(body.message || 'Phone login failed')
  }

  setStoredToken(body.data.token)
  return body.data
}
