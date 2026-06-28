import { describe, it, expect } from 'vitest'
import { WELCOME_CONTENT } from '../constants/welcomeContent'

describe('welcomeContent', () => {
  it('defines required content keys', () => {
    expect(WELCOME_CONTENT.heroLines).toHaveLength(2)
    expect(WELCOME_CONTENT.quoteLines.length).toBeGreaterThan(0)
    expect(WELCOME_CONTENT.ctaLabel).toBeTruthy()
    expect(WELCOME_CONTENT.authButtonLabel).toBeTruthy()
    expect(WELCOME_CONTENT.authHintText).toBeTruthy()
    expect(WELCOME_CONTENT.footerLabel).toBeTruthy()
    expect(WELCOME_CONTENT.brandTitle).toBeTruthy()
    expect(WELCOME_CONTENT.monoLabel).toBeTruthy()
    expect(WELCOME_CONTENT.hintText).toBeTruthy()
  })
})
