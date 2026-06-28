import { Text } from '@tarojs/components'
import type { ReactNode } from 'react'
import './index.scss'

interface SerifTitleProps {
  children: ReactNode
  className?: string
}

export default function SerifTitle ({ children, className = '' }: SerifTitleProps) {
  return (
    <Text className={`credo-serif-title ${className}`.trim()}>{children}</Text>
  )
}
