import { useMutation, useQueryClient } from '@tanstack/react-query'
import { deleteTask } from '@/entities/task/api/taskApi'
import { taskQueryKeys } from '@/entities/task/api/queryKeys'

export function useDeleteTaskMutation() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: string) => deleteTask(id),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: taskQueryKeys.lists() })
    },
  })
}
