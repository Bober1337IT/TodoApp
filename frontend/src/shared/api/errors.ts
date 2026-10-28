export class ApiError extends Error {
  readonly status: number
  readonly details: unknown

  constructor(message: string, status: number, details?: unknown) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.details = details
  }
}

type SpringFieldError = {
  defaultMessage?: string
  field?: string
  message?: string
}

type ErrorBody = {
  message?: string
  detail?: string
  title?: string
  error?: string
  errors?: SpringFieldError[]
  fieldErrors?: Record<string, string[]>
}

function collectFieldMessages(body: ErrorBody): string[] {
  const messages: string[] = []

  if (Array.isArray(body.errors)) {
    for (const item of body.errors) {
      const text = item.defaultMessage ?? item.message
      if (text) {
        messages.push(text)
      }
    }
  }

  if (body.fieldErrors) {
    for (const [field, values] of Object.entries(body.fieldErrors)) {
      for (const value of values) {
        messages.push(`${field}: ${value}`)
      }
    }
  }

  return messages
}

export function messageFromErrorBody(body: unknown, fallback: string): string {
  if (!body || typeof body !== 'object') {
    return fallback
  }

  const errorBody = body as ErrorBody
  const fieldMessages = collectFieldMessages(errorBody)
  if (fieldMessages.length > 0) {
    return fieldMessages.join('; ')
  }

  return (
    errorBody.message ??
    errorBody.detail ??
    errorBody.title ??
    errorBody.error ??
    fallback
  )
}

export function toErrorMessage(error: unknown, fallback = 'Something went wrong'): string {
  if (error instanceof ApiError) {
    return error.message
  }
  if (error instanceof Error) {
    return error.message
  }
  return fallback
}
