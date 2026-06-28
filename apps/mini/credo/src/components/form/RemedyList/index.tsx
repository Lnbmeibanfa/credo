import { View, Text } from '@tarojs/components'
import './index.scss'

export interface RemedyItem {
  index: string
  text: string
}

interface RemedyListProps {
  items: RemedyItem[]
  className?: string
}

export default function RemedyList ({ items, className = '' }: RemedyListProps) {
  return (
    <View className={`credo-remedy-list ${className}`.trim()}>
      {items.map((item) => (
        <View key={item.index} className='credo-remedy-list__row'>
          <Text className='credo-remedy-list__index'>{item.index}</Text>
          <Text className='credo-remedy-list__text'>{item.text}</Text>
        </View>
      ))}
    </View>
  )
}
