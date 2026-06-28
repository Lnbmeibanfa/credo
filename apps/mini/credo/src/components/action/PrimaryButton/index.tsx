import { View, Text, Button } from '@tarojs/components'
import type { ReactNode } from 'react'
import type { ButtonProps } from '@tarojs/components'
import './index.scss'

interface PrimaryButtonProps {
  children: ReactNode
  onClick?: () => void
  icon?: ReactNode
  letterSpacing?: boolean
  className?: string
  fullWidth?: boolean
  openType?: ButtonProps['openType']
  onGetPhoneNumber?: ButtonProps['onGetPhoneNumber']
}

export default function PrimaryButton ({
  children,
  onClick,
  icon,
  letterSpacing = false,
  className = '',
  fullWidth = true,
  openType,
  onGetPhoneNumber,
}: PrimaryButtonProps) {
  const classNames = `credo-primary-button ${fullWidth ? 'credo-primary-button--full' : ''} ${letterSpacing ? 'credo-primary-button--spaced' : ''} ${className}`.trim()

  if (openType) {
    return (
      <Button
        className={classNames}
        openType={openType}
        onGetPhoneNumber={onGetPhoneNumber}
        onClick={onClick}
      >
        {icon && <View className='credo-primary-button__icon'>{icon}</View>}
        <Text className='credo-primary-button__text'>{children}</Text>
      </Button>
    )
  }

  return (
    <View
      className={classNames}
      onClick={onClick}
    >
      {icon && <View className='credo-primary-button__icon'>{icon}</View>}
      <Text className='credo-primary-button__text'>{children}</Text>
    </View>
  )
}
