import { View } from '@tarojs/components'
import './index.scss'

interface DividerProps {
  className?: string
}

export default function Divider ({ className = '' }: DividerProps) {
  return <View className={`credo-divider ${className}`.trim()} />
}
