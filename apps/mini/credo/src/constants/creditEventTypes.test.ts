import { describe, it, expect } from 'vitest'
import {
  CreditEventType,
  CREDIT_EVENT_VISUALS,
  getCreditEventVisual,
} from '../constants/creditEventTypes'

describe('creditEventTypes visual mapping', () => {
  it('maps FULFILLED to plus icon and green color', () => {
    const visual = getCreditEventVisual(CreditEventType.FULFILLED)
    expect(visual.icon).toBe('+')
    expect(visual.label).toBe('履约')
    expect(visual.color).toBe('#2d6a4f')
  })

  it('maps BREACH to minus icon and breach color', () => {
    const visual = getCreditEventVisual(CreditEventType.BREACH)
    expect(visual.icon).toBe('-')
    expect(visual.label).toBe('未履约')
    expect(visual.color).toBe('#8b4049')
  })

  it('defines visuals for all event types', () => {
    const types = Object.values(CreditEventType)
    types.forEach((type) => {
      expect(CREDIT_EVENT_VISUALS[type]).toBeDefined()
      expect(CREDIT_EVENT_VISUALS[type].icon).toBeTruthy()
      expect(CREDIT_EVENT_VISUALS[type].label).toBeTruthy()
    })
  })
})
