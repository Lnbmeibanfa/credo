import { View } from '@tarojs/components'
import { SansText } from '../../typography'
import './index.scss'

interface QuoteBlockProps {
  lines: string[]
  className?: string
}

export default function QuoteBlock ({ lines, className = '' }: QuoteBlockProps) {
  return (
    <View className={`credo-quote-block ${className}`.trim()}>
      {lines.map((line) => (
        <SansText key={line} variant='caption'>{line}</SansText>
      ))}
    </View>
  )
}
