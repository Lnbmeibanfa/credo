import { View, Text } from '@tarojs/components'
import type { GridOption } from '../../../constants/exemptionOptions'
import './index.scss'

interface CheckboxGridProps {
  options: GridOption[]
  value: string[]
  onChange: (value: string[]) => void
  columns?: number
  className?: string
}

export default function CheckboxGrid ({
  options,
  value,
  onChange,
  columns = 2,
  className = '',
}: CheckboxGridProps) {
  const toggle = (id: string) => {
    if (value.includes(id)) {
      onChange(value.filter((v) => v !== id))
    } else {
      onChange([...value, id])
    }
  }

  return (
    <View
      className={`credo-checkbox-grid credo-checkbox-grid--cols-${columns} ${className}`.trim()}
    >
      {options.map((opt) => {
        const checked = value.includes(opt.id)
        return (
          <View
            key={opt.id}
            className={`credo-checkbox-grid__item ${checked ? 'credo-checkbox-grid__item--checked' : ''}`}
            onClick={() => toggle(opt.id)}
          >
            <View className={`credo-checkbox-grid__box ${checked ? 'credo-checkbox-grid__box--checked' : ''}`}>
              {checked && <Text className='credo-checkbox-grid__check'>✓</Text>}
            </View>
            <Text className='credo-checkbox-grid__label'>{opt.label}</Text>
          </View>
        )
      })}
    </View>
  )
}
