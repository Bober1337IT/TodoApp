import { useState } from 'react'
import type { Task } from '@/entities/task/model/types'
import { useDeleteTaskMutation } from '@/features/tasks/api/useDeleteTaskMutation'
import { useUpdateTaskMutation } from '@/features/tasks/api/useUpdateTaskMutation'
import { TaskEditForm } from '@/features/tasks/ui/TaskEditForm'
import { toErrorMessage } from '@/shared/api/errors'
import { Button } from '@/shared/ui/Button'

type TaskItemProps = {
  task: Task
}

export function TaskItem({ task }: TaskItemProps) {
  const [isEditing, setIsEditing] = useState(false)
  const deleteTask = useDeleteTaskMutation()
  const updateTask = useUpdateTaskMutation()

  const isComplete = task.status === 'COMPLETE'
  const isBusy = isEditing || deleteTask.isPending || updateTask.isPending

  const toggleStatus = () => {
    void updateTask.mutateAsync({
      id: task.id,
      input: {
        title: task.title,
        description: task.description,
        dueDate: task.dueDate,
        priority: task.priority,
        status: isComplete ? 'OPEN' : 'COMPLETE',
      },
    })
  }

  return (
    <li className="rounded border border-stone-200 bg-white px-4 py-3">
      <div className="flex flex-wrap items-start justify-between gap-2">
        <div className="min-w-0 flex-1">
          <div className="flex flex-wrap items-center gap-3">
            <label className="flex cursor-pointer items-center gap-2 text-sm text-stone-700">
              <input
                type="checkbox"
                className="size-4 accent-stone-800"
                checked={isComplete}
                disabled={isBusy}
                onChange={toggleStatus}
                aria-label={isComplete ? 'Mark task as open' : 'Mark task as complete'}
              />
              <span className="sr-only">Toggle status</span>
            </label>

            <div className="min-w-0 flex-1">
              <div className="flex flex-wrap items-baseline justify-between gap-2">
                <h3
                  className={`text-base font-medium text-stone-900 ${isComplete ? 'line-through text-stone-500' : ''}`}
                >
                  {task.title}
                </h3>
                <span className="text-xs uppercase tracking-wide text-stone-500">
                  {task.priority} · {task.status}
                </span>
              </div>
              {task.description ? (
                <p className="mt-1 text-sm text-stone-600">{task.description}</p>
              ) : null}
              {task.dueDate ? (
                <p className="mt-2 text-xs text-stone-500">Due {task.dueDate}</p>
              ) : null}
            </div>
          </div>

          {deleteTask.isError ? (
            <p className="mt-2 text-xs text-red-700">
              {toErrorMessage(deleteTask.error, 'Failed to delete task')}
            </p>
          ) : null}
          {updateTask.isError ? (
            <p className="mt-2 text-xs text-red-700">
              {toErrorMessage(updateTask.error, 'Failed to update task status')}
            </p>
          ) : null}
        </div>

        <div className="flex shrink-0 flex-wrap gap-2">
          <Button
            type="button"
            variant="secondary"
            disabled={isBusy}
            onClick={() => {
              setIsEditing(true)
            }}
          >
            Edit
          </Button>
          <Button
            type="button"
            variant="danger"
            disabled={isBusy}
            onClick={() => {
              void deleteTask.mutateAsync(task.id)
            }}
          >
            {deleteTask.isPending ? 'Deleting…' : 'Delete'}
          </Button>
        </div>
      </div>

      {isEditing ? (
        <TaskEditForm
          task={task}
          onCancel={() => {
            setIsEditing(false)
          }}
          onSaved={() => {
            setIsEditing(false)
          }}
        />
      ) : null}
    </li>
  )
}
