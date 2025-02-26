<template>
    <div>
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item>
                    <i class="el-icon-discount"></i> 预约足球场管理
                </el-breadcrumb-item>
                <el-breadcrumb-item>
                    预约足球场列表
                </el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <div class="container">
            <div class="handle-box">
                <el-input v-model="searchParams.id" size="small" placeholder="请输入预约编号" class="handle-input mr10"></el-input>
                <el-button type="primary" size="small" icon="el-icon-search" @click="search">搜索</el-button>
            </div>
            <el-table size="small" :data="tableData" highlight-current-row v-loading="loading" border element-loading-text="拼命加载中" style="width: 100%;">
                <el-table-column align="center" prop="id" label="预约编号" width="100"></el-table-column>
                <el-table-column align="center" prop="startTime" label="预约足球场开始时间" width="150"></el-table-column>
                <el-table-column align="center" prop="endTime" label="预约足球场结束时间" width="150"></el-table-column>
                <el-table-column align="center" prop="hallName" label="足球场名称" width="200"></el-table-column>
                <el-table-column align="center" label="足球场图片" width="150">
                    <template slot-scope="scope">
                        <el-image
                                style="width: 125px; height: 60px"
                                :src="scope.row.hallPhoto|filterPhoto"
                                :preview-src-list="scope.row.hallPhoto|filterPhotoList">
                        </el-image>
                    </template>
                </el-table-column>
                <el-table-column align="center" prop="fee" label="预约足球场费用" width="150"></el-table-column>
                <el-table-column align="center" prop="feeRule" label="收费标准" width="150"></el-table-column>
                <el-table-column align="center" label="租借足球场所属用户" width="150">
                    <template slot-scope="scope">
                        <div>{{scope.row.userDTO.username}}</div>
                    </template>
                </el-table-column>
                <el-table-column align="center" label="预约状态" width="150">
                    <template slot-scope="scope">
                        <div v-if="scope.row.state === 1" style="color: gray">待审核</div>
                        <div v-if="scope.row.state === 2" style="color: green">审核通过</div>
                        <div v-if="scope.row.state === 3" style="color: red">审核不通过</div>
                        <div v-if="scope.row.state === 4" style="color: orange">使用中</div>
                        <div v-if="scope.row.state === 5" style="color: green">已完成</div>
                        <div v-if="scope.row.state === 6" style="color: gray">已取消</div>
                    </template>
                </el-table-column>
                <el-table-column align="center" prop="remark" label="审核备注" width="150"></el-table-column>
                <el-table-column align="center" prop="createTime" label="创建时间" width="150"></el-table-column>

                <el-table-column align="center" label="操作" width="150" fixed="right">
                    <template slot-scope="scope">
                        <el-button
                                v-if="scope.row.state === 1 && loginUser.roleId === 2"
                                type="text"
                                class="orange"
                                icon="el-icon-edit"
                                @click="openEdit(scope.row)"
                        >审核</el-button>
                        <el-button
                                v-if="scope.row.state === 1 || scope.row.state === 2"
                                type="text"
                                class="gray"
                                icon="el-icon-edit"
                                @click="cancelAppointment(scope.row)"
                        >取消</el-button>
                        <el-button
                                v-if="loginUser.roleId === 2"
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
            <!-- 审核预约页面 -->
            <el-dialog title="审核预约信息" :visible.sync="dialogFormVisible" :close-on-click-modal="false">
                <el-form :model="form">
                    <el-form-item label="预约编号：" label-width="120px">
                        <div v-text="form.id"></div>
                    </el-form-item>
                    <el-form-item label="预约状态" label-width="120px">
                        <el-select v-model="form.state" placeholder="请选择预约状态">
                            <el-option label="待审核" :value="1"></el-option>
                            <el-option label="审核通过" :value="2"></el-option>
                            <el-option label="审核不通过" :value="3"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="审核备注：" label-width="120px">
                        <el-input type="textarea" :autosize="{ minRows: 2, maxRows: 4}" v-model="form.remark" autocomplete="off"></el-input>
                    </el-form-item>
                </el-form>
                <div slot="footer" class="dialog-footer">
                    <el-button @click="dialogFormVisible = false">取 消</el-button>
                    <el-button type="primary" @click="saveAppointment">确 定</el-button>
                </div>
            </el-dialog>
        </div>

    </div>
</template>

<script>
    import Pagination from '../../components/Pagination'
    export default {
        name: 'AppointmentList',
        data() {
            return {
                searchParams: {
                    id: ''
                },
                dialogFormVisible: false,
                form: {
                    id: "",
                    state: 1,
                    remark: ''
                },
                pageParams: {
                    currentPage: 1,
                    pageSize: 5,
                    total: 0
                },
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
        filters: {
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
                            this.getAppointmentList();
                        } else {
                            this.$message.error(resp.msg);
                            this.$router.push("/login");
                        }
                    }
                });
            },
            cancelAppointment(item) {
                this.$confirm('确定要取消编号为：' + item.id + ' 的记录吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$ajax.post("/appointment/save", {...item, state: 6}).then((response)=>{
                        let resp = response.data;
                        if(resp.code === 0){
                            this.$message.success("取消成功！");
                            this.getAppointmentList();
                        }else{
                            this.$message.error(resp.msg);
                        }
                    });
                });
            },
            getAppointmentList() {
                let data = {
                    searchEntity: this.searchParams,
                    page: this.pageParams.currentPage,
                    size: this.pageParams.pageSize
                };
                this.loading = true;
                this.$ajax.post("/appointment/list", data).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        this.tableData = resp.data.list;
                        this.pageParams.total = resp.data.total;
                    }else{
                        this.$message.error(resp.msg);
                    }
                    this.loading = false;
                });
            },
            changePagination(params) {
                this.pageParams.currentPage = params.currentPage;
                this.pageParams.pageSize = params.pageSize;
                this.getAppointmentList();
            },
            openEdit(item){
                this.form = {...item};
                this.dialogFormVisible = true;
            },
            saveAppointment(){
                this.$ajax.post("/appointment/save", this.form).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        this.dialogFormVisible = false;
                        this.$message.success(resp.msg);
                        this.getAppointmentList();
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
                    this.removeAppointment(item);
                });
            },
            removeAppointment(item){
                this.$ajax.post("/appointment/remove", item).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        this.$message.success(resp.msg);
                        this.getAppointmentList();
                    }else{
                        this.$message.error(resp.msg);
                    }
                });
            },
            // 搜索事件
            search() {
                this.pageParams.currentPage = 1;
                this.pageParams.pageSize = 5;
                this.getAppointmentList();
            }
        }
    };
</script>

<style scoped>
    .handle-box {
        margin-bottom: 20px;
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
