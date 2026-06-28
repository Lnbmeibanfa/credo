import { View } from '@tarojs/components'
import { useEffect, useState } from 'react'
import { formatCountdown, getRemainingSeconds, parseTimeString } from '../../../utils/time'
import { MonoLabel, SerifDisplay } from '../../typography'
import './index.scss'

interface CountdownTimerProps {
  targetTime: string
  label?: string
  className?: string
  onExpire?: () => void
}

export default function CountdownTimer ({
  targetTime,
  label = '距离最晚时间',
  className = '',
  onExpire,
}: CountdownTimerProps) {
  const [remaining, setRemaining] = useState(0)

  useEffect(() => {
    const tick = () => {
      const target = parseTimeString(targetTime)
      const secs = getRemainingSeconds(target)
      setRemaining(secs)
      if (secs === 0) {
        onExpire?.()
      }
    }
    tick()
    const id = setInterval(tick, 1000)
    return () => clearInterval(id)
  }, [targetTime, onExpire])

  return (
    <View className={`credo-countdown-timer ${className}`.trim()}>
      <MonoLabel>{label}</MonoLabel>
      <SerifDisplay className='credo-countdown-timer__value'>
        {formatCountdown(remaining)}
      </SerifDisplay>
    </View>
  )
}
