import { useMutation, useQueryClient } from '@tanstack/react-query'
import { updateTask } from '@/entities/task/api/taskApi'
import { taskQueryKeys } from '@/entities/task/api/queryKeys'
import type { UpdateTaskInput } from '@/entities/task/model/types'

type UpdateTaskVariables = {
  id: string
  input: UpdateTaskInput
}

export function useUpdateTaskMutation() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, input }: UpdateTaskVariables) => updateTask(id, input),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: taskQueryKeys.lists() })
    },
  })
}
