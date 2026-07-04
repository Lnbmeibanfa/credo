import { View, Text } from '@tarojs/components'
import type { BreachClauseFormItem, BreachClauseType } from '../../../utils/contractForm'
import { BREACH_CLAUSE_LABELS, BREACH_CLAUSE_DEFAULTS } from '../../../utils/contractForm'
import { SansText } from '../../typography'
import TextAreaField from '../TextAreaField'
import './index.scss'

interface BreachClauseSelectorProps {
  value: BreachClauseFormItem[]
  onChange: (value: BreachClauseFormItem[]) => void
  className?: string
}

const CLAUSE_ORDER: BreachClauseType[] = ['RECORD', 'REVIEW', 'CUSTOM']

export default function BreachClauseSelector ({
  value,
  onChange,
  className = '',
}: BreachClauseSelectorProps) {
  const getClause = (type: BreachClauseType): BreachClauseFormItem => {
    return value.find((item) => item.type === type) ?? { type, enabled: type === 'RECORD' }
  }

  const updateClause = (type: BreachClauseType, patch: Partial<BreachClauseFormItem>) => {
    const next = CLAUSE_ORDER.map((clauseType) => {
      const existing = getClause(clauseType)
      if (clauseType !== type) {
        return existing
      }
      return { ...existing, ...patch, type: clauseType }
    })
    onChange(next)
  }

  return (
    <View className={`credo-breach-clause-selector ${className}`.trim()}>
      {CLAUSE_ORDER.map((type) => {
        const clause = getClause(type)
        const locked = type === 'RECORD'
        const checked = locked || clause.enabled

        return (
          <View key={type} className='credo-breach-clause-selector__item'>
            <View
              className={`credo-breach-clause-selector__head ${locked ? 'credo-breach-clause-selector__head--locked' : ''}`}
              onClick={() => {
                if (!locked) {
                  updateClause(type, { enabled: !clause.enabled })
                }
              }}
            >
              <View className={`credo-breach-clause-selector__box ${checked ? 'credo-breach-clause-selector__box--checked' : ''}`}>
                {checked && <Text className='credo-breach-clause-selector__check'>✓</Text>}
              </View>
              <View className='credo-breach-clause-selector__label-wrap'>
                <SansText>{BREACH_CLAUSE_LABELS[type]}</SansText>
                {locked && <Text className='credo-breach-clause-selector__lock'>必选</Text>}
              </View>
            </View>
            {type !== 'CUSTOM' && (
              <SansText variant='caption' className='credo-breach-clause-selector__hint'>
                {BREACH_CLAUSE_DEFAULTS[type]}
              </SansText>
            )}
            {type === 'CUSTOM' && clause.enabled && (
              <TextAreaField
                value={clause.contentText ?? ''}
                onChange={(contentText) => updateClause(type, { contentText })}
                placeholder='输入自定义违约条款…'
                className='credo-breach-clause-selector__textarea'
              />
            )}
          </View>
        )
      })}
    </View>
  )
}
