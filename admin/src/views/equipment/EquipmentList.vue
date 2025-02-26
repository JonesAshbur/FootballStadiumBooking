<template>
    <div>
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item>
                    <i class="el-icon-discount"></i> 器材管理
                </el-breadcrumb-item>
                <el-breadcrumb-item>
                    器材列表
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
                <el-input v-model="searchParams.name" size="small" placeholder="请输入器材名称" class="handle-input mr10"></el-input>
                <el-button type="primary" size="small" icon="el-icon-search" @click="search">搜索</el-button>
            </div>
            <el-table size="small" :data="tableData" highlight-current-row v-loading="loading" border element-loading-text="拼命加载中" style="width: 100%;">
                <el-table-column align="center" prop="id" label="器材编号" width="100"></el-table-column>
                <el-table-column align="center" prop="name" label="器材名称" width="200"></el-table-column>
                <el-table-column align="center" prop="brand" label="器材品牌" width="200"></el-table-column>
                <el-table-column align="center" label="器材图片" width="150">
                    <template slot-scope="scope">
                        <el-image
                                style="width: 125px; height: 60px"
                                :src="scope.row.photo|filterPhoto"
                                :preview-src-list="scope.row.photo|filterPhotoList">
                        </el-image>
                    </template>
                </el-table-column>
                <el-table-column align="center" prop="fee" label="租借费用(元/时)" width="150"></el-table-column>
                <el-table-column align="center" label="器材数量" width="150">
                    <template slot-scope="scope">
                        <div v-if="scope.row.num === 0" style="color: red">{{scope.row.num}}</div>
                        <div v-else style="color: green">{{scope.row.num}}</div>
                    </template>
                </el-table-column>
                <el-table-column align="center" label="器材状态" width="150">
                    <template slot-scope="scope">
                        <div v-if="scope.row.state === 1" style="color: green">已上架</div>
                        <div v-if="scope.row.state === 2" style="color: red">已下架</div>
                    </template>
                </el-table-column>
                <el-table-column align="center" prop="info" label="器材简介" width="250"></el-table-column>
                <el-table-column align="center" label="操作" width="200" fixed="right">
                    <template slot-scope="scope">
                        <el-button
                                v-if="scope.row.state === 1"
                                type="text"
                                class="blue"
                                icon="el-icon-money"
                                @click="openRental(scope.row)"
                        >租借</el-button>
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
                    <el-form-item label="器材名称" label-width="120px">
                        <el-input v-model="form.name" autocomplete="off"></el-input>
                    </el-form-item>
                    <el-form-item label="器材品牌" label-width="120px">
                        <el-input v-model="form.brand" autocomplete="off"></el-input>
                    </el-form-item>
                    <el-form-item label="器材图片" label-width="120px">
                        <input type="file" id="photo-file" style="display:none;" @change="upload">
                        <img :src="form.photo|filterPhoto" id="photo-view" style="width:100px; height:70px;" />
                        <el-button type="danger" @click="uploadPhoto" style="vertical-align:middle;float:none;margin-top:-50px;margin-left:20px;"><i class="el-icon-upload"></i>上传图片</el-button>
                    </el-form-item>
                    <el-form-item label="租借费用(元/时)" label-width="120px">
                        <el-input-number v-model="form.fee" :precision="2" :min="0" :max="99999999.99"></el-input-number>
                    </el-form-item>
                    <el-form-item label="器材数量" label-width="120px">
                        <el-input-number v-model="form.num" :precision="2" :min="0" :max="99999999"></el-input-number>
                    </el-form-item>
                    <el-form-item label="器材状态" label-width="120px">
                        <el-select v-model="form.state">
                            <el-option label="已上架" :value="1"></el-option>
                            <el-option label="已下架" :value="2"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="器材简介" label-width="120px">
                        <el-input type="textarea" :autosize="{ minRows: 2, maxRows: 4}" v-model="form.info" autocomplete="off"></el-input>
                    </el-form-item>
                </el-form>
                <div slot="footer" class="dialog-footer">
                    <el-button @click="dialogFormVisible = false">取 消</el-button>
                    <el-button type="primary" @click="saveEquipment">确 定</el-button>
                </div>
            </el-dialog>

            <!-- 租借器材页面 -->
            <el-dialog title="租借器材" :visible.sync="rentalDialogFormVisible" :close-on-click-modal="false">
                <el-form :model="rentalForm">
                    <el-form-item label="器材名称：" label-width="120px">
                        <div v-text="rentalForm.equipmentName"></div>
                    </el-form-item>
                    <el-form-item label="器材图片：" label-width="120px">
                        <img :src="rentalForm.equipmentPhoto|filterPhoto" style="width:100px; height:70px;" />
                    </el-form-item>
                    <el-form-item label="收费标准：" label-width="120px">
                        <div v-text="rentalForm.feeRule"></div>
                    </el-form-item>
                    <el-form-item label="租借开始时间：" label-width="120px">
                        <el-date-picker
                                :clearable="false"
                                :picker-options="pickerOptions"
                                v-model="rentalForm.startTime"
                                type="datetime"
                                @change="changeStartTime"
                                placeholder="选择日期时间">
                        </el-date-picker>
                    </el-form-item>
                    <el-form-item label="租借结束时间：" label-width="120px">
                        <el-date-picker
                                :clearable="false"
                                :picker-options="pickerOptions"
                                v-model="rentalForm.endTime"
                                @change="changeEndTime"
                                type="datetime"
                                placeholder="选择日期时间">
                        </el-date-picker>
                    </el-form-item>
                    <el-form-item label="租借器材数量：" label-width="120px">
                        <el-input-number @change="changeNum" v-model="rentalForm.num" :precision="2" :min="1" :max="99999999"></el-input-number>
                    </el-form-item>
                    <el-form-item label="租借器材费用：" label-width="120px">
                        <div>
                            {{rentalForm.fee}} 元
                            <el-tooltip effect="dark" content="不足1小时按1小时收费哦！" placement="top">
                                <i class="el-icon-question" style="margin-left: 10px"></i>
                            </el-tooltip>
                        </div>
                    </el-form-item>
                </el-form>
                <div slot="footer" class="dialog-footer">
                    <el-button @click="rentalDialogFormVisible = false">取 消</el-button>
                    <el-button type="primary" @click="saveRental">支付并提交</el-button>
                </div>
            </el-dialog>
        </div>

    </div>
</template>

<script>
    import Pagination from '../../components/Pagination'
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
        name: 'EquipmentList',
        data() {
            return {
                searchParams: {
                    name: ''
                },
                dialogFormVisible: false,
                rentalDialogFormVisible: false,
                form: {
                    id: "",
                    name: "",
                    fee: 0,
                    brand: "",
                    info: "",
                    num: 0,
                    state: 1,
                    photo: "common/no_image.jpg"
                },
                rentalForm: {
                    startTime: new Date(),
                    endTime: new Date(new Date().getTime() + 1000),
                    equipmentName: '',
                    equipmentId: '',
                    equipmentPhoto: "common/no_image.jpg",
                    num: 1,
                    feeRule: '',
                    fee: 0,
                    equipmentFee: 0
                },
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
                            this.getEquipmentList();
                        } else {
                            this.$message.error(resp.msg);
                            this.$router.push("/login");
                        }
                    }
                });
            },
            changeEndTime(time) {
                if(time.getTime() <= this.rentalForm.startTime.getTime()) {
                    this.$message.warning("结束时间必须大于开始时间！");
                    this.rentalForm.endTime = new Date(this.rentalForm.startTime.getTime() + 1000);
                    return;
                }
                let diffTime = (time - this.rentalForm.startTime); //计算时间差,并把毫秒转换成秒
                let hours = parseInt(diffTime / (60 * 60 * 1000));
                if(parseInt(diffTime % (60 * 60 * 1000)) !== 0) {
                    hours += 1;
                }
                this.rentalForm.fee = (hours * this.rentalForm.num * Number(this.rentalForm.equipmentFee)).toFixed(2);
            },
            changeStartTime(time) {
                if(time.getTime() >= this.rentalForm.endTime.getTime()) {
                    this.$message.warning("开始时间必须小于结束时间！");
                    this.rentalForm.startTime = new Date(this.rentalForm.endTime.getTime() - 1000);
                    return;
                }
                let diffTime = (this.rentalForm.endTime - time); //计算时间差,并把毫秒转换成秒
                let hours = parseInt(diffTime / (60 * 60 * 1000));
                if(parseInt(diffTime % (60 * 60 * 1000)) !== 0) {
                    hours += 1;
                }
                this.rentalForm.fee = (hours * this.rentalForm.num * Number(this.rentalForm.equipmentFee)).toFixed(2);
            },
            changeNum(num) {
                let diffTime = (this.rentalForm.endTime - this.rentalForm.startTime); //计算时间差,并把毫秒转换成秒
                let hours = parseInt(diffTime / (60 * 60 * 1000));
                if(parseInt(diffTime % (60 * 60 * 1000)) !== 0) {
                    hours += 1;
                }
                this.rentalForm.fee = (hours * num * Number(this.rentalForm.equipmentFee)).toFixed(2);
            },
            saveRental() {
                this.$ajax.post("/rental/save", {
                    ...this.rentalForm,
                    userId: this.loginUser.id,
                    startTime: this.rentalForm.startTime.Format("yyyy-MM-dd hh:mm:ss"),
                    endTime: this.rentalForm.endTime.Format("yyyy-MM-dd hh:mm:ss")
                }).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0) {
                        this.$message.success("租借成功！");
                        this.rentalDialogFormVisible = false;
                        this.getEquipmentList();
                    }else{
                        this.$message.error(resp.msg);
                    }
                });
            },
            openRental(row) {
                this.rentalForm.equipmentPhoto = row.photo;
                this.rentalForm.equipmentName = row.name;
                this.rentalForm.equipmentId = row.id;
                this.rentalForm.feeRule = '单个' + row.fee + '元/时';
                this.rentalForm.equipmentFee = row.fee;
                this.rentalForm.fee = row.fee.toFixed(2);
                this.rentalForm.startTime = new Date();
                this.rentalForm.endTime = new Date(new Date().getTime() + 1000);
                this.rentalForm.num = 1;
                this.rentalDialogFormVisible = true;
            },
            getEquipmentList() {
                let data = {
                    searchEntity: this.searchParams,
                    page: this.pageParams.currentPage,
                    size: this.pageParams.pageSize
                };
                this.loading = true;
                this.$ajax.post("/equipment/list", data).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0) {
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
                this.getEquipmentList();
            },
            openAdd(){
                this.form = {
                    id: "",
                    name: "",
                    brand: "",
                    info: "",
                    state: 1,
                    fee: 0,
                    num: 0,
                    photo: "common/no_image.jpg"
                };
                this.dialogFormVisible = true;
                this.title = "添加器材信息";
            },
            openEdit(item){
                this.form = {...item};
                this.title = "修改器材信息";
                this.dialogFormVisible = true;
            },
            saveEquipment(){
                this.$ajax.post("/equipment/save", this.form).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        this.dialogFormVisible = false;
                        this.$message.success(resp.msg);
                        this.getEquipmentList();
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
                    this.removeEquipment(item);
                });
            },
            removeEquipment(item){
                this.$ajax.post("/equipment/remove", item).then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        this.$message.success(resp.msg);
                        this.getEquipmentList();
                    }else{
                        this.$message.error(resp.msg);
                    }
                });
            },
            // 搜索事件
            search() {
                this.pageParams.currentPage = 1;
                this.pageParams.pageSize = 5;
                this.getEquipmentList();
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
    .blue {
        color: #66b1ff;
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
