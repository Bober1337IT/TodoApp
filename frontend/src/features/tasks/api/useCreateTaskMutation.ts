import { useMutation, useQueryClient } from '@tanstack/react-query'
import { createTask } from '@/entities/task/api/taskApi'
import { taskQueryKeys } from '@/entities/task/api/queryKeys'
import type { CreateTaskInput } from '@/entities/task/model/types'

export function useCreateTaskMutation() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (input: CreateTaskInput) => createTask(input),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: taskQueryKeys.lists() })
    },
  })
}
