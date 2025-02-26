<template>
    <div>
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item>
                    <i class="el-icon-discount"></i> 用户管理
                </el-breadcrumb-item>
                <el-breadcrumb-item>
                    用户列表
                </el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <div class="container">
            <div class="handle-box">
                <el-button
                        v-if="loginUser.roleId === 2"
                        type="primary"
                        size="small"
                        icon="el-icon-plus"
                        class="mr10"
                        @click="openAdd()"
                >添加</el-button>
                <el-select v-model="searchParams.roleId" placeholder="请选择用户角色" size="small" class="handle-select mr10">
                    <el-option label="全部" :value="0"></el-option>
                    <el-option label="普通用户" :value="1"></el-option>
                    <el-option label="管理员" :value="2"></el-option>
                </el-select>
                <el-input v-model="searchParams.username" size="small" placeholder="请输入用户昵称" class="handle-input mr10"></el-input>
                <el-button type="primary" size="small" icon="el-icon-search" @click="search">搜索</el-button>
            </div>
            <el-table size="small" :data="tableData" highlight-current-row v-loading="loading" border element-loading-text="拼命加载中" style="width: 100%;">
                <el-table-column align="center" prop="id" label="用户编号" width="100"></el-table-column>
                <el-table-column align="center" prop="username" label="用户昵称" width="200"></el-table-column>
                <el-table-column align="center" label="用户头像" width="150">
                    <template slot-scope="scope">
                        <el-image
                                style="width: 125px; height: 60px"
                                :src="scope.row.headPic|filterPhoto"
                                :preview-src-list="scope.row.headPic|filterPhotoList">
                        </el-image>
                    </template>
                </el-table-column>
                <el-table-column align="center" prop="phone" label="手机号码" width="200"></el-table-column>
                <el-table-column align="center" label="所属角色" >
                    <template slot-scope="scope">
                        <div>{{scope.row.roleId|filterRole}}</div>
                    </template>
                </el-table-column>
                <el-table-column align="center" label="用户性别" >
                    <template slot-scope="scope">
                        <div>{{scope.row.sex|filterSex}}</div>
                    </template>
                </el-table-column>
                <el-table-column align="center" label="操作" v-if="loginUser.roleId === 2">
                    <template slot-scope="scope">
                        <el-button
                                type="text"
                                class="orange"
                                icon="el-icon-edit"
                                @click="openEdit(scope.row)"
                        >编辑</el-button>
                        <el-button
                                type="text"
                                icon="el-icon-delete"
                                class="red"
                                @click="openRemove(scope.row)"
                        >删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <!-- 分页组件 -->
            <Pagination :params="pageParams" @changePagination="changePagination"></Pagination>
            <!-- 添加修改页面 -->
            <el-dialog :title="title" :visible.sync="dialogFormVisible" :close-on-click-modal="false">
                <el-form :model="form">
                    <el-form-item label="用户昵称" label-width="120px">
                        <el-input v-model="form.username" autocomplete="off"></el-input>
                    </el-form-item>
                    <el-form-item label="用户密码" label-width="120px">
                        <el-input v-model="form.password" autocomplete="off" type="password"></el-input>
                    </el-form-item>
                    <el-form-item label="用户头像" label-width="120px">
                        <input type="file" id="photo-file" style="display:none;" @change="upload">
                        <img :src="form.headPic|filterPhoto" id="photo-view" style="width:100px; height:70px;" />
                        <el-button type="danger" @click="uploadPhoto" style="vertical-align:middle;float:none;margin-top:-50px;margin-left:20px;"><i class="el-icon-upload"></i>上传图片</el-button>
                    </el-form-item>
                    <el-form-item label="手机号码" label-width="120px">
                        <el-input v-model="form.phone" autocomplete="off"></el-input>
                    </el-form-item>
                    <el-form-item label="用户角色" label-width="120px">
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

    </div>
</template>

<script>
    import Pagination from '../../components/Pagination'
    import bus from "../../assets/js/event-bus";
    export default {
        name: 'UserList',
        data() {
            return {
                searchParams: {
                    username: '',
                    roleId: 0,
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
                },
                pageParams: {
                    currentPage: 1,
                    pageSize: 5,
                    total: 0
                },
                title: '',
                tableData: [],
                loading: true, //是显示加载
                loginUser: {}
            }
        },
        created() {
            this.getLoginUser();
            bus.$on('refreshUserList', msg => {
                this.getUserList();
            });
        },
        components: {
            Pagination
        },
        filters: {
            filterRole(val) {
                if(val === 1) {
                    return '普通用户';
                } else if(val === 2) {
                    return '管理员';
                }
            },
            filterSex(val) {
                if(val === 1) {
                    return '男';
                } else if(val === 2) {
                    return '女';
                } else if(val === 3) {
                    return '未知';
                }
            },
            filterPhoto(img){
                return process.env.VUE_APP_URL + "/photo/view?filename=" + img;
            },
            filterPhotoList(img) {
                return [process.env.VUE_APP_URL + "/photo/view?filename=" + img];
            }
        },
        methods: {
            getLoginUser() {
                this.$ajax.post("/user/getLoginUser", {token: Tool.getLoginUser()}).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        if(resp.data) {
                            this.loginUser = resp.data;
                            this.getUserList();
                        } else {
                            this.$message.error(resp.msg);
                            this.$router.push("/login");
                        }
                    }
                });
            },
            getUserList() {
                let data = {
                    searchEntity: this.searchParams,
                    page: this.pageParams.currentPage,
                    size: this.pageParams.pageSize
                };
                this.loading = true;
                this.$ajax.post("/user/list", data).then((response)=>{
                    let resp = response.data;
                    this.loading = false;
                    if(resp.code === 0){
                        this.tableData = resp.data.list;
                        this.pageParams.total = resp.data.total;
                    }else{
                        this.$message.error(resp.msg);
                    }
                });
            },
            changePagination(params) {
                this.pageParams.currentPage = params.currentPage;
                this.pageParams.pageSize = params.pageSize;
                this.getUserList();
            },
            openAdd(){
                this.form = {
                    id: "",
                    username: "",
                    password: "",
                    phone: "",
                    roleId: 1,
                    sex: 3,
                    headPic: "common/no_image.jpg"
                };
                this.dialogFormVisible = true;
                this.title = "添加用户信息";
            },
            openEdit(item){
                this.form = {...item};
                this.title = "修改用户信息";
                this.dialogFormVisible = true;
            },
            saveUser(){
                this.$ajax.post("/user/save", this.form).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        this.dialogFormVisible = false;
                        this.$message.success(resp.msg);
                        this.getUserList();
                        bus.$emit('refreshHeaderUser');
                    }else{
                        this.$message.error(resp.msg);
                    }
                });
            },
            openRemove(item){
                this.$confirm('确定要删除编号为：' + item.id + ' 的记录吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.removeUser(item);
                });
            },
            removeUser(item){
                this.$ajax.post("/user/remove", item).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        this.$message.success(resp.msg);
                        this.getUserList();
                    }else{
                        this.$message.error(resp.msg);
                    }
                });
            },
            // 搜索事件
            search() {
                this.pageParams.currentPage = 1;
                this.pageParams.pageSize = 5;
                this.getUserList();
            },
            uploadPhoto(){
                $("#photo-file").click();
            },
            upload(){
                if($("#photo-file").val() === '')return;
                let config = {
                    headers:{'Content-Type':'multipart/form-data'}
                };
                let formData = new FormData();
                formData.append('photo',document.getElementById('photo-file').files[0]);
                // 普通上传
                this.$ajax.post("/photo/upload", formData, config).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        $("#photo-view").attr('src', process.env.VUE_APP_URL + '/photo/view?filename=' + resp.data);
                        this.form.headPic = resp.data;
                        this.$message.success(resp.msg);
                    }else{
                        this.$message.error(resp.msg);
                    }
                    $("#photo-file").val("");
                });
            }
        }
    };
</script>

<style scoped>
    .handle-box {
        margin-bottom: 20px;
    }

    .handle-select {
        width: 120px;
    }

    .handle-input {
        width: 300px;
        display: inline-block;
    }
    .table {
        width: 100%;
        font-size: 14px;
    }
    .orange {
        color: #ffA500;
    }
    .red {
        color: #ff0000;
    }
    .mr10 {
        margin-right: 10px;
    }
    .table-td-thumb {
        display: block;
        margin: auto;
        width: 40px;
        height: 40px;
    }
</style>
