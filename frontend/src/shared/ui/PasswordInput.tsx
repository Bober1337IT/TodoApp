import { useState, type InputHTMLAttributes } from 'react'
import { Input } from '@/shared/ui/Input'

type PasswordInputProps = Omit<InputHTMLAttributes<HTMLInputElement>, 'type'> & {
  toggleLabelShow?: string
  toggleLabelHide?: string
}

export function PasswordInput({
  className = '',
  id,
  toggleLabelShow = 'Show password',
  toggleLabelHide = 'Hide password',
  ...props
}: PasswordInputProps) {
  const [visible, setVisible] = useState(false)

  return (
    <div className="relative">
      <Input
        id={id}
        type={visible ? 'text' : 'password'}
        className={`pr-20 ${className}`}
        {...props}
      />
      <button
        type="button"
        className="absolute top-1/2 right-2 -translate-y-1/2 rounded px-2 py-1 text-xs font-medium text-stone-600 hover:bg-stone-100 hover:text-stone-900"
        onClick={() => {
          setVisible((current) => !current)
        }}
        aria-label={visible ? toggleLabelHide : toggleLabelShow}
        aria-pressed={visible}
      >
        {visible ? 'Hide' : 'Show'}
      </button>
    </div>
  )
}
