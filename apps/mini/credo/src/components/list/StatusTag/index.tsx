import { Text } from '@tarojs/components'
import { CreditEventType, getCreditEventVisual } from '../../../constants/creditEventTypes'
import './index.scss'

interface StatusTagProps {
  type: CreditEventType
  text?: string
  className?: string
}

export default function StatusTag ({ type, text, className = '' }: StatusTagProps) {
  const visual = getCreditEventVisual(type)
  const label = text ?? visual.label
  return (
    <Text
      className={`credo-status-tag ${className}`.trim()}
      style={{ color: visual.tagColor, borderColor: visual.tagColor }}
    >
      {label}
    </Text>
  )
}
