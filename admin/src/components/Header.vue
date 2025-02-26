<template>
    <div class="header">
        <!-- 折叠按钮 -->
        <div class="collapse-btn" @click="collapseChage">
            <i v-if="!collapse" class="el-icon-s-fold"></i>
            <i v-else class="el-icon-s-unfold"></i>
        </div>
        <div class="logo">{{ loginUser.roleId === 2 ? '足球场预约管理系统' : '足球场预约' }}</div>
        <div class="header-right">
            <div class="header-user-con">
                <!-- 全屏显示 -->
                <div class="btn-fullscreen" @click="handleFullScreen">
                    <el-tooltip effect="dark" :content="fullscreen?`取消全屏`:`全屏`" placement="bottom">
                        <i class="el-icon-rank"></i>
                    </el-tooltip>
                </div>
                <!-- 用户头像 -->
                <div class="user-avator">
                    <img :src="loginUser.headPic|filterPhoto" />
                </div>
                <!-- 用户名下拉菜单 -->
                <el-dropdown class="user-name" trigger="click" @command="handleCommand">
                    <span class="el-dropdown-link">
                        {{loginUser.username}}
                        <i class="el-icon-caret-bottom"></i>
                    </span>
                    <el-dropdown-menu slot="dropdown">
                        <el-dropdown-item command="showUserInfo">个人信息</el-dropdown-item>
                        <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                    </el-dropdown-menu>
                </el-dropdown>
            </div>
        </div>
        <!-- 个人信息修改页面 -->
        <el-dialog title="个人信息修改" :visible.sync="dialogFormVisible" :close-on-click-modal="false">
            <el-form :model="form">
                <el-form-item label="用户昵称" label-width="120px">
                    <el-input v-model="form.username" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="用户密码" label-width="120px">
                    <el-input v-model="form.password" autocomplete="off" type="password"></el-input>
                </el-form-item>
                <el-form-item label="用户头像" label-width="120px">
                    <input type="file" id="headPic-file" style="display:none;" @change="upload">
                    <img :src="form.headPic|filterPhoto" id="headPic-view" style="width:100px; height:70px;" />
                    <el-button type="danger" @click="uploadPhoto" style="vertical-align:middle;float:none;margin-top:-50px;margin-left:20px;"><i class="el-icon-upload"></i>上传图片</el-button>
                </el-form-item>
                <el-form-item label="手机号码" label-width="120px">
                    <el-input v-model="form.phone" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="用户角色" label-width="120px" v-if="loginUser.roleId === 2">
                    <el-select v-model="form.roleId" placeholder="请选择用户角色">
                        <el-option label="普通用户" :value="1"></el-option>
                        <el-option label="管理员" :value="2"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="用户性别" label-width="120px">
                    <el-select v-model="form.sex" placeholder="请选择用户性别">
                        <el-option label="男" :value="1"></el-option>
                        <el-option label="女" :value="2"></el-option>
                        <el-option label="未知" :value="3"></el-option>
                    </el-select>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible = false">取 消</el-button>
                <el-button type="primary" @click="saveUser">确 定</el-button>
            </div>
        </el-dialog>
    </div>

</template>
<script>
    import bus from '../assets/js/event-bus';
    export default {
        data() {
            return {
                collapse: false,
                fullscreen: false,
                loginUser: {
                    headPic: "common/no_image.jpg"
                },
                dialogFormVisible: false,
                form: {
                    id: "",
                    username: "",
                    password: "",
                    phone: "",
                    sex: 3,
                    roleId: 1,
                    headPic: "common/no_image.jpg"
                }
            };
        },
        created() {
            this.getLoginUser();
            bus.$on('refreshHeaderUser', msg => {
                this.getLoginUser();
            });
        },
        filters: {
            filterPhoto(img){
                return process.env.VUE_APP_URL + "/photo/view?filename=" + img;
            }
        },
        methods: {
            async handleCommand(command) {
                if(command === 'logout') {
                    this.logout();
                } else if (command === 'showUserInfo') {
                    await this.getLoginUser();
                    this.dialogFormVisible = true;
                }
            },
            getLoginUser() {
                return this.$ajax.post("/user/getLoginUser", {token: Tool.getLoginUser()}).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        if(resp.data) {
                            this.loginUser = resp.data;
                            this.form = JSON.parse(JSON.stringify(resp.data));
                            $("#headPic-view").attr('src', process.env.VUE_APP_URL + '/photo/view?filename=' + this.form.headPic);
                        } else {
                            this.$message.error(resp.msg);
                            this.$router.push("/login");
                        }
                    }
                });
            },
            uploadPhoto(){
                $("#headPic-file").click();
            },
            upload(){
                if($("#headPic-file").val() === '')return;
                let config = {
                    headers:{'Content-Type':'multipart/form-data'}
                };
                let formData = new FormData();
                formData.append('photo',document.getElementById('headPic-file').files[0]);
                // 普通上传
                this.$ajax.post("/photo/upload", formData, config).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        $("#headPic-view").attr('src', process.env.VUE_APP_URL + '/photo/view?filename=' + resp.data);
                        this.form.headPic = resp.data;
                        this.$message.success(resp.msg);
                    }else{
                        this.$message.error(resp.msg);
                    }
                    $("#headPic-file").val("");
                });
            },
            saveUser(){
                this.$ajax.post("/user/save", this.form).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        this.dialogFormVisible = false;
                        this.$message.success(resp.msg);
                        this.getLoginUser();
                        bus.$emit('refreshUserList');
                    }else{
                        this.$message.error(resp.msg);
                    }
                });
            },
            logout() {
                this.$ajax.post("/user/logout", {token: Tool.getLoginUser()}).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        SessionStorage.remove(SESSION_KEY_LOGIN_USER);
                        this.$message.success(resp.msg);
                        this.$router.push("/login");
                    }
                });
            },
            // 侧边栏折叠
            collapseChage() {
                this.collapse = !this.collapse;
                bus.$emit('collapse', this.collapse);
            },
            // 全屏事件
            handleFullScreen() {
                let element = document.documentElement;
                if (this.fullscreen) {
                    if (document.exitFullscreen) {
                        document.exitFullscreen();
                    } else if (document.webkitCancelFullScreen) {
                        document.webkitCancelFullScreen();
                    } else if (document.mozCancelFullScreen) {
                        document.mozCancelFullScreen();
                    } else if (document.msExitFullscreen) {
                        document.msExitFullscreen();
                    }
                } else {
                    if (element.requestFullscreen) {
                        element.requestFullscreen();
                    } else if (element.webkitRequestFullScreen) {
                        element.webkitRequestFullScreen();
                    } else if (element.mozRequestFullScreen) {
                        element.mozRequestFullScreen();
                    } else if (element.msRequestFullscreen) {
                        // IE11
                        element.msRequestFullscreen();
                    }
                }
                this.fullscreen = !this.fullscreen;
            }
        }
    };
</script>
<style scoped>
    .header {
        position: relative;
        box-sizing: border-box;
        width: 100%;
        height: 70px;
        font-size: 22px;
        color: #fff;
    }
    .collapse-btn {
        float: left;
        padding: 0 21px;
        cursor: pointer;
        line-height: 70px;
    }
    .header .logo {
        float: left;
        width: 250px;
        line-height: 70px;
    }
    .header-right {
        float: right;
        padding-right: 50px;
    }
    .header-user-con {
        display: flex;
        height: 70px;
        align-items: center;
    }
    .btn-fullscreen {
        transform: rotate(45deg);
        margin-right: 5px;
        font-size: 24px;
    }
    .btn-bell,
    .btn-fullscreen {
        position: relative;
        width: 30px;
        height: 30px;
        text-align: center;
        border-radius: 15px;
        cursor: pointer;
    }
    .btn-bell-badge {
        position: absolute;
        right: 0;
        top: -2px;
        width: 8px;
        height: 8px;
        border-radius: 4px;
        background: #f56c6c;
        color: #fff;
    }
    .btn-bell .el-icon-bell {
        color: #fff;
    }
    .user-name {
        margin-left: 10px;
    }
    .user-avator {
        margin-left: 20px;
    }
    .user-avator img {
        display: block;
        width: 40px;
        height: 40px;
        border-radius: 50%;
    }
    .el-dropdown-link {
        color: #fff;
        cursor: pointer;
    }
    .el-dropdown-menu__item {
        text-align: center;
    }
</style>
