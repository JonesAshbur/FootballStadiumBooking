<template>
    <div class="tags" v-if="showTags">
        <ul>
            <li class="tags-li" v-for="(item,index) in tagsList" :class="{'active': isActive(item.path)}" :key="index">
                <router-link :to="item.path" class="tags-li-title">
                    {{item.title}}
                </router-link>
                <span class="tags-li-icon" @click="closeTags(index)" v-if="!isFirstPage(item.path)">
                    <i class="el-icon-close"></i>
                </span>
            </li>
        </ul>
        <div class="tags-close-box">
            <el-dropdown @command="handleTags">
                <el-button type="text">
                    标签选项 <i class="el-icon-arrow-down el-icon--right"></i>
                </el-button>
                <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item command="closeOthers">
                        <i class="el-icon-circle-close"></i>
                        关闭其他
                    </el-dropdown-item>
                    <el-dropdown-item command="closeAll">
                        <i class="el-icon-close"></i>
                        关闭所有
                    </el-dropdown-item>
                </el-dropdown-menu>
            </el-dropdown>
        </div>
    </div>
</template>

<script>
    import bus from '../assets/js/event-bus';
    export default {
        data() {
            return {
                tagsList: []
            }
        },
        methods: {
            isActive(path) {
                return path === this.$route.fullPath;
            },
            isFirstPage(path) {
                // 检查是否是首页或仪表盘页面
                return path === '/' || path === '/dash-board';
            },
            // 关闭单个标签
            closeTags(index) {
                const delItem = this.tagsList.splice(index, 1)[0];
                const item = this.tagsList[index] ? this.tagsList[index] : this.tagsList[index - 1];
                if (item) {
                    delItem.path === this.$route.fullPath && this.$router.push(item.path);
                } else {
                    this.$router.push('/dash-board');
                }
            },
            // 关闭全部标签
            closeAll(){
                this.tagsList = [];
                this.$router.push('/dash-board');
            },
            // 关闭其他标签
            closeOther(){
                const curItem = this.tagsList.filter(item => {
                    return item.path === this.$route.fullPath || this.isFirstPage(item.path);
                })
                this.tagsList = curItem;
            },
            // 设置标签
            setTags(route){
                const isExist = this.tagsList.some(item => {
                    return item.path === route.fullPath;
                })
                if(!isExist){
                    if(this.tagsList.length >= 8){
                        this.tagsList.shift();
                    }
                    this.tagsList.push({
                        title: route.meta.title,
                        path: route.fullPath,
                        name: route.matched[1]?.components.default.name
                    })
                }
                bus.$emit('tags', this.tagsList);
            },
            handleTags(command){
                if (command === 'closeOthers') {
                    this.closeOther();
                } else if (command === 'closeAll') {
                    this.closeAll();
                }
            }
        },
        computed: {
            showTags() {
                return this.tagsList.length > 0;
            }
        },
        watch:{
            $route(newValue, oldValue){
                this.setTags(newValue);
            }
        },
        created(){
            this.setTags(this.$route);
            // 监听关闭当前页面的标签页
            bus.$on('close_current_tags', () => {
                for (let i = 0, len = this.tagsList.length; i < len; i++) {
                    const item = this.tagsList[i];
                    if(item.path === this.$route.fullPath){
                        if(i < len - 1){
                            this.$router.push(this.tagsList[i+1].path);
                        }else if(i > 0){
                            this.$router.push(this.tagsList[i-1].path);
                        }else{
                            this.$router.push('/dash-board');
                        }
                        this.tagsList.splice(i, 1);
                        break;
                    }
                }
            })
        }
    }

</script>


<style>
    .tags {
        position: relative;
        height: 26px;
        overflow: hidden;
        background: #fff;
        padding-right: 120px;
        box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
    }

    .tags ul {
        box-sizing: border-box;
        width: 100%;
        height: 100%;
        padding: 0 8px;
    }

    .tags-li {
        float: left;
        margin: 2px 4px 2px 2px;
        border-radius: 3px;
        font-size: 12px;
        overflow: hidden;
        cursor: pointer;
        height: 20px;
        line-height: 20px;
        border: 1px solid #e9eaec;
        background: #fff;
        padding: 0 4px 0 8px;
        vertical-align: middle;
        color: #666;
        transition: all .2s ease-in-out;
    }

    .tags-li:not(.active):hover {
        background: #f8f8f8;
        border-color: #1890ff;
        color: #1890ff;
    }

    .tags-li.active {
        color: #fff;
        background: #1890ff;
        border-color: #1890ff;
    }

    .tags-li-title {
        float: left;
        max-width: 80px;
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
        margin-right: 4px;
        color: inherit;
        text-decoration: none;
    }

    .tags-li-icon {
        display: inline-block;
        width: 16px;
        height: 16px;
        line-height: 16px;
        text-align: center;
        border-radius: 50%;
        margin-left: 2px;
        transition: all .2s ease;
    }

    .tags-li:not(.active) .tags-li-icon:hover {
        background-color: #ff4d4f;
        color: #fff;
    }

    .tags-li.active .tags-li-icon {
        width: 16px;
        height: 16px;
        line-height: 16px;
        border-radius: 50%;
        background: rgba(255, 255, 255, 0.2);
    }

    .tags-li.active .tags-li-icon:hover {
        background: rgba(255, 255, 255, 0.3);
    }

    .tags-close-box {
        position: absolute;
        right: 0;
        top: 0;
        box-sizing: border-box;
        padding-top: 1px;
        text-align: center;
        width: 110px;
        height: 26px;
        background: #fff;
        box-shadow: -2px 0 8px rgba(0, 0, 0, 0.06);
        z-index: 10;
    }

    .tags-close-box .el-dropdown {
        display: block;
        height: 26px;
        line-height: 26px;
    }

    .tags-close-box .el-button--text {
        font-size: 12px;
        color: #666;
        padding: 0 8px;
    }

    .tags-close-box .el-button--text:hover {
        color: #1890ff;
        background: transparent;
    }

    .el-dropdown-menu__item {
        padding: 5px 16px;
        font-size: 12px;
        line-height: 22px;
        color: #666;
        cursor: pointer;
        outline: none;
        display: flex;
        align-items: center;
        transition: all .2s;
    }

    .el-dropdown-menu__item:hover {
        background-color: #f5f7fa;
        color: #1890ff;
    }

    .el-dropdown-menu__item i {
        margin-right: 5px;
    }

</style>
