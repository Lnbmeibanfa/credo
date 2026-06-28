import { View, Text } from '@tarojs/components'
import type { GridOption } from '../../../constants/exemptionOptions'
import './index.scss'

interface OptionGridProps {
  options: GridOption[]
  value: string | string[]
  onChange: (value: string | string[]) => void
  mode?: 'single' | 'multiple'
  columns?: number
  className?: string
}

export default function OptionGrid ({
  options,
  value,
  onChange,
  mode = 'single',
  columns = 3,
  className = '',
}: OptionGridProps) {
  const isSelected = (id: string) => {
    if (mode === 'multiple') {
      return (value as string[]).includes(id)
    }
    return value === id
  }

  const toggle = (id: string) => {
    if (mode === 'multiple') {
      const arr = value as string[]
      if (arr.includes(id)) {
        onChange(arr.filter((v) => v !== id))
      } else {
        onChange([...arr, id])
      }
    } else {
      onChange(id)
    }
  }

  return (
    <View
      className={`credo-option-grid credo-option-grid--cols-${columns} ${className}`.trim()}
    >
      {options.map((opt) => {
        const selected = isSelected(opt.id)
        return (
          <View
            key={opt.id}
            className={`credo-option-grid__item ${selected ? 'credo-option-grid__item--selected' : ''}`}
            onClick={() => toggle(opt.id)}
          >
            <Text className='credo-option-grid__label'>{opt.label}</Text>
          </View>
        )
      })}
    </View>
  )
}
