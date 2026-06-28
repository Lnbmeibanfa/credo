import { View, Text, Picker } from '@tarojs/components'
import { SerifDisplay } from '../../typography'
import './index.scss'

interface TimePickerFieldProps {
  value: string
  onChange: (value: string) => void
  className?: string
}

export default function TimePickerField ({
  value,
  onChange,
  className = '',
}: TimePickerFieldProps) {
  const handleChange = (e: { detail: { value: string } }) => {
    onChange(e.detail.value)
  }

  return (
    <Picker mode='time' value={value} onChange={handleChange}>
      <View className={`credo-time-picker-field ${className}`.trim()}>
        <SerifDisplay>{value.replace(':', ' : ')}</SerifDisplay>
        <Text className='credo-time-picker-field__icon'>◷</Text>
      </View>
    </Picker>
  )
}
