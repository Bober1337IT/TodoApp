import {
  clearSessionAndNotify,
  getAccessToken,
} from '@/entities/auth/lib/tokenStorage'
import { env } from '@/shared/config/env'
import { ApiError, messageFromErrorBody } from '@/shared/api/errors'

type RequestOptions = Omit<RequestInit, 'body'> & {
  body?: unknown
}

async function parseBody(response: Response): Promise<unknown> {
  if (response.status === 204) {
    return undefined
  }

  const contentType = response.headers.get('content-type') ?? ''
  if (contentType.includes('application/json')) {
    return response.json()
  }

  const text = await response.text()
  return text.length > 0 ? text : undefined
}

function isAuthPath(path: string): boolean {
  return path.includes('/v1/auth/')
}

export async function http<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const { body, headers, ...rest } = options
  const url = `${env.apiBaseUrl}${path.startsWith('/') ? path : `/${path}`}`
  const token = getAccessToken()

  const response = await fetch(url, {
    ...rest,
    headers: {
      ...(body !== undefined ? { 'Content-Type': 'application/json' } : {}),
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...headers,
    },
    body: body === undefined ? undefined : JSON.stringify(body),
  })

  const parsed = await parseBody(response)

  if (!response.ok) {
    if (response.status === 401 && !isAuthPath(path)) {
      clearSessionAndNotify()
    }

    throw new ApiError(
      messageFromErrorBody(parsed, `Request failed with status ${response.status}`),
      response.status,
      parsed,
    )
  }

  return parsed as T
}
