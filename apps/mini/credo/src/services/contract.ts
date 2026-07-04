import Taro from '@tarojs/taro'
import { getApiBaseUrl } from '@/constants/api'
import { buildAuthHeader } from '@/utils/httpAuth'
import type { SleepContractDto, SleepContractUpsertPayload } from '@/utils/contractForm'
import { getStoredToken, type ApiResponse } from './auth'

export { buildAuthHeader } from '@/utils/httpAuth'

export async function getMySleepContract (): Promise<SleepContractDto | null> {
  const token = getStoredToken()
  const response = await Taro.request<ApiResponse<SleepContractDto | null>>({
    url: `${getApiBaseUrl()}/api/contracts/sleep/mine`,
    method: 'GET',
    header: buildAuthHeader(token),
  })

  const body = response.data
  if (!body.success) {
    throw new Error(body.message || 'Failed to load contract')
  }
  return body.data ?? null
}

export async function upsertSleepContract (payload: SleepContractUpsertPayload): Promise<SleepContractDto> {
  const token = getStoredToken()
  const response = await Taro.request<ApiResponse<SleepContractDto>>({
    url: `${getApiBaseUrl()}/api/contracts/sleep`,
    method: 'PUT',
    header: buildAuthHeader(token),
    data: payload,
  })

  const body = response.data
  if (!body.success || !body.data) {
    throw new Error(body.message || 'Failed to save contract')
  }
  return body.data
}
