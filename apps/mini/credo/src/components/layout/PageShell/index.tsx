import { View } from '@tarojs/components'
import type { ReactNode } from 'react'
import './index.scss'

interface PageShellProps {
  children: ReactNode
  showDotGrid?: boolean
  className?: string
}

export default function PageShell ({
  children,
  showDotGrid = true,
  className = '',
}: PageShellProps) {
  return (
    <View
      className={`credo-page-shell ${showDotGrid ? 'credo-page-shell--dot-grid' : ''} ${className}`.trim()}
    >
      {children}
    </View>
  )
}
