import { useQuery } from '@tanstack/react-query'
import { listTasks } from '@/entities/task/api/taskApi'
import { taskQueryKeys } from '@/entities/task/api/queryKeys'

export function useTasksQuery() {
  return useQuery({
    queryKey: taskQueryKeys.lists(),
    queryFn: listTasks,
  })
}
