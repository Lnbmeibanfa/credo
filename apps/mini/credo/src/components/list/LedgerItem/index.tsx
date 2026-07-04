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
  actionable?: boolean
  onPress?: () => void
}

export default function LedgerItem ({
  date,
  weekday,
  statusType,
  title,
  description,
  tagText,
  className = '',
  actionable = false,
  onPress,
}: LedgerItemProps) {
  const rootClass = [
    'credo-ledger-item',
    actionable ? 'credo-ledger-item--actionable' : '',
    statusType === CreditEventType.UPCOMING ? 'credo-ledger-item--upcoming' : '',
    className,
  ].filter(Boolean).join(' ')

  return (
    <View className={rootClass} onClick={actionable ? onPress : undefined}>
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
