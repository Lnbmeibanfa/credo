export function buildAuthHeader (token: string | null): Record<string, string> {
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
  }
  if (token) {
    headers.Authorization = `Bearer ${token}`
  }
  return headers
}

export function isUnauthorizedResponse (
  statusCode: number,
  body: { code?: string } | undefined,
): boolean {
  return statusCode === 401 || body?.code === 'UNAUTHORIZED'
}
