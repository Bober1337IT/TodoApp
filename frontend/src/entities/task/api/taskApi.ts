import { http } from '@/shared/api/http'
import { mapTaskDtoToTask } from '@/entities/task/lib/mappers'
import type { CreateTaskInput, Task } from '@/entities/task/model/types'

const TASKS_PATH = '/v1/tasks'

export async function listTasks(): Promise<Task[]> {
  const data = await http<unknown[]>(TASKS_PATH)
  return data.map((item) => mapTaskDtoToTask(item as Parameters<typeof mapTaskDtoToTask>[0]))
}

export async function createTask(input: CreateTaskInput): Promise<Task> {
  const data = await http<unknown>(TASKS_PATH, {
    method: 'POST',
    body: input,
  })
  return mapTaskDtoToTask(data as Parameters<typeof mapTaskDtoToTask>[0])
}
