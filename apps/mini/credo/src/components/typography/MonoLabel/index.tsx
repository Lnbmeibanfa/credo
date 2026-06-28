import { Text } from '@tarojs/components'
import type { ReactNode } from 'react'
import './index.scss'

interface MonoLabelProps {
  children: ReactNode
  className?: string
}

export default function MonoLabel ({ children, className = '' }: MonoLabelProps) {
  return (
    <Text className={`credo-mono-label ${className}`.trim()}>{children}</Text>
  )
}
