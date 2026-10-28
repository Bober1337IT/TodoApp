import { z } from 'zod'
import { TASK_PRIORITIES } from '@/entities/task/model/types'

export const createTaskSchema = z.object({
  title: z
    .string()
    .trim()
    .min(1, 'Title must be between 1 and 255 characters')
    .max(255, 'Title must be between 1 and 255 characters'),
  description: z.string().max(1000, 'Description must be at most 1000 characters'),
  dueDate: z.string().refine((value) => {
    if (!value) {
      return true
    }
    const today = new Date()
    today.setHours(0, 0, 0, 0)
    const due = new Date(`${value}T00:00:00`)
    return !Number.isNaN(due.getTime()) && due >= today
  }, 'Date must be in future or present'),
  priority: z.enum(TASK_PRIORITIES),
})

export type CreateTaskFormValues = z.infer<typeof createTaskSchema>
