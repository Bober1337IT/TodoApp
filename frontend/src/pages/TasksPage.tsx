import { TaskForm } from '@/features/tasks/ui/TaskForm'
import { TaskList } from '@/features/tasks/ui/TaskList'

export function TasksPage() {
  return (
    <main className="mx-auto max-w-2xl px-4 py-8">
      <header className="mb-6">
        <h1 className="text-2xl font-semibold tracking-tight text-stone-900">Tasks</h1>
        <p className="mt-1 text-sm text-stone-600">Create and review your tasks.</p>
      </header>

      <section className="mb-8" aria-labelledby="create-task-heading">
        <h2 id="create-task-heading" className="mb-3 text-sm font-semibold uppercase tracking-wide text-stone-500">
          New task
        </h2>
        <TaskForm />
      </section>

      <section aria-labelledby="task-list-heading">
        <h2 id="task-list-heading" className="mb-3 text-sm font-semibold uppercase tracking-wide text-stone-500">
          All tasks
        </h2>
        <TaskList />
      </section>
    </main>
  )
}
