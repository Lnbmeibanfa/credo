export interface GridOption {
  id: string
  label: string
}

export const EXEMPTION_OPTIONS: GridOption[] = [
  { id: 'overtime', label: '加班' },
  { id: 'illness', label: '生病' },
  { id: 'family', label: '家庭紧急情况' },
  { id: 'travel', label: '出差' },
  { id: 'other', label: '其它' },
]
