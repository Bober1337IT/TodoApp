import { AppProviders } from '@/app/providers'
import { TasksPage } from '@/pages/TasksPage'

export default function App() {
  return (
    <AppProviders>
      <TasksPage />
    </AppProviders>
  )
}
