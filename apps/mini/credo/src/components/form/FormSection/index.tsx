import { View } from '@tarojs/components'
import type { ReactNode } from 'react'
import { SerifTitle, SansText } from '../../typography'
import './index.scss'

interface FormSectionProps {
  index: string
  title: string
  hint?: string
  children: ReactNode
  className?: string
}

export default function FormSection ({
  index,
  title,
  hint,
  children,
  className = '',
}: FormSectionProps) {
  return (
    <View className={`credo-form-section ${className}`.trim()}>
      <SerifTitle className='credo-form-section__title'>
        {index} · {title}
      </SerifTitle>
      {hint && <SansText variant='caption'>{hint}</SansText>}
      <View className='credo-form-section__content'>{children}</View>
    </View>
  )
}
