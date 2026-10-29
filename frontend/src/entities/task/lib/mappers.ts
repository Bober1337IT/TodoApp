import type {
  CreateTaskInput,
  Task,
  TaskPriority,
  TaskStatus,
  UpdateTaskInput,
} from '@/entities/task/model/types'
import { TASK_PRIORITIES, TASK_STATUSES } from '@/entities/task/model/types'
import type { CreateTaskFormValues, UpdateTaskFormValues } from '@/entities/task/lib/schemas'

type TaskDto = {
  id: string
  title?: string
  name?: string
  description?: string | null
  dueDate?: string | null
  priority: string
  status: string
}

function isTaskPriority(value: string): value is TaskPriority {
  return (TASK_PRIORITIES as readonly string[]).includes(value)
}

function isTaskStatus(value: string): value is TaskStatus {
  return (TASK_STATUSES as readonly string[]).includes(value)
}

export function mapTaskDtoToTask(dto: TaskDto): Task {
  const title = dto.title ?? dto.name
  if (!title) {
    throw new Error('Task response is missing title')
  }
  if (!isTaskPriority(dto.priority)) {
    throw new Error(`Unknown task priority: ${dto.priority}`)
  }
  if (!isTaskStatus(dto.status)) {
    throw new Error(`Unknown task status: ${dto.status}`)
  }

  return {
    id: dto.id,
    title,
    description: dto.description ?? null,
    dueDate: dto.dueDate ?? null,
    priority: dto.priority,
    status: dto.status,
  }
}

export function mapFormValuesToCreateInput(values: CreateTaskFormValues): CreateTaskInput {
  return {
    title: values.title.trim(),
    description: values.description?.trim() ? values.description.trim() : null,
    dueDate: values.dueDate ? values.dueDate : null,
    priority: values.priority,
  }
}

export function mapFormValuesToUpdateInput(values: UpdateTaskFormValues): UpdateTaskInput {
  return {
    title: values.title.trim(),
    description: values.description?.trim() ? values.description.trim() : null,
    dueDate: values.dueDate ? values.dueDate : null,
    priority: values.priority,
    status: values.status,
  }
}

export function mapTaskToUpdateFormValues(task: Task): UpdateTaskFormValues {
  return {
    title: task.title,
    description: task.description ?? '',
    dueDate: task.dueDate ?? '',
    priority: task.priority,
    status: task.status,
  }
}
