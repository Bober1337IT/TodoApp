export const TASK_PRIORITIES = ['HIGH', 'MEDIUM', 'LOW'] as const
export type TaskPriority = (typeof TASK_PRIORITIES)[number]

export const TASK_STATUSES = ['OPEN', 'COMPLETE'] as const
export type TaskStatus = (typeof TASK_STATUSES)[number]

export type Task = {
  id: string
  title: string
  description: string | null
  dueDate: string | null
  priority: TaskPriority
  status: TaskStatus
}

export type CreateTaskInput = {
  title: string
  description?: string | null
  dueDate?: string | null
  priority: TaskPriority
}
