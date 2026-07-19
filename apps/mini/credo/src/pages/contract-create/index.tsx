import { View } from '@tarojs/components'
import Taro, { useLoad } from '@tarojs/taro'
import { useCallback, useState } from 'react'
import {
  PageShell,
  PageHeader,
  FormSection,
  TimePickerField,
  DateRangeField,
  BreachClauseSelector,
  SleepContractDocument,
  PrimaryButton,
  SansText,
} from '@/components'
import { hasStoredToken } from '@/services/auth'
import { getMySleepContract, upsertSleepContract } from '@/services/contract'
import {
  createDefaultFormState,
  mapDtoToForm,
  mapFormToPayload,
  validateSleepContractForm,
  type SleepContractFormState,
} from '@/utils/contractForm'
import './index.scss'

export default function ContractCreate () {
  const [form, setForm] = useState<SleepContractFormState>(() => createDefaultFormState())
  const [contractNo, setContractNo] = useState<string | undefined>()
  const [signedAt, setSignedAt] = useState<string | null>(null)
  const [isSigned, setIsSigned] = useState(false)
  const [isLoading, setIsLoading] = useState(true)
  const [isSubmitting, setIsSubmitting] = useState(false)

  const loadExistingContract = useCallback(async () => {
    if (!hasStoredToken()) {
      setIsLoading(false)
      return
    }

    try {
      const existing = await getMySleepContract()
      if (existing) {
        setForm(mapDtoToForm(existing))
        setContractNo(existing.contractNo)
        setSignedAt(existing.signedAt)
        setIsSigned(true)
      }
    } catch {
      Taro.showToast({ title: '加载契约失败', icon: 'none' })
    } finally {
      setIsLoading(false)
    }
  }, [])

  useLoad(() => {
    if (!hasStoredToken()) {
      setIsLoading(false)
      Taro.showToast({ title: '请先登录', icon: 'none' })
      setTimeout(() => {
        Taro.redirectTo({ url: '/pages/index/index' })
      }, 800)
      return
    }
    loadExistingContract()
  })

  const handleSign = async () => {
    if (isSubmitting) {
      return
    }
    const error = validateSleepContractForm(form)
    if (error) {
      Taro.showToast({ title: error, icon: 'none' })
      return
    }

    setIsSubmitting(true)
    try {
      const saved = await upsertSleepContract(mapFormToPayload(form))
      setContractNo(saved.contractNo)
      setSignedAt(saved.signedAt)
      setIsSigned(true)
      Taro.showToast({ title: '签约成功', icon: 'success' })
      setTimeout(() => {
        Taro.redirectTo({ url: '/pages/ledger/index' })
      }, 500)
    } catch {
      Taro.showToast({ title: '签约失败，请重试', icon: 'none' })
    } finally {
      setIsSubmitting(false)
    }
  }

  if (isLoading) {
    return (
      <PageShell>
        <View className='contract-create'>
          <SansText variant='caption'>加载中…</SansText>
        </View>
      </PageShell>
    )
  }

  return (
    <PageShell>
      <View className='contract-create'>
        <PageHeader
          mode='context'
          monoLabel='STEP 01 / 缔约'
          title='创建睡眠契约'
          subtitle='填写条款，预览文书，确认签约'
          onBack={() => Taro.navigateBack()}
        />

        <FormSection index='01' title='关机入眠时间' hint='每日目标上床时间'>
          <TimePickerField
            value={form.targetBedtime}
            onChange={(targetBedtime) => setForm((prev) => ({ ...prev, targetBedtime }))}
          />
        </FormSection>

        <FormSection index='02' title='合同生效时期' hint='选择起止日期，粒度为天'>
          <DateRangeField
            startDate={form.startDate}
            endDate={form.endDate}
            onRangeChange={(range) => setForm((prev) => ({ ...prev, ...range }))}
          />
        </FormSection>

        <FormSection index='03' title='违约条款'>
          <BreachClauseSelector
            value={form.breachClauses}
            onChange={(breachClauses) => setForm((prev) => ({ ...prev, breachClauses }))}
          />
        </FormSection>

        <View className='contract-create__document'>
          <SleepContractDocument
            mode={isSigned ? 'signed' : 'preview'}
            targetBedtime={form.targetBedtime}
            startDate={form.startDate}
            endDate={form.endDate}
            breachClauses={form.breachClauses}
            contractNo={contractNo}
            signedAt={signedAt}
          />
        </View>

        <PrimaryButton
          className='contract-create__submit'
          onClick={handleSign}
        >
          {isSubmitting ? '提交中…' : isSigned ? '确认修改并重新签约' : '确认签约'}
        </PrimaryButton>
      </View>
    </PageShell>
  )
}
