import { Text } from '@tarojs/components'
import type { ReactNode } from 'react'
import './index.scss'

interface SansTextProps {
  children: ReactNode
  variant?: 'body' | 'caption'
  className?: string
}

export default function SansText ({
  children,
  variant = 'body',
  className = '',
}: SansTextProps) {
  return (
    <Text className={`credo-sans-text credo-sans-text--${variant} ${className}`.trim()}>
      {children}
    </Text>
  )
}
