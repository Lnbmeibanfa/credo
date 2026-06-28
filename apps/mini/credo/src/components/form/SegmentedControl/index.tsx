import { View, Text } from '@tarojs/components'
import './index.scss'

interface SegmentedControlProps {
  options: string[]
  value: string
  onChange: (value: string) => void
  className?: string
}

export default function SegmentedControl ({
  options,
  value,
  onChange,
  className = '',
}: SegmentedControlProps) {
  return (
    <View className={`credo-segmented-control ${className}`.trim()}>
      {options.map((opt) => (
        <View
          key={opt}
          className={`credo-segmented-control__item ${value === opt ? 'credo-segmented-control__item--selected' : ''}`}
          onClick={() => onChange(opt)}
        >
          <Text className='credo-segmented-control__label'>{opt}</Text>
        </View>
      ))}
    </View>
  )
}
