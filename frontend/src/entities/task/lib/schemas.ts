import { z } from 'zod'
import { TASK_PRIORITIES, TASK_STATUSES } from '@/entities/task/model/types'

const titleSchema = z
  .string()
  .trim()
  .min(1, 'Title must be between 1 and 255 characters')
  .max(255, 'Title must be between 1 and 255 characters')

const descriptionSchema = z.string().max(1000, 'Description must be at most 1000 characters')

const dueDateSchema = z.string().refine((value) => {
  if (!value) {
    return true
  }
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  const due = new Date(`${value}T00:00:00`)
  return !Number.isNaN(due.getTime()) && due >= today
}, 'Date must be in future or present')

export const createTaskSchema = z.object({
  title: titleSchema,
  description: descriptionSchema,
  dueDate: dueDateSchema,
  priority: z.enum(TASK_PRIORITIES),
})

export const updateTaskSchema = z.object({
  title: titleSchema,
  description: descriptionSchema,
  dueDate: dueDateSchema,
  priority: z.enum(TASK_PRIORITIES),
  status: z.enum(TASK_STATUSES),
})

export type CreateTaskFormValues = z.infer<typeof createTaskSchema>
export type UpdateTaskFormValues = z.infer<typeof updateTaskSchema>
