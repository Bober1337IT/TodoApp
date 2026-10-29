import { useState } from 'react'
import type { Task } from '@/entities/task/model/types'
import { TaskEditForm } from '@/features/tasks/ui/TaskEditForm'
import { TaskForm } from '@/features/tasks/ui/TaskForm'
import { TaskList } from '@/features/tasks/ui/TaskList'

export function TasksPage() {
  const [editingTask, setEditingTask] = useState<Task | null>(null)

  return (
    <main className="mx-auto max-w-2xl px-4 py-8">
      <header className="mb-6">
        <h1 className="text-2xl font-semibold tracking-tight text-stone-900">Tasks</h1>
        <p className="mt-1 text-sm text-stone-600">Create and review your tasks.</p>
      </header>

      <section className="mb-8" aria-labelledby="task-form-heading">
        <h2
          id="task-form-heading"
          className="mb-3 text-sm font-semibold uppercase tracking-wide text-stone-500"
        >
          {editingTask ? 'Edit task' : 'New task'}
        </h2>
        {editingTask ? (
          <TaskEditForm
            key={editingTask.id}
            task={editingTask}
            onCancel={() => {
              setEditingTask(null)
            }}
            onSaved={() => {
              setEditingTask(null)
            }}
          />
        ) : (
          <TaskForm />
        )}
      </section>

      <section aria-labelledby="task-list-heading">
        <h2
          id="task-list-heading"
          className="mb-3 text-sm font-semibold uppercase tracking-wide text-stone-500"
        >
          All tasks
        </h2>
        <TaskList
          editingTaskId={editingTask?.id ?? null}
          onEditTask={(task) => {
            setEditingTask(task)
          }}
        />
      </section>
    </main>
  )
}
