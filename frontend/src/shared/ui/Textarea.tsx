import type { TextareaHTMLAttributes } from 'react'

type TextareaProps = TextareaHTMLAttributes<HTMLTextAreaElement>

export function Textarea({ className = '', ...props }: TextareaProps) {
  return (
    <textarea
      className={`w-full rounded border border-stone-300 bg-white px-3 py-2 text-sm outline-none focus:border-stone-500 ${className}`}
      {...props}
    />
  )
}
