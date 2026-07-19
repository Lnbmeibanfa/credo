import { describe, it, expect } from 'vitest'
import { buildAuthHeader, isUnauthorizedResponse } from './httpAuth'

describe('httpAuth utils', () => {
  describe('buildAuthHeader', () => {
    it('includes Authorization when token is present', () => {
      expect(buildAuthHeader('jwt-token')).toEqual({
        'Content-Type': 'application/json',
        Authorization: 'Bearer jwt-token',
      })
    })

    it('omits Authorization when token is missing', () => {
      expect(buildAuthHeader(null)).toEqual({
        'Content-Type': 'application/json',
      })
    })
  })

  describe('isUnauthorizedResponse', () => {
    it('returns true for HTTP 401', () => {
      expect(isUnauthorizedResponse(401, { code: 'UNAUTHORIZED' })).toBe(true)
    })

    it('returns true for UNAUTHORIZED code even when status is 200', () => {
      expect(isUnauthorizedResponse(200, { code: 'UNAUTHORIZED' })).toBe(true)
    })

    it('returns false for other errors', () => {
      expect(isUnauthorizedResponse(400, { code: 'INVALID_PARAMETER' })).toBe(false)
    })
  })
})
