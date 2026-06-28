import { describe, it, expect, vi, beforeEach } from 'vitest'
import {
  AUTH_TOKEN_KEY,
  buildPhoneLoginPayload,
  getStoredToken,
  parseStoredToken,
  setStoredToken,
} from './auth'

vi.mock('@tarojs/taro', () => ({
  default: {
    getStorageSync: vi.fn(),
    setStorageSync: vi.fn(),
    login: vi.fn(),
    request: vi.fn(),
  },
}))

describe('auth service', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('buildPhoneLoginPayload', () => {
    it('includes both loginCode and phoneCode', () => {
      expect(buildPhoneLoginPayload('login-abc', 'phone-xyz')).toEqual({
        loginCode: 'login-abc',
        phoneCode: 'phone-xyz',
      })
    })
  })

  describe('parseStoredToken', () => {
    it('returns token for non-empty string', () => {
      expect(parseStoredToken('jwt-token')).toBe('jwt-token')
    })

    it('returns null for empty or invalid values', () => {
      expect(parseStoredToken('')).toBeNull()
      expect(parseStoredToken(null)).toBeNull()
      expect(parseStoredToken(undefined)).toBeNull()
    })
  })

  describe('token storage helpers', () => {
    it('reads token from Taro storage', async () => {
      const Taro = (await import('@tarojs/taro')).default
      vi.mocked(Taro.getStorageSync).mockReturnValue('stored-token')

      expect(getStoredToken()).toBe('stored-token')
      expect(Taro.getStorageSync).toHaveBeenCalledWith(AUTH_TOKEN_KEY)
    })

    it('writes token to Taro storage', async () => {
      const Taro = (await import('@tarojs/taro')).default
      setStoredToken('new-token')

      expect(Taro.setStorageSync).toHaveBeenCalledWith(AUTH_TOKEN_KEY, 'new-token')
    })
  })
})
