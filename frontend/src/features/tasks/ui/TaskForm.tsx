import { zodResolver } from '@hookform/resolvers/zod'
import { useForm } from 'react-hook-form'
import { createTaskSchema, type CreateTaskFormValues } from '@/entities/task/lib/schemas'
import { mapFormValuesToCreateInput } from '@/entities/task/lib/mappers'
import { TASK_PRIORITIES } from '@/entities/task/model/types'
import { useCreateTaskMutation } from '@/features/tasks/api/useCreateTaskMutation'
import { toErrorMessage } from '@/shared/api/errors'
import { Button } from '@/shared/ui/Button'
import { Input } from '@/shared/ui/Input'
import { Select } from '@/shared/ui/Select'
import { Textarea } from '@/shared/ui/Textarea'

const defaultValues: CreateTaskFormValues = {
  title: '',
  description: '',
  dueDate: '',
  priority: 'MEDIUM',
}

export function TaskForm() {
  const createTask = useCreateTaskMutation()
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<CreateTaskFormValues>({
    resolver: zodResolver(createTaskSchema),
    defaultValues,
  })

  const onSubmit = handleSubmit(async (values) => {
    try {
      await createTask.mutateAsync(mapFormValuesToCreateInput(values))
      reset(defaultValues)
    } catch {
      // error shown via createTask.error
    }
  })

  return (
    <form onSubmit={onSubmit} className="space-y-3 rounded border border-stone-200 bg-white p-4">
      <div>
        <label htmlFor="title" className="mb-1 block text-sm font-medium text-stone-700">
          Title
        </label>
        <Input id="title" {...register('title')} />
        {errors.title ? (
          <p className="mt-1 text-xs text-red-700">{errors.title.message}</p>
        ) : null}
      </div>

      <div>
        <label htmlFor="description" className="mb-1 block text-sm font-medium text-stone-700">
          Description
        </label>
        <Textarea id="description" rows={3} {...register('description')} />
        {errors.description ? (
          <p className="mt-1 text-xs text-red-700">{errors.description.message}</p>
        ) : null}
      </div>

      <div className="grid gap-3 sm:grid-cols-2">
        <div>
          <label htmlFor="dueDate" className="mb-1 block text-sm font-medium text-stone-700">
            Due date
          </label>
          <Input id="dueDate" type="date" {...register('dueDate')} />
          {errors.dueDate ? (
            <p className="mt-1 text-xs text-red-700">{errors.dueDate.message}</p>
          ) : null}
        </div>

        <div>
          <label htmlFor="priority" className="mb-1 block text-sm font-medium text-stone-700">
            Priority
          </label>
          <Select id="priority" {...register('priority')}>
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
      </div>

      {createTask.isError ? (
        <p className="text-sm text-red-700">
          {toErrorMessage(createTask.error, 'Failed to create task')}
        </p>
      ) : null}

      <Button type="submit" disabled={isSubmitting || createTask.isPending}>
        {createTask.isPending ? 'Adding…' : 'Add task'}
      </Button>
    </form>
  )
}
