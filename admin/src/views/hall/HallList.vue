<template>
    <div>
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item>
                    <i class="el-icon-discount"></i> 足球场管理
                </el-breadcrumb-item>
                <el-breadcrumb-item>
                    足球场列表
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
                <el-input v-model="searchParams.name" size="small" placeholder="请输入足球场名称" class="handle-input mr10"></el-input>
                <el-button type="primary" size="small" icon="el-icon-search" @click="search">搜索</el-button>
            </div>
            <el-table size="small" :data="tableData" highlight-current-row v-loading="loading" border element-loading-text="拼命加载中" style="width: 100%;">
                <el-table-column align="center" prop="id" label="足球场编号" width="100"></el-table-column>
                <el-table-column align="center" prop="name" label="足球场名称" width="200"></el-table-column>
                <el-table-column align="center" label="足球场图片" width="150">
                    <template slot-scope="scope">
                        <el-image
                                style="width: 125px; height: 60px"
                                :src="scope.row.photo|filterPhoto"
                                :preview-src-list="scope.row.photo|filterPhotoList">
                        </el-image>
                    </template>
                </el-table-column>
                <el-table-column align="center" prop="fee" label="预约费用(元/时)" width="150"></el-table-column>
                <el-table-column align="center" label="足球场状态" width="150">
                    <template slot-scope="scope">
                        <div v-if="scope.row.state === 1" style="color: green">正常</div>
                        <div v-if="scope.row.state === 2" style="color: red">维护中</div>
                    </template>
                </el-table-column>
                <el-table-column align="center" prop="location" label="足球场位置" width="200"></el-table-column>
                <el-table-column align="center" prop="info" label="足球场简介" width="250"></el-table-column>
                <el-table-column align="center" label="操作" width="150" fixed="right">
                    <template slot-scope="scope">
                        <el-button
                                v-if="scope.row.state === 1"
                                type="text"
                                class="blue"
                                icon="el-icon-money"
                                @click="openAppointment(scope.row)"
                        >预约</el-button>
                        <el-button
                                v-if="loginUser.roleId === 2"
                                type="text"
                                class="orange"
                                icon="el-icon-edit"
                                @click="openEdit(scope.row)"
                        >编辑</el-button>
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
            <!-- 添加修改页面 -->
            <el-dialog :title="title" :visible.sync="dialogFormVisible" :close-on-click-modal="false">
                <el-form :model="form">
                    <el-form-item label="足球场名称" label-width="120px">
                        <el-input v-model="form.name" autocomplete="off"></el-input>
                    </el-form-item>
                    <el-form-item label="足球场位置" label-width="120px">
                        <el-input v-model="form.location" autocomplete="off"></el-input>
                    </el-form-item>
                    <el-form-item label="足球场图片" label-width="120px">
                        <input type="file" id="photo-file" style="display:none;" @change="upload">
                        <img :src="form.photo|filterPhoto" id="photo-view" style="width:100px; height:70px;" />
                        <el-button type="danger" @click="uploadPhoto" style="vertical-align:middle;float:none;margin-top:-50px;margin-left:20px;"><i class="el-icon-upload"></i>上传图片</el-button>
                    </el-form-item>
                    <el-form-item label="预约费用(元/时)" label-width="120px">
                        <el-input-number v-model="form.fee" :precision="2" :min="0" :max="99999999.99"></el-input-number>
                    </el-form-item>
                    <el-form-item label="足球场状态" label-width="120px">
                        <el-select v-model="form.state">
                            <el-option label="正常" :value="1"></el-option>
                            <el-option label="维护中" :value="2"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="足球场简介" label-width="120px">
                        <el-input type="textarea" :autosize="{ minRows: 2, maxRows: 4}" v-model="form.info" autocomplete="off"></el-input>
                    </el-form-item>
                </el-form>
                <div slot="footer" class="dialog-footer">
                    <el-button @click="dialogFormVisible = false">取 消</el-button>
                    <el-button type="primary" @click="saveHall">确 定</el-button>
                </div>
            </el-dialog>
            <!-- 预约场馆页面 -->
            <el-dialog title="预约足球场" :visible.sync="appointmentDialogFormVisible" :close-on-click-modal="false">
                <div v-loading="loadingDialog">
                    <el-form :model="appointmentForm">
                        <el-form-item label="足球场名称：" label-width="120px">
                            <div v-text="appointmentForm.hallName"></div>
                        </el-form-item>
                        <el-form-item label="足球场图片：" label-width="120px">
                            <img :src="appointmentForm.hallPhoto|filterPhoto" style="width:100px; height:70px;" />
                        </el-form-item>
                        <el-form-item label="收费标准：" label-width="120px">
                            <div v-text="appointmentForm.feeRule"></div>
                        </el-form-item>
                        <el-form-item label="预约日期：" label-width="120px">
                            <el-date-picker
                                    :clearable="false"
                                    :picker-options="pickerOptions"
                                    v-model="appointmentForm.startTime"
                                    type="date"
                                    @change="changeStartTime"
                                    placeholder="选择日期">
                            </el-date-picker>
                        </el-form-item>
                        <el-form-item label="时间段：" label-width="120px">
                            <el-select v-model="appointmentForm.scheduleId" @change="changeSchedule" placeholder="请选择">
                                <el-option
                                        v-for="item in scheduleOptions"
                                        :disabled="item.disabled"
                                        :key="item.value"
                                        :label="item.label"
                                        :value="item.value">
                                </el-option>
                            </el-select>
                        </el-form-item>
                        <el-form-item label="预约场地费用：" label-width="120px">
                            <div>
                                {{appointmentForm.fee}} 元
                            </div>
                        </el-form-item>
                    </el-form>
                    <div slot="footer" class="dialog-footer" style="text-align: right">
                        <el-button @click="appointmentDialogFormVisible = false">取 消</el-button>
                        <el-button type="primary" @click="saveAppointment">支付并提交</el-button>
                    </div>
                </div>

            </el-dialog>
        </div>

    </div>
</template>

<script>
    import Pagination from '../../components/Pagination';
    Date.prototype.Format = function (fmt) {
        var o = {
            "M+": this.getMonth() + 1, //月份
            "d+": this.getDate(), //日
            "h+": this.getHours(), //小时
            "m+": this.getMinutes(), //分
            "s+": this.getSeconds(), //秒
            "q+": Math.floor((this.getMonth() + 3) / 3), //季度
            "S": this.getMilliseconds() //毫秒
        };
        if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
        for (var k in o)
            if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        return fmt;
    };
    export default {
        name: 'HallList',
        data() {
            return {
                searchParams: {
                    name: ''
                },
                scheduleOptions: [
                    {
                        label: "08:00-10:00",
                        value: "08-10",
                        disabled: false
                    },
                    {
                        label: "10:00-12:00",
                        value: "10-12",
                        disabled: false
                    },
                    {
                        label: "12:00-14:00",
                        value: "12-14",
                        disabled: false
                    },
                    {
                        label: "14:00-16:00",
                        value: "14-16",
                        disabled: false
                    },
                    {
                        label: "16:00-18:00",
                        value: "16-18",
                        disabled: false
                    },
                    {
                        label: "18:00-20:00",
                        value: "18-20",
                        disabled: false
                    },
                    {
                        label: "20:00-22:00",
                        value: "20-22",
                        disabled: false
                    }
                ],
                loadingDialog: false,
                dialogFormVisible: false,
                form: {
                    id: "",
                    name: "",
                    fee: 0,
                    location: "",
                    info: "",
                    state: 1,
                    photo: "common/no_image.jpg"
                },
                appointmentForm: {
                    startTime: new Date(),
                    endTime: new Date(),
                    hallName: '',
                    hallId: '',
                    hallPhoto: "common/no_image.jpg",
                    feeRule: '',
                    fee: 0,
                    hallFee: 0,
                    scheduleId: ""
                },
                appointmentDialogFormVisible: false,
                pageParams: {
                    currentPage: 1,
                    pageSize: 5,
                    total: 0
                },
                title: '',
                tableData: [],
                loading: true, //是显示加载
                loginUser: {},
                pickerOptions: {
                    disabledDate(time) {
                        return time.getTime() < Date.now() - 8.64e7;
                    }
                }
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
                            this.getHallList();
                        } else {
                            this.$message.error(resp.msg);
                            this.$router.push("/login");
                        }
                    }
                });
            },
            getAppointedList() {
                return this.$ajax.post("/appointment/list_exist").then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        let appointedList = resp.data.filter(item => item.hallId === this.appointmentForm.hallId);
                        this.scheduleOptions = this.scheduleOptions.map(item => {
                            let timeRange = item.value.split("-");
                            let nowAppointedList = appointedList.filter(e => {
                                let date = e.startTime.substring(0, 10);
                                if(date === this.appointmentForm.startTime.Format("yyyy-MM-dd")) {
                                    return true
                                }
                                return false;
                            }).map(e => e.startTime.substring(11, 13));
                            let isExist = nowAppointedList.filter(
                                e => Number(e) >= Number(timeRange[0]) && Number(e) < Number(timeRange[1])
                            );
                            if(isExist.length > 0) {
                                item.disabled = true;
                            }
                            if(Number(timeRange[0]) <= Number(new Date().Format("hh"))
                                && this.appointmentForm.startTime.Format("yyyy-MM-dd") === new Date().Format("yyyy-MM-dd")) {
                                item.disabled = true;
                            }
                            return item;
                        })
                    }
                });
            },
            async openAppointment(row) {
                this.loadingDialog = true;
                this.scheduleOptions = this.scheduleOptions.map(item => {
                    item.disabled = false;
                    return item;
                });
                this.appointmentForm.scheduleId = "";
                this.appointmentForm.startTime = new Date();
                this.appointmentForm.hallPhoto = row.photo;
                this.appointmentForm.hallName = row.name;
                this.appointmentForm.hallId = row.id;
                this.appointmentForm.feeRule = '单个' + row.fee + '元/时';
                this.appointmentForm.hallFee = row.fee;
                this.appointmentForm.fee = row.fee.toFixed(2);
                await this.getAppointedList();
                this.appointmentDialogFormVisible = true;
                this.loadingDialog = false;
            },
            saveAppointment() {
                if(this.appointmentForm.scheduleId === "") {
                    this.$message.warning("请选择时间段！");
                    return;
                }
                let selectedSchedule = this.appointmentForm.scheduleId?.split("-");
                let date = this.appointmentForm.startTime.Format("yyyy-MM-dd");
                let startTime = date + " " + selectedSchedule[0] + ":00:00";
                let endTime = date + " " + selectedSchedule[1] + ":00:00";
                this.$ajax.post("/appointment/save", {
                    ...this.appointmentForm,
                    userId: this.loginUser.id,
                    startTime: startTime,
                    endTime: endTime
                }).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0) {
                        this.$message.success("预约成功！");
                        this.appointmentDialogFormVisible = false;
                        this.getHallList();
                    }else{
                        this.$message.error(resp.msg);
                    }
                });
            },
            getHallList() {
                let data = {
                    searchEntity: this.searchParams,
                    page: this.pageParams.currentPage,
                    size: this.pageParams.pageSize
                };
                this.loading = true;
                this.$ajax.post("/hall/list", data).then((response)=>{
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
            changeSchedule(schedule) {
                let timeRange = schedule.split("-");
                let date = this.appointmentForm.startTime.Format("yyyy-MM-dd");
                let startTime = date + " " + timeRange[0] + ":00:00";
                let endTime = date + " " + timeRange[1] + ":00:00";
                this.appointmentForm.startTime = new Date(startTime);
                this.appointmentForm.endTime = new Date(endTime);
                let diffTime = (this.appointmentForm.endTime - this.appointmentForm.startTime); //计算时间差,并把毫秒转换成秒
                let hours = parseInt(diffTime / (60 * 60 * 1000));
                if(parseInt(diffTime % (60 * 60 * 1000)) !== 0) {
                    hours += 1;
                }
                this.appointmentForm.fee = (hours * Number(this.appointmentForm.hallFee)).toFixed(2);
            },
            async changeStartTime(time) {
                this.loadingDialog = true;
                this.appointmentForm.scheduleId = "";
                this.scheduleOptions = this.scheduleOptions.map(item => {
                    item.disabled = false;
                    return item;
                });
                await this.getAppointedList();
                this.loadingDialog = false;
            },
            changePagination(params) {
                this.pageParams.currentPage = params.currentPage;
                this.pageParams.pageSize = params.pageSize;
                this.getHallList();
            },
            openAdd(){
                this.form = {
                    id: "",
                    name: "",
                    location: "",
                    info: "",
                    fee: 0,
                    state: 1,
                    photo: "common/no_image.jpg"
                };
                this.dialogFormVisible = true;
                this.title = "添加足球场信息";
            },
            openEdit(item){
                this.form = {...item};
                this.title = "修改足球场信息";
                this.dialogFormVisible = true;
            },
            saveHall(){
                this.$ajax.post("/hall/save", this.form).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        this.dialogFormVisible = false;
                        this.$message.success(resp.msg);
                        this.getHallList();
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
                    this.removeHall(item);
                });
            },
            removeHall(item){
                this.$ajax.post("/hall/remove", item).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        this.$message.success(resp.msg);
                        this.getHallList();
                    }else{
                        this.$message.error(resp.msg);
                    }
                });
            },
            // 搜索事件
            search() {
                this.pageParams.currentPage = 1;
                this.pageParams.pageSize = 5;
                this.getHallList();
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
                        this.form.photo = resp.data;
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
