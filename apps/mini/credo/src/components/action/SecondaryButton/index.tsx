import { View, Text } from '@tarojs/components'
import type { ReactNode } from 'react'
import './index.scss'

interface SecondaryButtonProps {
  children: ReactNode
  onClick?: () => void
  icon?: ReactNode
  className?: string
  fullWidth?: boolean
}

export default function SecondaryButton ({
  children,
  onClick,
  icon,
  className = '',
  fullWidth = true,
}: SecondaryButtonProps) {
  return (
    <View
      className={`credo-secondary-button ${fullWidth ? 'credo-secondary-button--full' : ''} ${className}`.trim()}
      onClick={onClick}
    >
      {icon && <View className='credo-secondary-button__icon'>{icon}</View>}
      <Text className='credo-secondary-button__text'>{children}</Text>
    </View>
  )
}
