import { zodResolver } from '@hookform/resolvers/zod'
import { useForm } from 'react-hook-form'
import {
  mapFormValuesToUpdateInput,
  mapTaskToUpdateFormValues,
} from '@/entities/task/lib/mappers'
import { updateTaskSchema, type UpdateTaskFormValues } from '@/entities/task/lib/schemas'
import type { Task } from '@/entities/task/model/types'
import { TASK_PRIORITIES, TASK_STATUSES } from '@/entities/task/model/types'
import { useUpdateTaskMutation } from '@/features/tasks/api/useUpdateTaskMutation'
import { toErrorMessage } from '@/shared/api/errors'
import { Button } from '@/shared/ui/Button'
import { Input } from '@/shared/ui/Input'
import { Select } from '@/shared/ui/Select'
import { Textarea } from '@/shared/ui/Textarea'

type TaskEditFormProps = {
  task: Task
  onCancel: () => void
  onSaved: () => void
}

export function TaskEditForm({ task, onCancel, onSaved }: TaskEditFormProps) {
  const updateTaskMutation = useUpdateTaskMutation()
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<UpdateTaskFormValues>({
    resolver: zodResolver(updateTaskSchema),
    defaultValues: mapTaskToUpdateFormValues(task),
  })

  const onSubmit = handleSubmit(async (values) => {
    try {
      await updateTaskMutation.mutateAsync({
        id: task.id,
        input: mapFormValuesToUpdateInput(values),
      })
      onSaved()
    } catch {
      // error shown via updateTaskMutation.error
    }
  })

  return (
    <form onSubmit={onSubmit} className="space-y-3 rounded border border-stone-200 bg-white p-4">
      <div>
        <label htmlFor={`edit-title-${task.id}`} className="mb-1 block text-sm font-medium text-stone-700">
          Title
        </label>
        <Input id={`edit-title-${task.id}`} {...register('title')} />
        {errors.title ? <p className="mt-1 text-xs text-red-700">{errors.title.message}</p> : null}
      </div>

      <div>
        <label
          htmlFor={`edit-description-${task.id}`}
          className="mb-1 block text-sm font-medium text-stone-700"
        >
          Description
        </label>
        <Textarea id={`edit-description-${task.id}`} rows={3} {...register('description')} />
        {errors.description ? (
          <p className="mt-1 text-xs text-red-700">{errors.description.message}</p>
        ) : null}
      </div>

      <div className="grid gap-3 sm:grid-cols-3">
        <div>
          <label
            htmlFor={`edit-dueDate-${task.id}`}
            className="mb-1 block text-sm font-medium text-stone-700"
          >
            Due date
          </label>
          <Input id={`edit-dueDate-${task.id}`} type="date" {...register('dueDate')} />
          {errors.dueDate ? (
            <p className="mt-1 text-xs text-red-700">{errors.dueDate.message}</p>
          ) : null}
        </div>

        <div>
          <label
            htmlFor={`edit-priority-${task.id}`}
            className="mb-1 block text-sm font-medium text-stone-700"
          >
            Priority
          </label>
          <Select id={`edit-priority-${task.id}`} {...register('priority')}>
            {TASK_PRIORITIES.map((priority) => (
              <option key={priority} value={priority}>
                {priority}
              </option>
            ))}
          </Select>
          {errors.priority ? (
            <p className="mt-1 text-xs text-red-700">{errors.priority.message}</p>
          ) : null}
        </div>

        <div>
          <label
            htmlFor={`edit-status-${task.id}`}
            className="mb-1 block text-sm font-medium text-stone-700"
          >
            Status
          </label>
          <Select id={`edit-status-${task.id}`} {...register('status')}>
            {TASK_STATUSES.map((status) => (
              <option key={status} value={status}>
                {status}
              </option>
            ))}
          </Select>
          {errors.status ? (
            <p className="mt-1 text-xs text-red-700">{errors.status.message}</p>
          ) : null}
        </div>
      </div>

      {updateTaskMutation.isError ? (
        <p className="text-sm text-red-700">
          {toErrorMessage(updateTaskMutation.error, 'Failed to update task')}
        </p>
      ) : null}

      <div className="flex flex-wrap gap-2">
        <Button type="submit" disabled={isSubmitting || updateTaskMutation.isPending}>
          {updateTaskMutation.isPending ? 'Saving…' : 'Save'}
        </Button>
        <Button type="button" variant="secondary" disabled={updateTaskMutation.isPending} onClick={onCancel}>
          Cancel
        </Button>
      </div>
    </form>
  )
}
