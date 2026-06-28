import { View, Text } from '@tarojs/components'
import { SerifDisplay, SansText } from '../../typography'
import './index.scss'

interface StatCellProps {
  label: string
  value: string | number
  unit?: string
  className?: string
}

export function StatCell ({ label, value, unit, className = '' }: StatCellProps) {
  return (
    <View className={`credo-stat-cell ${className}`.trim()}>
      <SansText variant='caption'>{label}</SansText>
      <View className='credo-stat-cell__value-row'>
        <SerifDisplay>{value}</SerifDisplay>
        {unit && <Text className='credo-stat-cell__unit'>{unit}</Text>}
      </View>
    </View>
  )
}

export interface StatCellData {
  label: string
  value: string | number
  unit?: string
}

interface StatGridProps {
  cells: StatCellData[]
  motto?: string
  className?: string
}

export default function StatGrid ({ cells, motto, className = '' }: StatGridProps) {
  return (
    <View className={`credo-stat-grid ${className}`.trim()}>
      <View className='credo-stat-grid__row'>
        {cells.map((cell) => (
          <StatCell key={cell.label} {...cell} />
        ))}
      </View>
      {motto && (
        <SansText variant='caption' className='credo-stat-grid__motto'>
          {motto}
        </SansText>
      )}
    </View>
  )
}
