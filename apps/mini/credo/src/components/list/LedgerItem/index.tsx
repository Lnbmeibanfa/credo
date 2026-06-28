import { View, Text } from '@tarojs/components'
import { CreditEventType } from '../../../constants/creditEventTypes'
import StatusIcon from '../StatusIcon'
import StatusTag from '../StatusTag'
import './index.scss'

interface LedgerItemProps {
  date: string
  weekday: string
  statusType: CreditEventType
  title: string
  description: string
  tagText?: string
  className?: string
}

export default function LedgerItem ({
  date,
  weekday,
  statusType,
  title,
  description,
  tagText,
  className = '',
}: LedgerItemProps) {
  return (
    <View className={`credo-ledger-item ${className}`.trim()}>
      <View className='credo-ledger-item__date'>
        <Text className='credo-ledger-item__date-text'>{date}</Text>
        <Text className='credo-ledger-item__weekday'>{weekday}</Text>
      </View>
      <StatusIcon type={statusType} />
      <View className='credo-ledger-item__content'>
        <View className='credo-ledger-item__title-row'>
          <Text className='credo-ledger-item__title'>{title}</Text>
          <StatusTag type={statusType} text={tagText} />
        </View>
        <Text className='credo-ledger-item__desc'>{description}</Text>
      </View>
    </View>
  )
}
