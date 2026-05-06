export function formatCurrencyVnd(value: number): string {
  return new Intl.NumberFormat('vi-VN', {
    maximumFractionDigits: 0,
  }).format(value)
}

export function parseNumber(value: unknown): number {
  if (typeof value === 'number') {
    return Number.isFinite(value) ? value : 0
  }

  if (typeof value === 'string') {
    const normalized = value.replace(/[^\d.-]/g, '')
    const parsed = Number(normalized)
    return Number.isFinite(parsed) ? parsed : 0
  }

  return 0
}

export function formatDateTime(input: string | null | undefined): string {
  if (!input) {
    return '--:--:-- --/--/----'
  }

  const date = new Date(input)
  if (Number.isNaN(date.getTime())) {
    return input
  }

  return new Intl.DateTimeFormat('vi-VN', {
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
  }).format(date)
}

export function paymentMethodLabel(method: string | null): string {
  if (method === 'CHUYEN_KHOAN_QR') {
    return 'Chuyển khoản QR'
  }

  if (method === 'THE') {
    return 'Quẹt thẻ'
  }

  if (method === 'TIEN_MAT') {
    return 'Tiền mặt'
  }

  return 'Không xác định'
}
