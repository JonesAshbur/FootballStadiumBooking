<template>
    <div class="sidebar" :class="{'user-sidebar': loginUser.roleId !== 2}">
        <el-menu
            class="sidebar-el-menu"
            :default-active="onRoutes"
            :collapse="collapse"
            :background-color="loginUser.roleId === 2 ? '#324157' : 'transparent'"
            :text-color="loginUser.roleId === 2 ? '#bfcbd9' : '#fff'"
            :active-text-color="loginUser.roleId === 2 ? '#20a0ff' : '#fff'"
            unique-opened
            router
        >
            <template v-if="loginUser.roleId === 2">
                <template v-for="item in adminItems">
                    <template v-if="item.subs">
                        <el-submenu :index="item.index" :key="item.index">
                            <template slot="title">
                                <i :class="item.icon"></i>
                                <span>{{ item.title }}</span>
                            </template>
                            <el-menu-item
                                v-for="subItem in item.subs"
                                :key="subItem.index"
                                :index="subItem.url"
                            >
                                <i :class="subItem.icon"></i>{{ subItem.title }}
                            </el-menu-item>
                        </el-submenu>
                    </template>
                    <template v-else>
                        <el-menu-item :index="item.url" :key="item.index">
                            <i :class="item.icon"></i>
                            <span>{{ item.title }}</span>
                        </el-menu-item>
                    </template>
                </template>
            </template>
            <template v-else>
                <template v-for="item in userItems">
                    <template v-if="item.subs">
                        <el-submenu :index="item.index" :key="item.index">
                            <template slot="title">
                                <i :class="item.icon"></i>
                                <span>{{ item.title }}</span>
                            </template>
                            <el-menu-item
                                v-for="subItem in item.subs"
                                :key="subItem.index"
                                :index="subItem.url"
                            >
                                <i :class="subItem.icon"></i>{{ subItem.title }}
                            </el-menu-item>
                        </el-submenu>
                    </template>
                    <template v-else>
                        <el-menu-item :index="item.url" :key="item.index">
                            <i :class="item.icon"></i>
                            <span>{{ item.title }}</span>
                        </el-menu-item>
                    </template>
                </template>
            </template>
        </el-menu>
    </div>
</template>

<script>
    import bus from '../assets/js/event-bus';
    export default {
        data() {
            return {
                collapse: false,
                loginUser: {
                    roleId: 1
                },
                adminItems: [
                    {
                        icon: 'el-icon-s-home',
                        index: '1',
                        url: 'dash-board',
                        title: '系统首页'
                    },
                    {
                        icon: 'el-icon-user',
                        index: '2',
                        title: '用户管理',
                        subs: [
                            {
                                icon: 'el-icon-document',
                                index: '2-1',
                                url: 'user-list',
                                title: '用户列表'
                            }
                        ]
                    },
                    {
                        icon: 'el-icon-school',
                        index: '3',
                        title: '足球场管理',
                        subs: [
                            {
                                icon: 'el-icon-document',
                                index: '3-1',
                                url: 'hall-list',
                                title: '足球场列表'
                            }
                        ]
                    },
                    {
                        icon: 'el-icon-box',
                        index: '4',
                        title: '器材管理',
                        subs: [
                            {
                                icon: 'el-icon-document',
                                index: '4-1',
                                url: 'equipment-list',
                                title: '器材列表'
                            }
                        ]
                    },
                    {
                        icon: 'el-icon-money',
                        index: '5',
                        title: '租借器材管理',
                        subs: [
                            {
                                icon: 'el-icon-document',
                                index: '5-1',
                                url: 'rental-list',
                                title: '租借器材列表'
                            }
                        ]
                    },
                    {
                        icon: 'el-icon-alarm-clock',
                        index: '6',
                        title: '预约足球场管理',
                        subs: [
                            {
                                icon: 'el-icon-document',
                                index: '6-1',
                                url: 'appointment-list',
                                title: '预约足球场列表'
                            }
                        ]
                    },
                    {
                        icon: 'el-icon-chat-line-round',
                        index: '7',
                        title: '公告管理',
                        subs: [
                            {
                                icon: 'el-icon-document',
                                index: '7-1',
                                url: 'announce-list',
                                title: '公告列表'
                            }
                        ]
                    }
                ],
                userItems: [
                    {
                        icon: 'el-icon-s-home',
                        index: '1',
                        url: 'dash-board',
                        title: '首页'
                    },
                    {
                        icon: 'el-icon-school',
                        index: '3',
                        title: '足球场',
                        subs: [
                            {
                                icon: 'el-icon-document',
                                index: '3-1',
                                url: 'hall-list',
                                title: '场地预订'
                            }
                        ]
                    },
                    {
                        icon: 'el-icon-box',
                        index: '4',
                        title: '器材租借',
                        subs: [
                            {
                                icon: 'el-icon-document',
                                index: '4-1',
                                url: 'equipment-list',
                                title: '可用器材'
                            }
                        ]
                    },
                    {
                        icon: 'el-icon-money',
                        index: '5',
                        title: '我的租借',
                        subs: [
                            {
                                icon: 'el-icon-document',
                                index: '5-1',
                                url: 'rental-list',
                                title: '租借记录'
                            }
                        ]
                    },
                    {
                        icon: 'el-icon-alarm-clock',
                        index: '6',
                        title: '我的预约',
                        subs: [
                            {
                                icon: 'el-icon-document',
                                index: '6-1',
                                url: 'appointment-list',
                                title: '预约记录'
                            }
                        ]
                    },
                    {
                        icon: 'el-icon-chat-line-round',
                        index: '7',
                        title: '公告',
                        subs: [
                            {
                                icon: 'el-icon-document',
                                index: '7-1',
                                url: 'announce-list',
                                title: '公告列表'
                            }
                        ]
                    }
                ]
            };
        },
        computed: {
            onRoutes() {
                return this.$route.path.replace('/', '');
            }
        },
        created() {
            this.getLoginUser();
            bus.$on('collapse', msg => {
                this.collapse = msg;
                bus.$emit('collapse-content', msg);
            });
        },
        methods: {
            getLoginUser() {
                this.$ajax.post("/user/getLoginUser", {token: Tool.getLoginUser()}).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        if(resp.data) {
                            this.loginUser = resp.data;
                        }
                    }
                });
            }
        }
    };
</script>

<style scoped>
.sidebar {
    display: block;
    position: absolute;
    left: 0;
    top: 70px;
    bottom: 0;
    overflow-y: scroll;
}

/* 管理员侧边栏样式 */
.sidebar:not(.user-sidebar) {
    background-color: #324157;
}

/* 普通用户侧边栏样式 */
.user-sidebar {
    background: #1677dd;
}

.sidebar::-webkit-scrollbar {
    width: 0;
}

.sidebar-el-menu:not(.el-menu--collapse) {
    width: 250px;
}

.sidebar > ul {
    height: 100%;
}

/* 普通用户菜单图标和文字样式 */
.user-sidebar .el-menu-item i,
.user-sidebar .el-submenu__title i {
    color: rgba(255, 255, 255, 0.95);
    font-size: 18px;
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

/* 普通用户菜单项hover效果 */
.user-sidebar .el-menu-item:hover,
.user-sidebar .el-submenu__title:hover {
    background-color: rgba(255, 255, 255, 0.12) !important;
}

.user-sidebar .el-menu-item:hover i,
.user-sidebar .el-submenu__title:hover i {
    color: #ffffff;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

/* 普通用户子菜单背景 */
.user-sidebar .el-submenu .el-menu {
    background-color: rgba(0, 92, 185, 0.8) !important;
}

/* 普通用户选中菜单项样式 */
.user-sidebar .el-menu-item.is-active {
    background-color: #005cb9 !important;
}

.user-sidebar .el-menu-item.is-active i {
    color: #ffffff;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}
</style>
