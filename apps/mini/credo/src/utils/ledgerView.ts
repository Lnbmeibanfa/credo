import { CreditEventType } from '@/constants/creditEventTypes'

export type DailyStatus = 'PENDING' | 'FULFILLED' | 'BREACH' | 'UPCOMING'

export interface SleepDailyDay {
  recordDate: string
  status: DailyStatus
  eventType?: 'FULFILLED' | 'BREACH'
  createdAt?: string
}

export interface SleepLedgerSummary {
  obligationDays: number
  recordedDays: number
  pendingDays: number
  fulfilledDays: number
  breachDays: number
}

export interface SleepDailyView {
  contractId: number
  summary: SleepLedgerSummary
  days: SleepDailyDay[]
}

export interface SleepLedgerEvent {
  id: number
  contractId: number
  recordDate: string
  eventType: 'FULFILLED' | 'BREACH'
  note?: string | null
  createdAt: string
}

export interface SleepRecordPayload {
  recordDate: string
  eventType: 'FULFILLED' | 'BREACH'
  note?: string
}

export interface SleepDailyViewQuery {
  from?: string
  to?: string
  status?: string
}

const WEEKDAY_LABELS = ['日', '一', '二', '三', '四', '五', '六']

export function mapDailyStatusToCreditEventType (status: DailyStatus): CreditEventType {
  return status as CreditEventType
}

export function formatRecordDateDisplay (recordDate: string): string {
  const match = /^(\d{4})-(\d{2})-(\d{2})$/.exec(recordDate)
  if (!match) {
    return recordDate
  }
  return `${match[2]}.${match[3]}`
}

export function getWeekdayLabel (recordDate: string): string {
  const match = /^(\d{4})-(\d{2})-(\d{2})$/.exec(recordDate)
  if (!match) {
    return ''
  }
  const date = new Date(Number(match[1]), Number(match[2]) - 1, Number(match[3]))
  return `周${WEEKDAY_LABELS[date.getDay()]}`
}

export function getDayTitle (status: DailyStatus): string {
  switch (status) {
    case 'FULFILLED':
      return '睡眠履约'
    case 'BREACH':
      return '睡眠未履约'
    case 'UPCOMING':
      return '尚未到期'
    default:
      return '待登记睡眠状态'
  }
}

export function getDayDescription (day: SleepDailyDay): string {
  if (day.status === 'FULFILLED') {
    return '已登记为履约，记录不可修改。'
  }
  if (day.status === 'BREACH') {
    return '已登记为未履约，记录不可修改。'
  }
  if (day.status === 'UPCOMING') {
    return '该日尚未到来，暂不可登记。'
  }
  return '请点击登记今日履约或未履约。'
}

export function isDayActionable (status: DailyStatus): boolean {
  return status === 'PENDING'
}

export function buildRecordPayload (
  recordDate: string,
  eventType: 'FULFILLED' | 'BREACH'
): SleepRecordPayload {
  return { recordDate, eventType }
}

export function sortDaysForDisplay (days: SleepDailyDay[]): SleepDailyDay[] {
  return [...days].sort((a, b) => b.recordDate.localeCompare(a.recordDate))
}

export function formatIsoDate (date: Date): string {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

export function buildPendingQueueQuery (today?: string): Required<Pick<SleepDailyViewQuery, 'status' | 'to'>> {
  return {
    status: 'PENDING',
    to: today ?? formatIsoDate(new Date()),
  }
}

export function buildLedgerTimelineQuery (today?: string): Pick<SleepDailyViewQuery, 'to'> {
  return {
    to: today ?? formatIsoDate(new Date()),
  }
}
