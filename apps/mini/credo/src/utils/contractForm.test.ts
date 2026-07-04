import { describe, it, expect } from 'vitest'
import {
  applyDateRangeUpdate,
  computeDurationDays,
  createDefaultFormState,
  mapDtoToForm,
  mapFormToPayload,
  validateSleepContractForm,
  type SleepContractDto,
} from './contractForm'

describe('contractForm utils', () => {
  describe('computeDurationDays', () => {
    it('returns inclusive day count', () => {
      expect(computeDurationDays('2026-06-24', '2026-07-23')).toBe(30)
    })

    it('returns 1 when start and end are the same day', () => {
      expect(computeDurationDays('2026-06-24', '2026-06-24')).toBe(1)
    })

    it('returns 0 for invalid range', () => {
      expect(computeDurationDays('2026-07-01', '2026-06-01')).toBe(0)
    })
  })

  describe('applyDateRangeUpdate', () => {
    it('bumps end date when start moves past end', () => {
      expect(applyDateRangeUpdate('2026-06-01', '2026-06-15', 'startDate', '2026-06-20')).toEqual({
        startDate: '2026-06-20',
        endDate: '2026-06-20',
      })
    })

    it('rejects end date before start', () => {
      expect(applyDateRangeUpdate('2026-06-10', '2026-06-20', 'endDate', '2026-06-01')).toEqual({
        startDate: '2026-06-10',
        endDate: '2026-06-10',
      })
    })
  })

  describe('validateSleepContractForm', () => {
    it('accepts valid form', () => {
      const form = createDefaultFormState(new Date('2026-06-24'))
      expect(validateSleepContractForm(form)).toBeNull()
    })

    it('rejects missing bedtime', () => {
      const form = createDefaultFormState()
      form.targetBedtime = ''
      expect(validateSleepContractForm(form)).toBe('请选择目标入眠时间')
    })

    it('rejects enabled custom clause without text', () => {
      const form = createDefaultFormState()
      const custom = form.breachClauses.find((c) => c.type === 'CUSTOM')
      if (custom) {
        custom.enabled = true
        custom.contentText = '   '
      }
      expect(validateSleepContractForm(form)).toBe('请填写自定义违约条款')
    })
  })

  describe('DTO mapping', () => {
    it('maps DTO to form and back to payload', () => {
      const dto: SleepContractDto = {
        id: 1,
        contractNo: 'C-20260624-001',
        targetBedtime: '22:30',
        startDate: '2026-06-24',
        endDate: '2026-07-23',
        signedAt: '2026-06-24T12:00:00',
        breachClauses: [
          { type: 'RECORD', enabled: true },
          { type: 'REVIEW', enabled: false },
          { type: 'CUSTOM', enabled: true, contentText: '连续 3 次' },
        ],
      }

      const form = mapDtoToForm(dto)
      expect(form.targetBedtime).toBe('22:30')

      const payload = mapFormToPayload(form)
      expect(payload.targetBedtime).toBe('22:30')
      expect(payload.breachClauses).toEqual([
        { type: 'RECORD', enabled: true, contentText: undefined },
        { type: 'REVIEW', enabled: false, contentText: undefined },
        { type: 'CUSTOM', enabled: true, contentText: '连续 3 次' },
      ])
    })
  })
})
