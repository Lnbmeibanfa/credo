export type BreachClauseType = 'RECORD' | 'REVIEW' | 'CUSTOM'

export interface BreachClauseFormItem {
  type: BreachClauseType
  enabled: boolean
  contentText?: string
}

export interface SleepContractFormState {
  targetBedtime: string
  startDate: string
  endDate: string
  breachClauses: BreachClauseFormItem[]
}

export interface SleepContractDto {
  id: number
  contractNo: string
  targetBedtime: string
  startDate: string
  endDate: string
  breachClauses: BreachClauseFormItem[]
  signedAt: string | null
}

export interface SleepContractUpsertPayload {
  targetBedtime: string
  startDate: string
  endDate: string
  breachClauses: BreachClauseFormItem[]
}

export const BREACH_CLAUSE_LABELS: Record<BreachClauseType, string> = {
  RECORD: '记录档案',
  REVIEW: '进行复盘',
  CUSTOM: '自定义条款',
}

export const BREACH_CLAUSE_DEFAULTS: Record<BreachClauseType, string> = {
  RECORD: '违约事件将永久写入信用账本，不可撤销。',
  REVIEW: '违约后次日须完成 3 题复盘问卷。',
  CUSTOM: '',
}

export const IRREVOCABLE_CLAUSE_TEXT =
  '本契约一经签署，在生效期内对缔约方具有约束力；违约记录写入信用账本后不可撤销。'

function formatDate (date: Date): string {
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}

export function createDefaultFormState (baseDate: Date = new Date()): SleepContractFormState {
  const start = new Date(baseDate)
  const end = new Date(baseDate)
  end.setDate(end.getDate() + 29)

  return {
    targetBedtime: '23:00',
    startDate: formatDate(start),
    endDate: formatDate(end),
    breachClauses: [
      { type: 'RECORD', enabled: true },
      { type: 'REVIEW', enabled: true },
      { type: 'CUSTOM', enabled: false, contentText: '' },
    ],
  }
}

export function computeDurationDays (startDate: string, endDate: string): number {
  if (!startDate || !endDate) {
    return 0
  }
  const start = parseIsoDate(startDate)
  const end = parseIsoDate(endDate)
  if (!start || !end || end < start) {
    return 0
  }
  const diffMs = end.getTime() - start.getTime()
  return Math.floor(diffMs / (24 * 60 * 60 * 1000)) + 1
}

export function isDateRangeValid (startDate: string, endDate: string): boolean {
  return computeDurationDays(startDate, endDate) > 0
}

/** Keep endDate on or after startDate when either boundary changes. */
export function applyDateRangeUpdate (
  startDate: string,
  endDate: string,
  field: 'startDate' | 'endDate',
  value: string
): { startDate: string; endDate: string } {
  if (field === 'startDate') {
    let nextEnd = endDate
    if (!isDateRangeValid(value, nextEnd)) {
      nextEnd = value
    }
    return { startDate: value, endDate: nextEnd }
  }

  if (!isDateRangeValid(startDate, value)) {
    return { startDate, endDate: startDate }
  }
  return { startDate, endDate: value }
}

function parseIsoDate (value: string): Date | null {
  const match = /^(\d{4})-(\d{2})-(\d{2})$/.exec(value)
  if (!match) {
    return null
  }
  const date = new Date(Number(match[1]), Number(match[2]) - 1, Number(match[3]))
  return Number.isNaN(date.getTime()) ? null : date
}

export function validateSleepContractForm (form: SleepContractFormState): string | null {
  if (!form.targetBedtime || !/^\d{2}:\d{2}$/.test(form.targetBedtime)) {
    return '请选择目标入眠时间'
  }
  if (!form.startDate || !form.endDate) {
    return '请选择生效起止日期'
  }
  if (computeDurationDays(form.startDate, form.endDate) <= 0) {
    return '结束日期须晚于或等于开始日期'
  }
  const record = form.breachClauses.find((clause) => clause.type === 'RECORD')
  if (!record?.enabled) {
    return '记录档案条款为必选项'
  }
  const custom = form.breachClauses.find((clause) => clause.type === 'CUSTOM')
  if (custom?.enabled && !custom.contentText?.trim()) {
    return '请填写自定义违约条款'
  }
  return null
}

export function mapDtoToForm (dto: SleepContractDto): SleepContractFormState {
  return {
    targetBedtime: dto.targetBedtime,
    startDate: dto.startDate,
    endDate: dto.endDate,
    breachClauses: dto.breachClauses.map((clause) => ({
      type: clause.type,
      enabled: clause.enabled,
      contentText: clause.contentText ?? '',
    })),
  }
}

export function mapFormToPayload (form: SleepContractFormState): SleepContractUpsertPayload {
  return {
    targetBedtime: form.targetBedtime,
    startDate: form.startDate,
    endDate: form.endDate,
    breachClauses: form.breachClauses.map((clause) => ({
      type: clause.type,
      enabled: clause.type === 'RECORD' ? true : clause.enabled,
      contentText: clause.type === 'CUSTOM' ? clause.contentText?.trim() || '' : undefined,
    })),
  }
}

export function formatDisplayDate (isoDate: string): string {
  const date = parseIsoDate(isoDate)
  if (!date) {
    return isoDate
  }
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}.${m}.${d}`
}

export function formatBedtimeDisplay (time: string): string {
  return time.replace(':', ' : ')
}

export function getEnabledClauseTexts (clauses: BreachClauseFormItem[]): string[] {
  return clauses
    .filter((clause) => clause.enabled)
    .map((clause) => {
      if (clause.type === 'CUSTOM') {
        return clause.contentText?.trim() || BREACH_CLAUSE_DEFAULTS.CUSTOM
      }
      return BREACH_CLAUSE_DEFAULTS[clause.type]
    })
}
