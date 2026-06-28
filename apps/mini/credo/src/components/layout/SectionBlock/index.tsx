import { View } from '@tarojs/components'
import type { ReactNode } from 'react'
import { MonoLabel, SerifTitle, SansText } from '../../typography'
import './index.scss'

interface SectionBlockProps {
  monoLabel?: string
  title?: string
  subtitle?: string
  children: ReactNode
  className?: string
}

export default function SectionBlock ({
  monoLabel,
  title,
  subtitle,
  children,
  className = '',
}: SectionBlockProps) {
  return (
    <View className={`credo-section-block ${className}`.trim()}>
      {monoLabel && <MonoLabel>{monoLabel}</MonoLabel>}
      {title && <SerifTitle>{title}</SerifTitle>}
      {subtitle && <SansText variant='caption'>{subtitle}</SansText>}
      <View className='credo-section-block__content'>{children}</View>
    </View>
  )
}
