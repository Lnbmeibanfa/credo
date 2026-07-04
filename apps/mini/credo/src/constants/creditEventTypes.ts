export enum CreditEventType {
  SIGN = 'SIGN',
  FULFILLED = 'FULFILLED',
  BREACH = 'BREACH',
  REMEDY = 'REMEDY',
  EXEMPT = 'EXEMPT',
  PENDING = 'PENDING',
  UPCOMING = 'UPCOMING',
}

export interface CreditEventVisual {
  icon: string
  label: string
  color: string
  tagColor: string
}

export const CREDIT_EVENT_VISUALS: Record<CreditEventType, CreditEventVisual> = {
  [CreditEventType.SIGN]: {
    icon: 'S',
    label: '签署',
    color: '#111111',
    tagColor: '#111111',
  },
  [CreditEventType.FULFILLED]: {
    icon: '+',
    label: '履约',
    color: '#2d6a4f',
    tagColor: '#2d6a4f',
  },
  [CreditEventType.BREACH]: {
    icon: '-',
    label: '未履约',
    color: '#8b4049',
    tagColor: '#8b4049',
  },
  [CreditEventType.REMEDY]: {
    icon: 'R',
    label: '补救',
    color: '#666666',
    tagColor: '#666666',
  },
  [CreditEventType.EXEMPT]: {
    icon: 'O',
    label: '豁免',
    color: '#8b6914',
    tagColor: '#8b6914',
  },
  [CreditEventType.PENDING]: {
    icon: '…',
    label: '待登记',
    color: '#888888',
    tagColor: '#888888',
  },
  [CreditEventType.UPCOMING]: {
    icon: '—',
    label: '未到期',
    color: '#aaaaaa',
    tagColor: '#aaaaaa',
  },
}

export function getCreditEventVisual (type: CreditEventType): CreditEventVisual {
  return CREDIT_EVENT_VISUALS[type]
}
