import { toErrorMessage } from '@/shared/api/errors'
import { useTasksQuery } from '@/features/tasks/api/useTasksQuery'
import { TaskItem } from '@/features/tasks/ui/TaskItem'

export function TaskList() {
  const { data, isLoading, isError, error, refetch, isFetching } = useTasksQuery()

  if (isLoading) {
    return <p className="text-sm text-stone-600">Loading tasks…</p>
  }

  if (isError) {
    return (
      <div className="rounded border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-800">
        <p>{toErrorMessage(error, 'Failed to load tasks')}</p>
        <button
          type="button"
          className="mt-2 underline"
          onClick={() => {
            void refetch()
          }}
        >
          Retry
        </button>
      </div>
    )
  }

  if (!data || data.length === 0) {
    return <p className="text-sm text-stone-600">No tasks yet. Create one above.</p>
  }

  return (
    <div className="space-y-2">
      {isFetching ? <p className="text-xs text-stone-400">Refreshing…</p> : null}
      <ul className="space-y-2">
        {data.map((task) => (
          <TaskItem key={task.id} task={task} />
        ))}
      </ul>
    </div>
  )
}
