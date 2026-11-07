import { AppProviders } from '@/app/providers'
import { AuthProvider, useAuth } from '@/features/auth/model/AuthContext'
import { AuthPage } from '@/features/auth/ui/AuthPage'
import { TasksPage } from '@/pages/TasksPage'

function AppGate() {
  const { session } = useAuth()
  return session ? <TasksPage /> : <AuthPage />
}

export default function App() {
  return (
    <AppProviders>
      <AuthProvider>
        <AppGate />
      </AuthProvider>
    </AppProviders>
  )
}
