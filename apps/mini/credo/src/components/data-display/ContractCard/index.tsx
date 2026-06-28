import { View, Text } from '@tarojs/components'
import type { ReactNode } from 'react'
import { Card } from '../../surface'
import ContractBadge from '../ContractBadge'
import SplitCompare from '../SplitCompare'
import { SansText } from '../../typography'
import './index.scss'

interface ContractCardProps {
  contractNo: string
  status: string
  targetBedtime: string
  latestAllowed: string
  todayStatus: string
  icon?: ReactNode
  className?: string
}

export default function ContractCard ({
  contractNo,
  status,
  targetBedtime,
  latestAllowed,
  todayStatus,
  icon,
  className = '',
}: ContractCardProps) {
  const footer = (
    <View className='credo-contract-card__footer'>
      <SansText variant='caption'>今日状态</SansText>
      <Text className='credo-contract-card__status'>{todayStatus}</Text>
    </View>
  )

  return (
    <Card footer={footer} className={`credo-contract-card ${className}`.trim()}>
      <SplitCompare
        left={{ label: '目标上床', value: targetBedtime }}
        right={{ label: '最晚允许', value: latestAllowed }}
        header={
          <ContractBadge
            contractLabel='睡眠契约'
            contractNo={contractNo}
            status={status}
            icon={icon}
          />
        }
      />
    </Card>
  )
}
