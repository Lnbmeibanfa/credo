import { describe, it, expect } from 'vitest'
import { CreditEventType } from '@/constants/creditEventTypes'
import {
  formatRecordDateDisplay,
  getDayDescription,
  getDayTitle,
  getWeekdayLabel,
  isDayActionable,
  mapDailyStatusToCreditEventType,
  sortDaysForDisplay,
  buildRecordPayload,
  buildPendingQueueQuery,
  formatIsoDate,
  type SleepDailyDay,
} from './ledgerView'

describe('ledgerView utils', () => {
  it('formats record date as MM.DD', () => {
    expect(formatRecordDateDisplay('2026-06-28')).toBe('06.28')
  })

  it('returns weekday label', () => {
    expect(getWeekdayLabel('2026-06-28')).toBe('周日')
  })

  it('maps daily status to credit event type', () => {
    expect(mapDailyStatusToCreditEventType('PENDING')).toBe(CreditEventType.PENDING)
    expect(mapDailyStatusToCreditEventType('UPCOMING')).toBe(CreditEventType.UPCOMING)
  })

  it('identifies actionable pending days only', () => {
    expect(isDayActionable('PENDING')).toBe(true)
    expect(isDayActionable('FULFILLED')).toBe(false)
    expect(isDayActionable('UPCOMING')).toBe(false)
  })

  it('builds titles and descriptions by status', () => {
    expect(getDayTitle('PENDING')).toContain('待登记')
    expect(getDayDescription({ recordDate: '2026-06-28', status: 'UPCOMING' })).toContain('尚未到来')
  })

  it('sorts days descending by date', () => {
    const days: SleepDailyDay[] = [
      { recordDate: '2026-06-26', status: 'PENDING' },
      { recordDate: '2026-06-28', status: 'PENDING' },
    ]
    expect(sortDaysForDisplay(days).map((d) => d.recordDate)).toEqual(['2026-06-28', '2026-06-26'])
  })

  it('builds record payload for API', () => {
    expect(buildRecordPayload('2026-06-28', 'BREACH')).toEqual({
      recordDate: '2026-06-28',
      eventType: 'BREACH',
    })
  })

  it('builds pending queue query with explicit today', () => {
    expect(buildPendingQueueQuery('2026-07-04')).toEqual({
      status: 'PENDING',
      to: '2026-07-04',
    })
  })

  it('formats ISO date', () => {
    expect(formatIsoDate(new Date(2026, 6, 4))).toBe('2026-07-04')
  })
})
