const rawBaseUrl = import.meta.env.VITE_API_URL ?? '/api'

export const env = {
  apiBaseUrl: rawBaseUrl.replace(/\/$/, ''),
} as const
