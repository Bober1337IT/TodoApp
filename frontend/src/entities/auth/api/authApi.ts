import { http } from '@/shared/api/http'
import type { AuthResponse, LoginInput, RegisterInput } from '@/entities/auth/model/types'

const AUTH_PATH = '/v1/auth'

export async function login(input: LoginInput): Promise<AuthResponse> {
  return http<AuthResponse>(`${AUTH_PATH}/login`, {
    method: 'POST',
    body: input,
  })
}

export async function register(input: RegisterInput): Promise<AuthResponse> {
  return http<AuthResponse>(`${AUTH_PATH}/register`, {
    method: 'POST',
    body: input,
  })
}
