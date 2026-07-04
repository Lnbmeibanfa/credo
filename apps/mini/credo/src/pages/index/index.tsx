import { View, Text } from '@tarojs/components'
import Taro, { useDidShow } from '@tarojs/taro'
import { useCallback, useState } from 'react'
import {
  PageShell,
  PageHeader,
  QuoteBlock,
  FeatureListCard,
  PrimaryButton,
  MonoLabel,
  SansText,
  SecondaryButton,
} from '@/components'
import { WELCOME_CONTENT } from '@/constants/welcomeContent'
import { hasStoredToken, phoneLogin } from '@/services/auth'
import './index.scss'

export default function Index () {
  const [isAuthenticated, setIsAuthenticated] = useState(false)
  const [isLoggingIn, setIsLoggingIn] = useState(false)

  const refreshAuthState = useCallback(() => {
    setIsAuthenticated(hasStoredToken())
  }, [])

  useDidShow(() => {
    refreshAuthState()
  })

  const handleStartContract = () => {
    if (!isAuthenticated) {
      return
    }
    Taro.navigateTo({ url: '/pages/contract-create/index' })
  }

  const handleGetPhoneNumber = async (event: { detail: { code?: string; errMsg: string } }) => {
    const phoneCode = event.detail.code
    if (!phoneCode) {
      Taro.showToast({ title: '需要授权手机号才能继续', icon: 'none' })
      return
    }

    setIsLoggingIn(true)
    try {
      await phoneLogin(phoneCode)
      setIsAuthenticated(true)
      Taro.showToast({ title: '登录成功', icon: 'success' })
    } catch {
      Taro.showToast({ title: '登录失败，请重试', icon: 'none' })
    } finally {
      setIsLoggingIn(false)
    }
  }

  return (
    <PageShell showDotGrid>
      <View className='welcome'>
        <PageHeader
          mode='brand'
          title={WELCOME_CONTENT.brandTitle}
          monoLabel={WELCOME_CONTENT.monoLabel}
        />

        <View className='welcome__hero'>
          <Text className='welcome__hero-line'>{WELCOME_CONTENT.heroLines[0]}</Text>
          <Text className='welcome__hero-line welcome__hero-line--sub'>
            {WELCOME_CONTENT.heroLines[1]}
          </Text>
        </View>

        <QuoteBlock lines={[...WELCOME_CONTENT.quoteLines]} className='welcome__quote' />

        <View className='welcome__features'>
          <FeatureListCard />
        </View>

        <SansText variant='caption' className='welcome__hint'>
          {isAuthenticated ? WELCOME_CONTENT.hintText : WELCOME_CONTENT.authHintText}
        </SansText>

        <View className='welcome__cta'>
          {isAuthenticated ? (
            <>
              <PrimaryButton letterSpacing onClick={handleStartContract}>
                {WELCOME_CONTENT.ctaLabel}
              </PrimaryButton>
              <SecondaryButton onClick={() => Taro.navigateTo({ url: '/pages/ledger/index' })}>
                查看睡眠账本
              </SecondaryButton>
            </>
          ) : (
            <PrimaryButton
              letterSpacing
              openType='getPhoneNumber'
              onGetPhoneNumber={handleGetPhoneNumber}
            >
              {isLoggingIn ? '登录中…' : WELCOME_CONTENT.authButtonLabel}
            </PrimaryButton>
          )}
        </View>

        <MonoLabel className='welcome__footer'>{WELCOME_CONTENT.footerLabel}</MonoLabel>
      </View>
    </PageShell>
  )
}
