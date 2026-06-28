import { View } from '@tarojs/components'
import type { ReactNode } from 'react'
import './index.scss'

interface CardProps {
  children: ReactNode
  footer?: ReactNode
  className?: string
}

export default function Card ({ children, footer, className = '' }: CardProps) {
  return (
    <View className={`credo-card ${className}`.trim()}>
      <View className='credo-card__body'>{children}</View>
      {footer && <View className='credo-card__footer'>{footer}</View>}
    </View>
  )
}
