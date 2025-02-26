<template>
    <div>
        <el-row :gutter="20" style="margin-bottom: 0">
            <el-col :span="8">
                <el-card shadow="hover" class="mgb20" style="height:252px;">
                    <div class="user-info">
                        <img :src="loginUser.headPic|filterPhoto" class="user-avator" alt />
                        <div class="user-info-cont">
                            <div class="user-info-name">{{loginUser.username}}</div>
                            <div>{{loginUser.role|filterRole}}</div>
                        </div>
                    </div>
                    <div class="user-info-list">
                        用户角色：
                        <span>{{loginUser.roleId|filterRole}}</span>
                    </div>
                    <div class="user-info-list">
                        手机号码：
                        <span>{{loginUser.phone}}</span>
                    </div>
                </el-card>
                <el-card shadow="hover" style="height:356px;">
                    <echart :chartData="echartData.pieRatio" style="height: 320px" isChart="pie"></echart>
                </el-card>
            </el-col>
            <el-col :span="16">
                <el-row :gutter="20" class="mgb20">
                    <el-col :span="8">
                        <el-card shadow="hover" :body-style="{padding: '0px'}">
                            <div class="grid-content grid-con-1">
                                <i class="el-icon-user grid-con-icon"></i>
                                <div class="grid-cont-right">
                                    <div class="grid-num" v-text="userTotal"></div>
                                    <div>用户数</div>
                                </div>
                            </div>
                        </el-card>
                    </el-col>
                    <el-col :span="8">
                        <el-card shadow="hover" :body-style="{padding: '0px'}">
                            <div class="grid-content grid-con-2">
                                <i class="el-icon-money grid-con-icon"></i>
                                <div class="grid-cont-right">
                                    <div class="grid-num" v-text="rentalTotal"></div>
                                    <div>租借数</div>
                                </div>
                            </div>
                        </el-card>
                    </el-col>
                    <el-col :span="8">
                        <el-card shadow="hover" :body-style="{padding: '0px'}">
                            <div class="grid-content grid-con-3">
                                <i class="el-icon-alarm-clock grid-con-icon"></i>
                                <div class="grid-cont-right">
                                    <div class="grid-num" v-text="appointmentTotal"></div>
                                    <div>预约数</div>
                                </div>
                            </div>
                        </el-card>
                    </el-col>
                </el-row>
                <el-card shadow="hover" style="height:260px;margin-bottom: 20px">
                    <div slot="header" class="clearfix">
                        <span>最新公告</span>
                        <router-link to="announce-list"><el-button style="float: right; padding: 3px 0" type="text">更多</el-button></router-link>
                    </div>
                    <el-table size="small" :data="tableData" highlight-current-row v-loading="loading" border element-loading-text="拼命加载中" style="width: 100%;">
                        <el-table-column align="center" prop="content" label="公告内容" width="350"></el-table-column>
                        <el-table-column align="center" label="公告所属用户">
                            <template slot-scope="scope">
                                <div>{{scope.row.userDTO.username}}</div>
                            </template>
                        </el-table-column>
                        <el-table-column align="center" prop="createTime" label="创建时间" width="150"></el-table-column>
                    </el-table>
                </el-card>

                <el-card shadow="hover" style="height:225px">
                    <echart :chartData="echartData.lineTotal" isChart="line" style="height: 195px"></echart>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script>
import Echart from "@/components/ECharts.vue";

export default {
    name: 'AdminDashBoard',
    components: {
        Echart
    },
    props: {
        loginUser: {
            type: Object,
            required: true
        }
    },
    data() {
        return {
            loading: false,
            tableData: [],
            searchParams: {
                content: ''
            },
            rentalTotal: 0,
            appointmentTotal: 0,
            userTotal: 0,
            echartData: {
                lineTotal: {
                    xData: [],
                    series: []
                },
                pieRatio: {
                    series: []
                }
            }
        };
    },
    filters: {
        filterPhoto(img){
            return process.env.VUE_APP_URL + "/photo/view?filename=" + img;
        },
        filterRole(val) {
            if(val === 1) {
                return '普通用户';
            } else if(val === 2) {
                return '管理员';
            }
        }
    },
    created() {
        this.loadData();
    },
    methods: {
        loadData() {
            this.getAnnounceList();
            this.getUserTotal();
            this.echartData.lineTotal.xData = [this.getDate(5), this.getDate(4), this.getDate(3), this.getDate(2), this.getDate(1)];
            
            Promise.all([this.getRentalTotal(), this.getAppointmentTotal()])
                .then((results) => {
                    let data = [{value: results[0], name: "租借器材"}, {value: results[1], name: "预约场地"}];
                    this.echartData.pieRatio.series = [
                        {
                            type: 'pie',
                            label: {
                                normal: {
                                    show: false,
                                }
                            },
                            labelLine: {
                                normal: {
                                    show: false,
                                }
                            },
                            data: data
                        }
                    ];
                });

            Promise.all([this.getRentalTotalByDay(), this.getAppointmentTotalByDay()])
                .then((results) => {
                    this.echartData.lineTotal.series = [
                        {
                            name: '租借器材',
                            data: results[0],
                            type: 'line',
                            smooth: true
                        },
                        {
                            name: '预约场地',
                            data: results[1],
                            type: 'line',
                            smooth: true
                        }
                    ];
                });
        },
        getDate(i) {
            let date;
            switch (i) {
                case 1:
                    date = new Date();
                    break;
                case 2:
                    date = new Date(new Date() - 60000*60*24);
                    break;
                case 3:
                    date = new Date(new Date() - 60000*60*24*2);
                    break;
                case 4:
                    date = new Date(new Date() - 60000*60*24*3);
                    break;
                case 5:
                    date = new Date(new Date() - 60000*60*24*4);
                    break;
            }
            let year = date.getFullYear();
            let month = date.getMonth() + 1;
            let strDate = date.getDate();
            if (month >= 1 && month <= 9) {
                month = "0" + month;
            }
            if (strDate >= 0 && strDate <= 9) {
                strDate = "0" + strDate;
            }
            let currentDate = year + "年" + month + "月" + strDate + "日";
            return currentDate;
        },
        getAnnounceList() {
            let data = {
                searchEntity: this.searchParams,
                page: 1,
                size: 3
            };
            this.loading = true;
            this.$ajax.post("/announce/list", data).then((response)=>{
                let resp = response.data;
                this.loading = false;
                if(resp.code === 0){
                    this.tableData = resp.data.list;
                }else{
                    this.$message.error(resp.msg);
                }
            });
        },
        getRentalTotal() {
            return this.$ajax.post("/rental/total", {token: Tool.getLoginUser()}).then((response)=>{
                let resp = response.data;
                if(resp.code === 0){
                    this.rentalTotal = resp.data;
                    return Promise.resolve(resp.data);
                }
            });
        },
        getRentalTotalByDay() {
            return this.$ajax.post("/rental/total-day", {token: Tool.getLoginUser()}).then((response)=>{
                let resp = response.data;
                if(resp.code === 0){
                    return Promise.resolve(resp.data);
                }
            });
        },
        getAppointmentTotal() {
            return this.$ajax.post("/appointment/total", {token: Tool.getLoginUser()}).then((response)=>{
                let resp = response.data;
                if(resp.code === 0){
                    this.appointmentTotal = resp.data;
                    return Promise.resolve(resp.data);
                }
            });
        },
        getAppointmentTotalByDay() {
            return this.$ajax.post("/appointment/total-day", {token: Tool.getLoginUser()}).then((response)=>{
                let resp = response.data;
                if(resp.code === 0){
                    return Promise.resolve(resp.data);
                }
            });
        },
        getUserTotal() {
            this.$ajax.post("/user/total").then((response)=>{
                let resp = response.data;
                if(resp.code === 0){
                    this.userTotal = resp.data;
                }
            });
        }
    }
};
</script>

<style scoped>
.el-row {
    margin-bottom: 20px;
}

.grid-content {
    display: flex;
    align-items: center;
    height: 100px;
}

.grid-cont-right {
    flex: 1;
    text-align: center;
    font-size: 14px;
    color: #999;
}

.grid-num {
    font-size: 30px;
    font-weight: bold;
}

.grid-con-icon {
    font-size: 50px;
    width: 100px;
    height: 100px;
    text-align: center;
    line-height: 100px;
    color: #fff;
}

.grid-con-1 .grid-con-icon {
    background: rgb(45, 140, 240);
}

.grid-con-1 .grid-num {
    color: rgb(45, 140, 240);
}

.grid-con-2 .grid-con-icon {
    background: rgb(100, 213, 114);
}

.grid-con-2 .grid-num {
    color: rgb(45, 140, 240);
}

.grid-con-3 .grid-con-icon {
    background: rgb(242, 94, 67);
}

.grid-con-3 .grid-num {
    color: rgb(242, 94, 67);
}

.user-info {
    display: flex;
    align-items: center;
    padding-bottom: 20px;
    border-bottom: 2px solid #ccc;
    margin-bottom: 20px;
}

.user-avator {
    width: 120px;
    height: 120px;
    border-radius: 50%;
}

.user-info-cont {
    padding-left: 50px;
    flex: 1;
    font-size: 14px;
    color: #999;
}

.user-info-cont div:first-child {
    font-size: 30px;
    color: #222;
}

.user-info-list {
    font-size: 14px;
    color: #999;
    line-height: 25px;
}

.user-info-list span {
    margin-left: 70px;
}

.mgb20 {
    margin-bottom: 20px;
}
</style> 