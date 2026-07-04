import { describe, it, expect } from 'vitest'
import { buildAuthHeader } from './httpAuth'

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
})
