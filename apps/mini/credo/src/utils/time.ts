/**
 * Parse "HH:mm" into today's Date at that local time.
 */
export function parseTimeString (time: string, baseDate: Date = new Date()): Date {
  const [hours, minutes] = time.split(':').map(Number)
  const date = new Date(baseDate)
  date.setHours(hours, minutes, 0, 0)
  return date
}

/**
 * Seconds remaining until target; 0 if target is in the past.
 */
export function getRemainingSeconds (target: Date, now: Date = new Date()): number {
  const diff = Math.floor((target.getTime() - now.getTime()) / 1000)
  return Math.max(0, diff)
}

/**
 * Format seconds as "HH : mm : ss" with zero-padded segments.
 */
export function formatCountdown (totalSeconds: number): string {
  const hours = Math.floor(totalSeconds / 3600)
  const minutes = Math.floor((totalSeconds % 3600) / 60)
  const seconds = totalSeconds % 60
  const pad = (n: number) => String(n).padStart(2, '0')
  return `${pad(hours)} : ${pad(minutes)} : ${pad(seconds)}`
}

/**
 * Combine a date with "HH:mm" for countdown target.
 */
export function toTargetDateTime (time: string, baseDate: Date = new Date()): Date {
  return parseTimeString(time, baseDate)
}
