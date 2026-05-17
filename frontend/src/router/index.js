import Vue from 'vue'
import Router from 'vue-router'
import UserDashboard from '@/views/UserDashboard'
import WorkerDashboard from '@/views/WorkerDashboard'
import AdminDashboard from '@/views/AdminDashboard'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      redirect: '/user'
    },
    {
      path: '/user',
      name: 'UserDashboard',
      component: UserDashboard
    },
    {
      path: '/worker',
      name: 'WorkerDashboard',
      component: WorkerDashboard
    },
    {
      path: '/admin',
      name: 'AdminDashboard',
      component: AdminDashboard
    }
  ]
})
