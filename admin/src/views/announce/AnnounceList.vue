<template>
    <div>
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item>
                    <i class="el-icon-discount"></i> 公告管理
                </el-breadcrumb-item>
                <el-breadcrumb-item>
                    公告列表
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
                <el-input v-model="searchParams.content" size="small" placeholder="请输入公告内容" class="handle-input mr10"></el-input>
                <el-button type="primary" size="small" icon="el-icon-search" @click="search">搜索</el-button>
            </div>
            <el-table size="small" :data="tableData" highlight-current-row v-loading="loading" border element-loading-text="拼命加载中" style="width: 100%;">
                <el-table-column align="center" prop="id" label="公告编号" width="100"></el-table-column>
                <el-table-column align="center" prop="content" label="公告内容" width="500"></el-table-column>
                <el-table-column align="center" label="公告所属用户" width="150">
                    <template slot-scope="scope">
                        <div>{{scope.row.userDTO.username}}</div>
                    </template>
                </el-table-column>
                <el-table-column align="center" prop="createTime" label="创建时间"></el-table-column>
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
                    <el-form-item label="公告内容" label-width="120px">
                        <el-input type="textarea" :autosize="{ minRows: 3, maxRows: 5}" v-model="form.content" autocomplete="off"></el-input>
                    </el-form-item>
                </el-form>
                <div slot="footer" class="dialog-footer">
                    <el-button @click="dialogFormVisible = false">取 消</el-button>
                    <el-button type="primary" @click="saveAnnounce">确 定</el-button>
                </div>
            </el-dialog>
        </div>

    </div>
</template>

<script>
    import Pagination from '../../components/Pagination'
    export default {
        name: 'AnnounceList',
        data() {
            return {
                searchParams: {
                    content: ''
                },
                dialogFormVisible: false,
                form: {
                    id: "",
                    content: ""
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
        },
        components: {
            Pagination
        },
        methods: {
            getLoginUser() {
                this.$ajax.post("/user/getLoginUser", {token: Tool.getLoginUser()}).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        if(resp.data) {
                            this.loginUser = resp.data;
                            this.getAnnounceList();
                        } else {
                            this.$message.error(resp.msg);
                            this.$router.push("/login");
                        }
                    }
                });
            },
            getAnnounceList() {
                let data = {
                    searchEntity: this.searchParams,
                    page: this.pageParams.currentPage,
                    size: this.pageParams.pageSize
                };
                this.loading = true;
                this.$ajax.post("/announce/list", data).then((response)=>{
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
                this.getAnnounceList();
            },
            openAdd(){
                this.form = {
                    id: "",
                    content: ""
                };
                this.dialogFormVisible = true;
                this.title = "添加公告信息";
            },
            openEdit(item){
                this.form = {...item};
                this.title = "修改公告信息";
                this.dialogFormVisible = true;
            },
            saveAnnounce(){
                this.$ajax.post("/announce/save", {...this.form, userId: this.loginUser.id}).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        this.dialogFormVisible = false;
                        this.$message.success(resp.msg);
                        this.getAnnounceList();
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
                    this.removeAnnounce(item);
                });
            },
            removeAnnounce(item){
                this.$ajax.post("/announce/remove", item).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        this.$message.success(resp.msg);
                        this.getAnnounceList();
                    }else{
                        this.$message.error(resp.msg);
                    }
                });
            },
            // 搜索事件
            search() {
                this.pageParams.currentPage = 1;
                this.pageParams.pageSize = 5;
                this.getAnnounceList();
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
