import { View } from '@tarojs/components'
import type { ReactNode } from 'react'
import { SansText, SerifDisplay } from '../../typography'
import './index.scss'

export interface CompareColumn {
  label: string
  value: string
}

interface SplitCompareProps {
  left: CompareColumn
  right: CompareColumn
  header?: ReactNode
  className?: string
}

export default function SplitCompare ({
  left,
  right,
  header,
  className = '',
}: SplitCompareProps) {
  return (
    <View className={`credo-split-compare ${className}`.trim()}>
      {header && <View className='credo-split-compare__header'>{header}</View>}
      <View className='credo-split-compare__body'>
        <View className='credo-split-compare__col'>
          <SansText variant='caption'>{left.label}</SansText>
          <SerifDisplay>{left.value}</SerifDisplay>
        </View>
        <View className='credo-split-compare__divider' />
        <View className='credo-split-compare__col'>
          <SansText variant='caption'>{right.label}</SansText>
          <SerifDisplay>{right.value}</SerifDisplay>
        </View>
      </View>
    </View>
  )
}
