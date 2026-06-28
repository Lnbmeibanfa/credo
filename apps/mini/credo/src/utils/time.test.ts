import { describe, it, expect } from 'vitest'
import {
  formatCountdown,
  parseTimeString,
  getRemainingSeconds,
} from './time'

describe('formatCountdown', () => {
  it('formats seconds as HH : mm : ss', () => {
    expect(formatCountdown(10000)).toBe('02 : 46 : 40')
  })

  it('zero-pads single digits', () => {
    expect(formatCountdown(3661)).toBe('01 : 01 : 01')
  })

  it('returns zeros for zero input', () => {
    expect(formatCountdown(0)).toBe('00 : 00 : 00')
  })
})

describe('parseTimeString', () => {
  it('parses HH:mm into today date', () => {
    const base = new Date('2026-06-27T00:00:00')
    const result = parseTimeString('23:30', base)
    expect(result.getHours()).toBe(23)
    expect(result.getMinutes()).toBe(30)
    expect(result.getSeconds()).toBe(0)
  })
})

describe('getRemainingSeconds', () => {
  it('returns positive seconds when target is in future', () => {
    const now = new Date('2026-06-27T20:00:00')
    const target = new Date('2026-06-27T23:30:00')
    expect(getRemainingSeconds(target, now)).toBe(12600)
  })

  it('returns 0 when target is in past', () => {
    const now = new Date('2026-06-27T23:30:00')
    const target = new Date('2026-06-27T20:00:00')
    expect(getRemainingSeconds(target, now)).toBe(0)
  })
})
