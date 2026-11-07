import { z } from 'zod'

const emailSchema = z.email('Enter a valid email address')

const loginPasswordSchema = z
  .string()
  .min(1, 'Password must not be blank')

const registerPasswordSchema = z
  .string()
  .min(8, 'Password must be between 8 and 64 characters')
  .max(64, 'Password must be between 8 and 64 characters')
  .regex(
    /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]+$/,
    'Password must contain uppercase, lowercase, digit and special character (@$!%*?&)',
  )

export const loginSchema = z.object({
  email: emailSchema,
  password: loginPasswordSchema,
})

export const registerSchema = z
  .object({
    email: emailSchema,
    password: registerPasswordSchema,
    confirmPassword: z.string().min(1, 'Confirm your password'),
  })
  .refine((values) => values.password === values.confirmPassword, {
    message: 'Passwords must match',
    path: ['confirmPassword'],
  })

export type LoginFormValues = z.infer<typeof loginSchema>
export type RegisterFormValues = z.infer<typeof registerSchema>
