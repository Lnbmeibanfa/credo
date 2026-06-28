import { Textarea } from '@tarojs/components'
import './index.scss'

interface TextAreaFieldProps {
  value: string
  onChange: (value: string) => void
  placeholder?: string
  className?: string
}

export default function TextAreaField ({
  value,
  onChange,
  placeholder = '',
  className = '',
}: TextAreaFieldProps) {
  return (
    <Textarea
      className={`credo-textarea-field ${className}`.trim()}
      value={value}
      placeholder={placeholder}
      onInput={(e) => onChange(e.detail.value)}
      maxlength={500}
      autoHeight
    />
  )
}
