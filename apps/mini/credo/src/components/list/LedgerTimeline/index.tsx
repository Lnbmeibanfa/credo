import { View } from '@tarojs/components'
import type { ReactNode } from 'react'
import './index.scss'

interface LedgerTimelineProps {
  children: ReactNode
  className?: string
}

export default function LedgerTimeline ({ children, className = '' }: LedgerTimelineProps) {
  return (
    <View className={`credo-ledger-timeline ${className}`.trim()}>
      {children}
    </View>
  )
}
