import { useMutation, useQueryClient } from '@tanstack/react-query'
import { updateTaskStatus } from '@/entities/task/api/taskApi'
import { taskQueryKeys } from '@/entities/task/api/queryKeys'
import type { Task, UpdateTaskStatusInput } from '@/entities/task/model/types'

type UpdateTaskStatusVariables = {
  id: string
  input: UpdateTaskStatusInput
}

export function useUpdateTaskStatusMutation() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, input }: UpdateTaskStatusVariables) => updateTaskStatus(id, input),
    onMutate: async ({ id, input }) => {
      await queryClient.cancelQueries({ queryKey: taskQueryKeys.lists() })

      const previousTasks = queryClient.getQueryData<Task[]>(taskQueryKeys.lists())

      queryClient.setQueryData<Task[]>(taskQueryKeys.lists(), (current) =>
        current?.map((task) => (task.id === id ? { ...task, status: input.status } : task)),
      )

      return { previousTasks }
    },
    onError: (_error, _variables, context) => {
      if (context?.previousTasks) {
        queryClient.setQueryData(taskQueryKeys.lists(), context.previousTasks)
      }
    },
    onSettled: async () => {
      await queryClient.invalidateQueries({ queryKey: taskQueryKeys.lists() })
    },
  })
}
