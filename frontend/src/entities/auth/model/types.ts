export type AuthResponse = {
  accessToken: string
  tokenType: string
  email: string
}

export type LoginInput = {
  email: string
  password: string
}

export type RegisterInput = {
  email: string
  password: string
}

export type AuthSession = {
  accessToken: string
  email: string
}
