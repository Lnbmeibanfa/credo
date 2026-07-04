import { View, Text, Picker } from '@tarojs/components'
import { SansText, SerifDisplay } from '../../typography'
import {
  applyDateRangeUpdate,
  computeDurationDays,
  formatDisplayDate,
  isDateRangeValid,
} from '../../../utils/contractForm'
import './index.scss'

interface DateRangeFieldProps {
  startDate: string
  endDate: string
  onRangeChange: (range: { startDate: string; endDate: string }) => void
  className?: string
}

export default function DateRangeField ({
  startDate,
  endDate,
  onRangeChange,
  className = '',
}: DateRangeFieldProps) {
  const totalDays = computeDurationDays(startDate, endDate)
  const showRangeError = Boolean(startDate && endDate && !isDateRangeValid(startDate, endDate))

  const handleStartChange = (value: string) => {
    onRangeChange(applyDateRangeUpdate(startDate, endDate, 'startDate', value))
  }

  const handleEndChange = (value: string) => {
    onRangeChange(applyDateRangeUpdate(startDate, endDate, 'endDate', value))
  }

  return (
    <View className={`credo-date-range-field ${className}`.trim()}>
      <View className='credo-date-range-field__row'>
        <SansText variant='caption'>生效开始</SansText>
        <Picker mode='date' value={startDate} onChange={(e) => handleStartChange(e.detail.value)}>
          <View className='credo-date-range-field__picker'>
            <SerifDisplay>{formatDisplayDate(startDate)}</SerifDisplay>
            <Text className='credo-date-range-field__icon'>▾</Text>
          </View>
        </Picker>
      </View>
      <View className='credo-date-range-field__divider' />
      <View className='credo-date-range-field__row'>
        <SansText variant='caption'>生效结束</SansText>
        <Picker
          mode='date'
          value={endDate}
          start={startDate}
          onChange={(e) => handleEndChange(e.detail.value)}
        >
          <View className='credo-date-range-field__picker'>
            <SerifDisplay>{formatDisplayDate(endDate)}</SerifDisplay>
            <Text className='credo-date-range-field__icon'>▾</Text>
          </View>
        </Picker>
      </View>
      {showRangeError && (
        <SansText variant='caption' className='credo-date-range-field__error'>
          结束日期须晚于或等于开始日期
        </SansText>
      )}
      {!showRangeError && totalDays > 0 && (
        <SansText variant='caption' className='credo-date-range-field__duration'>
          共 {totalDays} 天
        </SansText>
      )}
    </View>
  )
}
