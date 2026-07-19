import Taro from '@tarojs/taro'
import { getApiBaseUrl } from '@/constants/api'
import { buildAuthHeader } from '@/utils/httpAuth'
import type {
  SleepDailyView,
  SleepLedgerEvent,
  SleepLedgerSummary,
  SleepRecordPayload,
} from '@/utils/ledgerView'
import { ensureAuthorized, getStoredToken, type ApiResponse } from './auth'

export { buildRecordPayload } from '@/utils/ledgerView'

export async function recordSleepDay (payload: SleepRecordPayload): Promise<SleepLedgerEvent> {
  const token = getStoredToken()
  const response = await Taro.request<ApiResponse<SleepLedgerEvent>>({
    url: `${getApiBaseUrl()}/api/ledger/sleep/records`,
    method: 'POST',
    header: buildAuthHeader(token),
    data: payload,
  })

  const body = response.data
  ensureAuthorized(response.statusCode, body)
  if (!body.success || !body.data) {
    throw new Error(body.message || 'Failed to record sleep day')
  }
  return body.data
}

export async function getSleepDailyView (params?: {
  from?: string
  to?: string
  status?: string
}): Promise<SleepDailyView | null> {
  const token = getStoredToken()
  const response = await Taro.request<ApiResponse<SleepDailyView>>({
    url: `${getApiBaseUrl()}/api/ledger/sleep/daily-view`,
    method: 'GET',
    header: buildAuthHeader(token),
    data: params,
  })

  const body = response.data
  ensureAuthorized(response.statusCode, body)
  if (!body.success) {
    if (body.code === 'NO_CONTRACT') {
      return null
    }
    throw new Error(body.message || 'Failed to load daily view')
  }
  return body.data ?? null
}

export async function getSleepLedgerSummary (): Promise<SleepLedgerSummary> {
  const token = getStoredToken()
  const response = await Taro.request<ApiResponse<SleepLedgerSummary>>({
    url: `${getApiBaseUrl()}/api/ledger/sleep/summary`,
    method: 'GET',
    header: buildAuthHeader(token),
  })

  const body = response.data
  ensureAuthorized(response.statusCode, body)
  if (!body.success || !body.data) {
    throw new Error(body.message || 'Failed to load ledger summary')
  }
  return body.data
}
