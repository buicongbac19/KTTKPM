type Primitive = string | number | boolean

type QueryValue = Primitive | Primitive[] | undefined | null

interface RequestOptions {
  method?: 'GET' | 'POST'
  query?: Record<string, QueryValue>
  body?: unknown
}

const API_BASE = import.meta.env.VITE_API_BASE_URL ?? ''

function buildQuery(query?: Record<string, QueryValue>): string {
  if (!query) {
    return ''
  }

  const params = new URLSearchParams()

  for (const [key, value] of Object.entries(query)) {
    if (value === null || value === undefined || value === '') {
      continue
    }

    if (Array.isArray(value)) {
      value.forEach((item) => params.append(key, String(item)))
      continue
    }

    params.append(key, String(value))
  }

  const serialized = params.toString()
  return serialized ? `?${serialized}` : ''
}

function extractErrorMessage(data: unknown): string {
  if (typeof data === 'object' && data !== null && 'message' in data) {
    const maybeMessage = (data as { message?: unknown }).message
    if (typeof maybeMessage === 'string' && maybeMessage.trim().length > 0) {
      return maybeMessage
    }
  }

  return 'Không thể xử lý yêu cầu. Vui lòng thử lại.'
}

export async function apiRequest<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const method = options.method ?? 'GET'
  const queryString = buildQuery(options.query)

  const headers: Record<string, string> = {}
  if (options.body !== undefined) {
    headers['Content-Type'] = 'application/json'
  }

  const response = await fetch(`${API_BASE}${path}${queryString}`, {
    method,
    headers,
    body: options.body !== undefined ? JSON.stringify(options.body) : undefined,
  })

  const hasJson = response.headers.get('content-type')?.includes('application/json') ?? false
  const payload = hasJson ? ((await response.json()) as unknown) : null

  if (!response.ok) {
    throw new Error(extractErrorMessage(payload))
  }

  return payload as T
}
