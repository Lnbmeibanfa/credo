import { View, Text } from '@tarojs/components'
import { CreditEventType, getCreditEventVisual } from '../../../constants/creditEventTypes'
import './index.scss'

interface StatusIconProps {
  type: CreditEventType
  className?: string
}

export default function StatusIcon ({ type, className = '' }: StatusIconProps) {
  const visual = getCreditEventVisual(type)
  return (
    <View
      className={`credo-status-icon credo-status-icon--${type.toLowerCase()} ${className}`.trim()}
      style={{ borderColor: visual.color }}
    >
      <Text className='credo-status-icon__char' style={{ color: visual.color }}>
        {visual.icon}
      </Text>
    </View>
  )
}
