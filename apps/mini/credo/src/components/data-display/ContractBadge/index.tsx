import { View } from '@tarojs/components'
import type { ReactNode } from 'react'
import { MonoLabel, SansText } from '../../typography'
import './index.scss'

interface ContractBadgeProps {
  contractLabel: string
  contractNo: string
  status: string
  icon?: ReactNode
  className?: string
}

export default function ContractBadge ({
  contractLabel,
  contractNo,
  status,
  icon,
  className = '',
}: ContractBadgeProps) {
  return (
    <View className={`credo-contract-badge ${className}`.trim()}>
      {icon && <View className='credo-contract-badge__icon'>{icon}</View>}
      <SansText>{contractLabel} · No. {contractNo}</SansText>
      <MonoLabel>{status}</MonoLabel>
    </View>
  )
}
