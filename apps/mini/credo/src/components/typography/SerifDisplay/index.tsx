import { Text } from '@tarojs/components'
import type { ReactNode } from 'react'
import './index.scss'

interface SerifDisplayProps {
  children: ReactNode
  className?: string
}

export default function SerifDisplay ({ children, className = '' }: SerifDisplayProps) {
  return (
    <Text className={`credo-serif-display ${className}`.trim()}>{children}</Text>
  )
}
