import { useQueryClient } from '@tanstack/react-query'
import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
  type ReactNode,
} from 'react'
import { login as loginRequest, register as registerRequest } from '@/entities/auth/api/authApi'
import {
  clearSession,
  getStoredSession,
  saveSession,
  SESSION_CLEARED_EVENT,
} from '@/entities/auth/lib/tokenStorage'
import type { AuthSession, LoginInput, RegisterInput } from '@/entities/auth/model/types'

type AuthContextValue = {
  session: AuthSession | null
  login: (input: LoginInput) => Promise<void>
  register: (input: RegisterInput) => Promise<void>
  logout: () => void
}

const AuthContext = createContext<AuthContextValue | null>(null)

type AuthProviderProps = {
  children: ReactNode
}

export function AuthProvider({ children }: AuthProviderProps) {
  const queryClient = useQueryClient()
  const [session, setSession] = useState<AuthSession | null>(() => getStoredSession())

  const logout = useCallback(() => {
    clearSession()
    setSession(null)
    queryClient.clear()
  }, [queryClient])

  useEffect(() => {
    const onSessionCleared = () => {
      setSession(null)
      queryClient.clear()
    }
    window.addEventListener(SESSION_CLEARED_EVENT, onSessionCleared)
    return () => {
      window.removeEventListener(SESSION_CLEARED_EVENT, onSessionCleared)
    }
  }, [queryClient])

  const login = useCallback(async (input: LoginInput) => {
    const response = await loginRequest(input)
    const nextSession = { accessToken: response.accessToken, email: response.email }
    saveSession(nextSession)
    setSession(nextSession)
  }, [])

  const register = useCallback(async (input: RegisterInput) => {
    const response = await registerRequest(input)
    const nextSession = { accessToken: response.accessToken, email: response.email }
    saveSession(nextSession)
    setSession(nextSession)
  }, [])

  const value = useMemo(
    () => ({
      session,
      login,
      register,
      logout,
    }),
    [session, login, register, logout],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth(): AuthContextValue {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider')
  }
  return context
}
