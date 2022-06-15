import { createRouter, createWebHashHistory } from 'vue-router';

export const routes = [
    { path: '/', component: () => import('../components/homePage.vue') },
    { path: '/sshWindow', component: () => import('../components/shellWin.vue') },
]

const router = new createRouter({
    routes,
    history: createWebHashHistory()
});

export default router;
