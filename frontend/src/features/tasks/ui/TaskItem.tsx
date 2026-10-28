import type { Task } from '@/entities/task/model/types'

type TaskItemProps = {
  task: Task
}

export function TaskItem({ task }: TaskItemProps) {
  return (
    <li className="rounded border border-stone-200 bg-white px-4 py-3">
      <div className="flex flex-wrap items-baseline justify-between gap-2">
        <h3 className="text-base font-medium text-stone-900">{task.title}</h3>
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
    </li>
  )
}
