import { View } from '@tarojs/components'
import { PageShell, MonoLabel, SerifTitle, SansText } from '@/components'
import './index.scss'

export default function ContractCreate () {
  return (
    <PageShell>
      <View className='contract-create-placeholder'>
        <MonoLabel>STEP 01 / 缔约</MonoLabel>
        <SerifTitle className='contract-create-placeholder__title'>创建睡眠契约</SerifTitle>
        <SansText variant='caption' className='contract-create-placeholder__desc'>
          此页面为占位，完整创建表单将在后续 change 实现。
        </SansText>
      </View>
    </PageShell>
  )
}
