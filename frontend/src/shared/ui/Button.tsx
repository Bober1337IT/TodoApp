import type { ButtonHTMLAttributes } from 'react'

type ButtonVariant = 'primary' | 'secondary' | 'danger'

type ButtonProps = ButtonHTMLAttributes<HTMLButtonElement> & {
  variant?: ButtonVariant
}

const variantClassName: Record<ButtonVariant, string> = {
  primary:
    'border-stone-700 bg-stone-800 text-white hover:bg-stone-700',
  secondary:
    'border-stone-400 bg-stone-100 text-stone-900 hover:bg-stone-200',
  danger:
    'border-red-800 bg-red-700 text-white hover:bg-red-800',
}

export function Button({
  className = '',
  disabled,
  variant = 'primary',
  type = 'button',
  ...props
}: ButtonProps) {
  return (
    <button
      type={type}
      disabled={disabled}
      className={`rounded border px-3 py-2 text-sm font-medium disabled:cursor-not-allowed disabled:opacity-50 ${variantClassName[variant]} ${className}`}
      {...props}
    />
  )
}
