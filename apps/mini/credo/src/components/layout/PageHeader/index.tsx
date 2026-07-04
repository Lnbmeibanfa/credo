import { View, Text } from '@tarojs/components'
import type { ReactNode } from 'react'
import { BrandLogo } from '../../brand'
import { MonoLabel, SerifTitle, SansText } from '../../typography'
import './index.scss'

interface PageHeaderBrandProps {
  mode: 'brand'
  title: string
  subtitle?: string
  monoLabel?: string
  rightAction?: ReactNode
}

interface PageHeaderContextProps {
  mode: 'context'
  monoLabel: string
  title: string
  subtitle?: string
  rightLabel?: string
  onBack?: () => void
}

export type PageHeaderProps = PageHeaderBrandProps | PageHeaderContextProps

export default function PageHeader (props: PageHeaderProps) {
  if (props.mode === 'brand') {
    return (
      <View className='credo-page-header'>
        <View className='credo-page-header__main'>
          <BrandLogo />
          <View className='credo-page-header__brand-text'>
            <SerifTitle>{props.title}</SerifTitle>
            {props.monoLabel && <MonoLabel>{props.monoLabel}</MonoLabel>}
            {props.subtitle && <SansText variant='caption'>{props.subtitle}</SansText>}
          </View>
        </View>
        {props.rightAction && (
          <View className='credo-page-header__right'>{props.rightAction}</View>
        )}
      </View>
    )
  }

  return (
    <View className='credo-page-header credo-page-header--context'>
      {props.onBack && (
        <View className='credo-page-header__back' onClick={props.onBack}>
          <Text className='credo-page-header__back-text'>← 返回</Text>
        </View>
      )}
      <View className='credo-page-header__context-top'>
        <MonoLabel>{props.monoLabel}</MonoLabel>
        {props.rightLabel && (
          <Text className='credo-page-header__right-label'>{props.rightLabel}</Text>
        )}
      </View>
      <SerifTitle>{props.title}</SerifTitle>
      {props.subtitle && <SansText variant='caption'>{props.subtitle}</SansText>}
    </View>
  )
}
