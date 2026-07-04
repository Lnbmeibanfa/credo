import { View, Text } from '@tarojs/components'
import type { BreachClauseFormItem } from '../../../utils/contractForm'
import {
  formatBedtimeDisplay,
  formatDisplayDate,
  getEnabledClauseTexts,
  IRREVOCABLE_CLAUSE_TEXT,
  computeDurationDays,
} from '../../../utils/contractForm'
import { MonoLabel, SansText, SerifTitle } from '../../typography'
import './index.scss'

export type SleepContractDocumentMode = 'preview' | 'signed'

interface SleepContractDocumentProps {
  mode: SleepContractDocumentMode
  targetBedtime: string
  startDate: string
  endDate: string
  breachClauses: BreachClauseFormItem[]
  contractNo?: string
  signedAt?: string | null
  className?: string
}

function formatSignedAt (signedAt: string | null | undefined): string {
  if (!signedAt) {
    return ''
  }
  const normalized = signedAt.replace('T', ' ').slice(0, 16)
  return normalized
}

export default function SleepContractDocument ({
  mode,
  targetBedtime,
  startDate,
  endDate,
  breachClauses,
  contractNo,
  signedAt,
  className = '',
}: SleepContractDocumentProps) {
  const durationDays = computeDurationDays(startDate, endDate)
  const clauseTexts = getEnabledClauseTexts(breachClauses)

  return (
    <View className={`credo-sleep-contract-document ${className}`.trim()}>
      <View className='credo-sleep-contract-document__header'>
        <MonoLabel>PERSONAL SLEEP CONTRACT</MonoLabel>
        <SerifTitle>睡眠契约</SerifTitle>
        {contractNo && (
          <SansText variant='caption' className='credo-sleep-contract-document__no'>
            NO. {contractNo}
          </SansText>
        )}
      </View>

      <View className='credo-sleep-contract-document__dates'>
        <View className='credo-sleep-contract-document__date-col'>
          <SansText variant='caption'>生效开始</SansText>
          <Text className='credo-sleep-contract-document__date-value'>{formatDisplayDate(startDate)}</Text>
        </View>
        <View className='credo-sleep-contract-document__date-col'>
          <SansText variant='caption'>生效结束</SansText>
          <Text className='credo-sleep-contract-document__date-value'>{formatDisplayDate(endDate)}</Text>
        </View>
      </View>

      <View className='credo-sleep-contract-document__article'>
        <MonoLabel>第一条 · 契约内容</MonoLabel>
        <SansText>
          本人承诺在契约生效期内（共 {durationDays} 天），每日于 {formatBedtimeDisplay(targetBedtime)} 前上床入眠。
        </SansText>
      </View>

      <View className='credo-sleep-contract-document__article'>
        <MonoLabel>第二条 · 违约条款</MonoLabel>
        {clauseTexts.length === 0 ? (
          <SansText variant='caption'>（请选择违约条款）</SansText>
        ) : (
          clauseTexts.map((text, index) => (
            <SansText key={index} className='credo-sleep-contract-document__clause-item'>
              {index + 1}. {text}
            </SansText>
          ))
        )}
      </View>

      <View className='credo-sleep-contract-document__article'>
        <MonoLabel>第三条 · 不可撤销</MonoLabel>
        <SansText>{IRREVOCABLE_CLAUSE_TEXT}</SansText>
      </View>

      <View className='credo-sleep-contract-document__parties'>
        <View className='credo-sleep-contract-document__party'>
          <SansText variant='caption'>甲方（承诺人）</SansText>
          <Text className='credo-sleep-contract-document__party-name'>本人</Text>
        </View>
        <View className='credo-sleep-contract-document__party'>
          <SansText variant='caption'>乙方（监督方）</SansText>
          <Text className='credo-sleep-contract-document__party-name'>本人</Text>
        </View>
      </View>

      <View className={`credo-sleep-contract-document__seal ${mode === 'signed' ? 'credo-sleep-contract-document__seal--signed' : ''}`}>
        {mode === 'signed' ? (
          <>
            <MonoLabel>SIGNED AT</MonoLabel>
            <SansText>{formatSignedAt(signedAt)}</SansText>
          </>
        ) : (
          <Text className='credo-sleep-contract-document__seal-pending'>待签</Text>
        )}
      </View>
    </View>
  )
}
