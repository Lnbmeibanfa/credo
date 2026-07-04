import { View, Text } from '@tarojs/components'
import Taro, { useDidShow } from '@tarojs/taro'
import { useCallback, useState } from 'react'
import {
  PageShell,
  PageHeader,
  LedgerTimeline,
  LedgerItem,
  PrimaryButton,
  SansText,
  MonoLabel,
} from '@/components'
import { hasStoredToken } from '@/services/auth'
import { getSleepDailyView, recordSleepDay } from '@/services/ledger'
import {
  formatRecordDateDisplay,
  getDayDescription,
  getDayTitle,
  getWeekdayLabel,
  isDayActionable,
  mapDailyStatusToCreditEventType,
  sortDaysForDisplay,
  buildPendingQueueQuery,
  type SleepDailyDay,
  type SleepDailyView,
} from '@/utils/ledgerView'
import './index.scss'

export default function LedgerPage () {
  const [view, setView] = useState<SleepDailyView | null>(null)
  const [noContract, setNoContract] = useState(false)
  const [isLoading, setIsLoading] = useState(true)
  const [submittingDate, setSubmittingDate] = useState<string | null>(null)

  const loadLedger = useCallback(async () => {
    if (!hasStoredToken()) {
      setIsLoading(false)
      return
    }

    setIsLoading(true)
    try {
      const data = await getSleepDailyView(buildPendingQueueQuery())
      if (data == null) {
        setNoContract(true)
        setView(null)
      } else {
        setNoContract(false)
        setView(data)
      }
    } catch {
      Taro.showToast({ title: '加载账本失败', icon: 'none' })
    } finally {
      setIsLoading(false)
    }
  }, [])

  useDidShow(() => {
    if (!hasStoredToken()) {
      Taro.showToast({ title: '请先登录', icon: 'none' })
      setTimeout(() => {
        Taro.redirectTo({ url: '/pages/index/index' })
      }, 800)
      return
    }
    loadLedger()
  })

  const handleRegister = (day: SleepDailyDay) => {
    if (!isDayActionable(day.status) || submittingDate) {
      return
    }

    Taro.showActionSheet({
      itemList: ['履约', '未履约'],
      success: (res) => {
        const eventType = res.tapIndex === 0 ? 'FULFILLED' : 'BREACH'
        Taro.showModal({
          title: '确认登记',
          content: `${day.recordDate} 将登记为${eventType === 'FULFILLED' ? '履约' : '未履约'}，提交后不可修改。`,
          success: async (modalRes) => {
            if (!modalRes.confirm) {
              return
            }
            setSubmittingDate(day.recordDate)
            try {
              await recordSleepDay({ recordDate: day.recordDate, eventType })
              Taro.showToast({ title: '登记成功', icon: 'success' })
              await loadLedger()
            } catch {
              Taro.showToast({ title: '登记失败', icon: 'none' })
            } finally {
              setSubmittingDate(null)
            }
          },
        })
      },
    })
  }

  if (isLoading) {
    return (
      <PageShell>
        <View className='sleep-ledger'>
          <SansText variant='caption'>加载中…</SansText>
        </View>
      </PageShell>
    )
  }

  if (noContract) {
    return (
      <PageShell>
        <View className='sleep-ledger__empty'>
          <PageHeader
            mode='context'
            monoLabel='LEDGER / 账本'
            title='睡眠账本'
            subtitle='尚未创建睡眠契约'
          />
          <SansText variant='caption'>创建契约后，可在此逐日登记履约情况。</SansText>
          <PrimaryButton onClick={() => Taro.navigateTo({ url: '/pages/contract-create/index' })}>
            去创建契约
          </PrimaryButton>
        </View>
      </PageShell>
    )
  }

  const summary = view?.summary
  const days = sortDaysForDisplay(view?.days ?? [])

  return (
    <PageShell>
      <View className='sleep-ledger'>
        <PageHeader
          mode='context'
          monoLabel='LEDGER / 账本'
          title='睡眠账本'
          subtitle='以下为待登记日期，统计卡片展示契约全貌'
        />

        {summary && (
          <View className='sleep-ledger__summary'>
            <View className='sleep-ledger__stat'>
              <MonoLabel>义务天数</MonoLabel>
              <Text className='sleep-ledger__stat-value'>{summary.obligationDays}</Text>
            </View>
            <View className='sleep-ledger__stat'>
              <MonoLabel>待登记</MonoLabel>
              <Text className='sleep-ledger__stat-value'>{summary.pendingDays}</Text>
            </View>
            <View className='sleep-ledger__stat'>
              <MonoLabel>履约</MonoLabel>
              <Text className='sleep-ledger__stat-value'>{summary.fulfilledDays}</Text>
            </View>
            <View className='sleep-ledger__stat'>
              <MonoLabel>未履约</MonoLabel>
              <Text className='sleep-ledger__stat-value'>{summary.breachDays}</Text>
            </View>
          </View>
        )}

        <LedgerTimeline>
          {days.length === 0 ? (
            <View className='sleep-ledger__queue-empty'>
              <SansText variant='body'>暂无待登记日期</SansText>
              <SansText variant='caption'>当前没有需要登记的睡眠记录，继续保持。</SansText>
            </View>
          ) : (
            days.map((day) => (
              <LedgerItem
                key={day.recordDate}
                date={formatRecordDateDisplay(day.recordDate)}
                weekday={getWeekdayLabel(day.recordDate)}
                statusType={mapDailyStatusToCreditEventType(day.status)}
                title={getDayTitle(day.status)}
                description={getDayDescription(day)}
                actionable={isDayActionable(day.status)}
                onPress={() => handleRegister(day)}
              />
            ))
          )}
        </LedgerTimeline>
      </View>
    </PageShell>
  )
}
