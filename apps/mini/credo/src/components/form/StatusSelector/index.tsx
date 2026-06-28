import { View, Text } from '@tarojs/components'
import './index.scss'

export interface StatusOption {
  id: string
  label: string
  icon?: string
  variant?: 'default' | 'gold'
}

interface StatusSelectorProps {
  options: StatusOption[]
  value: string
  onChange: (value: string) => void
  className?: string
}

export default function StatusSelector ({
  options,
  value,
  onChange,
  className = '',
}: StatusSelectorProps) {
  return (
    <View className={`credo-status-selector ${className}`.trim()}>
      {options.map((opt) => {
        const selected = value === opt.id
        const isGold = opt.variant === 'gold'
        return (
          <View
            key={opt.id}
            className={`credo-status-selector__item ${selected ? 'credo-status-selector__item--selected' : ''} ${isGold && selected ? 'credo-status-selector__item--gold' : ''} ${isGold && !selected ? 'credo-status-selector__item--gold-border' : ''}`}
            onClick={() => onChange(opt.id)}
          >
            {opt.icon && (
              <Text className='credo-status-selector__icon'>{opt.icon}</Text>
            )}
            <Text className='credo-status-selector__label'>{opt.label}</Text>
          </View>
        )
      })}
    </View>
  )
}
