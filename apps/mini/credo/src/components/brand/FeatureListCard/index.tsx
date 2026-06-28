import { View, Text } from '@tarojs/components'
import './index.scss'

const FEATURES = [
  { label: '承诺', description: '记录每一次与自己达成的契约' },
  { label: '履约', description: '记录每一次按约定完成' },
  { label: '未履约', description: '记录每一次没有做到' },
  { label: '补救', description: '记录每一次面对与修复' },
]

export default function FeatureListCard () {
  return (
    <View className='credo-feature-list-card'>
      {FEATURES.map((item) => (
        <View key={item.label} className='credo-feature-list-card__row'>
          <Text className='credo-feature-list-card__label'>{item.label}</Text>
          <Text className='credo-feature-list-card__desc'>{item.description}</Text>
        </View>
      ))}
    </View>
  )
}
