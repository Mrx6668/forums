// 从 Vue.js 中导入 createApp 函数
// 从 vue-router 中导入 createRouter 和 createWebHistory 函数
import {createRouter, createWebHistory} from "vue-router";
import {unauthorized} from "@/net";

// 使用 createRouter 函数创建一个路由器实例
const routes = createRouter({
    // 使用 createWebHistory 函数创建一个历史记录实例
    // import.meta.env.BASE_URL 是你的应用的基础 URL
    history: createWebHistory(import.meta.env.BASE_URL),

    // 定义路由规则
    routes: [
        {
            // 当 URL 的路径部分为 "/" 时，这个路由规则会被匹配
            path: "/",
            // 这个路由规则的名字是 'welcome'
            name: 'welcome',
            // 当这个路由规则被匹配时，会动态导入 '../views/WelcomeView.vue' 组件并显示它
            component: () => import('../views/WelcomeView.vue'),

            // 这个路由规则有一个子路由规则
            children: [
                {
                    // 当 URL 的路径部分为 "/welcome" 时，这个子路由规则会被匹配
                    path: "",
                    // 这个子路由规则的名字是 'welcome-login'
                    name: "welcome-login",

                    // 当这个子路由规则被匹配时，会动态导入 '@/views/welcome/LoginPage.vue' 组件并显示它
                    component: () => import("@/views/welcome/LoginPage.vue")
                },
                {
                    path: "register",
                    name: "welcome-register",
                    component: () => import("@/views/welcome/register.vue")
                },
                {
                    path: "reset",
                    name: "welcome-reset",
                    component: () => import("@/views/resetPage.vue")
                }
            ]
        },
        {
            path: '/index',
            name: 'index',
            // redirect: '/index/user-setting', // 重定向到
            // component: () => import('../views/IndexView.vue'),
            component: () => import('../views/IndexView.vue'),
            children: [
                {
                    path: '',
                    name: 'posts',
                    component: () => import("@/views/forum/Forum.vue"),
                    children: [
                        {
                            path: "posts",
                            name: "posts",
                            component: () => import('../views/forum/Posts.vue')
                        },
                        {
                            path: "post-detail/:pid",
                            name: "post-detail",
                            component: () => import('../views/forum/PostDetail.vue')
                        }
                    ]
                },
                {
                    path: "post-detail/:pid",
                    name: "post-detail",
                    component:()=>import('../views/forum/PostDetail.vue')
                },
                {
                    path: "user-setting",
                    name: "user-setting",
                    component: () => import('../views/settings/UserSetting.vue')
                },
                {
                    path: "privacy-setting",
                    name: "privacy-setting",
                    component: () => import('../views/settings/PrivacySetting.vue')
                }
            ]
        }
    ]
})

//路由守卫
routes.beforeEach((to, from, next) => {
    const inUnauthorized = unauthorized();
    if (to.name.startsWith('welcome-') && !inUnauthorized) {
        next('/index');
        //登录情况下，如果用户访问登录页，直接跳转到首页
    } else if (to.fullPath.startsWith('/index') && inUnauthorized) {
        next('/');
        //未登录情况下，如果用户访问首页，直接跳转到登录页
    } else {
        next();
    }
})
// 导出路由器实例，这样其他文件就可以导入它了
export default routes
