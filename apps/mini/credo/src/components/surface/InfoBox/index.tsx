import { View } from '@tarojs/components'
import type { ReactNode } from 'react'
import './index.scss'

interface InfoBoxProps {
  children: ReactNode
  icon?: ReactNode
  className?: string
}

export default function InfoBox ({ children, icon, className = '' }: InfoBoxProps) {
  return (
    <View className={`credo-info-box ${className}`.trim()}>
      {icon && <View className='credo-info-box__icon'>{icon}</View>}
      <View className='credo-info-box__content'>{children}</View>
    </View>
  )
}
