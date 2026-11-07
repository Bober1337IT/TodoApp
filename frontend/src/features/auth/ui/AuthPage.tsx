import { zodResolver } from '@hookform/resolvers/zod'
import { useState } from 'react'
import { useForm } from 'react-hook-form'
import {
  loginSchema,
  registerSchema,
  type LoginFormValues,
  type RegisterFormValues,
} from '@/entities/auth/lib/schemas'
import { useAuth } from '@/features/auth/model/AuthContext'
import { toErrorMessage } from '@/shared/api/errors'
import { Button } from '@/shared/ui/Button'
import { Input } from '@/shared/ui/Input'

type AuthMode = 'login' | 'register'

export function AuthPage() {
  const { login, register } = useAuth()
  const [mode, setMode] = useState<AuthMode>('login')
  const [formError, setFormError] = useState<string | null>(null)

  const loginForm = useForm<LoginFormValues>({
    resolver: zodResolver(loginSchema),
    defaultValues: { email: '', password: '' },
  })

  const registerForm = useForm<RegisterFormValues>({
    resolver: zodResolver(registerSchema),
    defaultValues: { email: '', password: '' },
  })

  const switchMode = (next: AuthMode) => {
    setMode(next)
    setFormError(null)
    loginForm.reset({ email: '', password: '' })
    registerForm.reset({ email: '', password: '' })
  }

  const onLogin = loginForm.handleSubmit(async (values) => {
    setFormError(null)
    try {
      await login(values)
    } catch (error) {
      setFormError(toErrorMessage(error, 'Login failed'))
    }
  })

  const onRegister = registerForm.handleSubmit(async (values) => {
    setFormError(null)
    try {
      await register(values)
    } catch (error) {
      setFormError(toErrorMessage(error, 'Registration failed'))
    }
  })

  const activeForm = mode === 'login' ? loginForm : registerForm
  const isSubmitting = activeForm.formState.isSubmitting

  return (
    <main className="mx-auto flex min-h-dvh max-w-md flex-col justify-center px-4 py-8">
      <header className="mb-6">
        <h1 className="text-2xl font-semibold tracking-tight text-stone-900">Todoapp</h1>
        <p className="mt-1 text-sm text-stone-600">
          {mode === 'login' ? 'Sign in to manage your tasks.' : 'Create an account to get started.'}
        </p>
      </header>

      <div className="mb-4 flex gap-2" role="tablist" aria-label="Authentication mode">
        <Button
          type="button"
          variant={mode === 'login' ? 'primary' : 'secondary'}
          className="flex-1"
          onClick={() => {
            switchMode('login')
          }}
        >
          Sign in
        </Button>
        <Button
          type="button"
          variant={mode === 'register' ? 'primary' : 'secondary'}
          className="flex-1"
          onClick={() => {
            switchMode('register')
          }}
        >
          Register
        </Button>
      </div>

      {mode === 'login' ? (
        <form
          onSubmit={onLogin}
          className="space-y-3 rounded border border-stone-200 bg-white p-4"
          noValidate
        >
          <div>
            <label htmlFor="login-email" className="mb-1 block text-sm font-medium text-stone-700">
              Email
            </label>
            <Input id="login-email" type="email" autoComplete="email" {...loginForm.register('email')} />
            {loginForm.formState.errors.email ? (
              <p className="mt-1 text-xs text-red-700">{loginForm.formState.errors.email.message}</p>
            ) : null}
          </div>

          <div>
            <label
              htmlFor="login-password"
              className="mb-1 block text-sm font-medium text-stone-700"
            >
              Password
            </label>
            <Input
              id="login-password"
              type="password"
              autoComplete="current-password"
              {...loginForm.register('password')}
            />
            {loginForm.formState.errors.password ? (
              <p className="mt-1 text-xs text-red-700">
                {loginForm.formState.errors.password.message}
              </p>
            ) : null}
          </div>

          {formError ? <p className="text-sm text-red-700">{formError}</p> : null}

          <Button type="submit" disabled={isSubmitting} className="w-full">
            {isSubmitting ? 'Signing in…' : 'Sign in'}
          </Button>
        </form>
      ) : (
        <form
          onSubmit={onRegister}
          className="space-y-3 rounded border border-stone-200 bg-white p-4"
          noValidate
        >
          <div>
            <label
              htmlFor="register-email"
              className="mb-1 block text-sm font-medium text-stone-700"
            >
              Email
            </label>
            <Input
              id="register-email"
              type="email"
              autoComplete="email"
              {...registerForm.register('email')}
            />
            {registerForm.formState.errors.email ? (
              <p className="mt-1 text-xs text-red-700">
                {registerForm.formState.errors.email.message}
              </p>
            ) : null}
          </div>

          <div>
            <label
              htmlFor="register-password"
              className="mb-1 block text-sm font-medium text-stone-700"
            >
              Password
            </label>
            <Input
              id="register-password"
              type="password"
              autoComplete="new-password"
              {...registerForm.register('password')}
            />
            {registerForm.formState.errors.password ? (
              <p className="mt-1 text-xs text-red-700">
                {registerForm.formState.errors.password.message}
              </p>
            ) : null}
            <p className="mt-1 text-xs text-stone-500">
              At least 8 characters with upper, lower, digit and special (@$!%*?&).
            </p>
          </div>

          {formError ? <p className="text-sm text-red-700">{formError}</p> : null}

          <Button type="submit" disabled={isSubmitting} className="w-full">
            {isSubmitting ? 'Creating account…' : 'Create account'}
          </Button>
        </form>
      )}
    </main>
  )
}
