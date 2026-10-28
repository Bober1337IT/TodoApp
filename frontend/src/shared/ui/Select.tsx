import type { SelectHTMLAttributes } from 'react'

type SelectProps = SelectHTMLAttributes<HTMLSelectElement>

export function Select({ className = '', children, ...props }: SelectProps) {
  return (
    <select
      className={`w-full rounded border border-stone-300 bg-white px-3 py-2 text-sm outline-none focus:border-stone-500 ${className}`}
      {...props}
    >
      {children}
    </select>
  )
}
