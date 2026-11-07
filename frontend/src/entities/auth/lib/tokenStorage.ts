import type { AuthSession } from '@/entities/auth/model/types'

const TOKEN_KEY = 'todoapp.accessToken'
const EMAIL_KEY = 'todoapp.email'

export const SESSION_CLEARED_EVENT = 'todoapp:session-cleared'

export function getAccessToken(): string | null {
  return localStorage.getItem(TOKEN_KEY)
}

export function getStoredSession(): AuthSession | null {
  const accessToken = localStorage.getItem(TOKEN_KEY)
  const email = localStorage.getItem(EMAIL_KEY)
  if (!accessToken || !email) {
    return null
  }
  return { accessToken, email }
}

export function saveSession(session: AuthSession): void {
  localStorage.setItem(TOKEN_KEY, session.accessToken)
  localStorage.setItem(EMAIL_KEY, session.email)
}

export function clearSession(): void {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(EMAIL_KEY)
}

export function clearSessionAndNotify(): void {
  clearSession()
  window.dispatchEvent(new Event(SESSION_CLEARED_EVENT))
}
