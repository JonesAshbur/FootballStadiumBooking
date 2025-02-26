import Vue from "vue"
import VueRouter from "vue-router"

Vue.use(VueRouter);

const routes = [
        {
            path: '/',
            redirect: '/dash-board',
            meta: {
                loginRequire: true
            }
        },
        {
            path: '/',
            name: 'Home',
            component: () => import('@/views/system/Home'),
            meta: {
                loginRequire: true
            },
            children: [
                {
                    path: 'dash-board',
                    name: 'DashBoard',
                    component: () => import('@/views/system/DashBoard'),
                    meta: {
                        title: '系统首页',
                        loginRequire: true
                    }
                },
                {
                    path: 'user-list',
                    name: 'UserList',
                    component: () => import('@/views/user/UserList'),
                    meta: {
                        title: '用户列表',
                        loginRequire: true
                    }
                },
                {
                    path: 'hall-list',
                    name: 'HallList',
                    component: () => import('@/views/hall/HallList'),
                    meta: {
                        title: '足球场列表',
                        loginRequire: true
                    }
                },
                {
                    path: 'equipment-list',
                    name: 'EquipmentList',
                    component: () => import('@/views/equipment/EquipmentList'),
                    meta: {
                        title: '器材列表',
                        loginRequire: true
                    }
                },
                {
                    path: 'rental-list',
                    name: 'RentalList',
                    component: () => import('@/views/rental/RentalList'),
                    meta: {
                        title: '租借器材列表',
                        loginRequire: true
                    }
                },
                {
                    path: 'appointment-list',
                    name: 'AppointmentList',
                    component: () => import('@/views/appointment/AppointmentList'),
                    meta: {
                        title: '预约场地列表',
                        loginRequire: true
                    }
                },
                {
                    path: 'announce-list',
                    name: 'AnnounceList',
                    component: () => import('@/views/announce/AnnounceList'),
                    meta: {
                        title: '公告列表',
                        loginRequire: true
                    }
                }
            ]
        },
        {
            path: '/login',
            name: 'Login',
            component: () => import('@/views/Login'),
            meta: {
                title: '足球场预约'
            }
        },
        {
            path: '/admin-login',
            name: 'AdminLogin',
            component: () => import('@/views/AdminLogin'),
            meta: {
                title: '管理员登录'
            }
        },
        {
            path: '/register',
            name: 'Register',
            component: () => import('@/views/Register'),
            meta: {
                title: '用户注册'
            }
        },
        {
            path: '/',
            redirect: '/login',
        }
      ];

const router = new VueRouter({
    mode: "history",
    base: process.env.BASE_URL,
    routes
});

// 导航栏中用到了路由 防止重复点同一个菜单给出错误提示
const originalPush = VueRouter.prototype.push;
VueRouter.prototype.push = function push(location) {
    return originalPush.call(this, location).catch(err => err)
};


export default router;
