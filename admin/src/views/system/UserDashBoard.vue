<template>
    <div class="user-dashboard">
        <!-- 公告区域 -->
        <div class="announcement-section">
            <div class="section-header">
                <h3>最新公告</h3>
                <router-link to="announce-list" class="view-all">
                    查看全部 <i class="el-icon-arrow-right"></i>
                </router-link>
            </div>
            <el-table 
                :data="tableData" 
                style="width: 100%"
                :header-cell-style="{background:'#f5f7fa'}"
                v-loading="loading">
                <el-table-column prop="content" label="公告内容" min-width="350">
                    <template slot-scope="scope">
                        <div class="announcement-content">
                            <i class="el-icon-bell"></i>
                            <span>{{scope.row.content}}</span>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column prop="userDTO.username" label="发布者" width="150">
                    <template slot-scope="scope">
                        <el-tag size="small">{{scope.row.userDTO.username}}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="发布时间" width="180">
                    <template slot-scope="scope">
                        <i class="el-icon-time"></i>
                        <span style="margin-left: 8px">{{scope.row.createTime}}</span>
                    </template>
                </el-table-column>
            </el-table>
        </div>

        <!-- 顶部欢迎区域 -->
        <div class="welcome-section">
            <div class="user-profile">
                <img :src="loginUser.headPic|filterPhoto" class="avatar" alt="用户头像" />
                <div class="welcome-text">
                    <h2>欢迎回来, {{loginUser.username}}</h2>
                    <p>{{loginUser.roleId|filterRole}} · {{loginUser.phone}}</p>
                </div>
            </div>
            <div class="date-time">
                <div class="current-time">{{currentTime}}</div>
                <div class="current-date">{{currentDate}}</div>
            </div>
        </div>

        <!-- 数据概览卡片 -->
        <el-row :gutter="20" class="data-overview">
            <el-col :span="8">
                <div class="overview-card total-users">
                    <div class="card-icon">
                        <i class="el-icon-user"></i>
                    </div>
                    <div class="card-content">
                        <div class="card-title">总用户数</div>
                        <div class="card-number">{{userTotal}}</div>
                        <div class="card-footer">活跃用户数据</div>
                    </div>
                </div>
            </el-col>
            <el-col :span="8">
                <div class="overview-card total-rentals">
                    <div class="card-icon">
                        <i class="el-icon-money"></i>
                    </div>
                    <div class="card-content">
                        <div class="card-title">总租借数</div>
                        <div class="card-number">{{rentalTotal}}</div>
                        <div class="card-footer">器材使用情况</div>
                    </div>
                </div>
            </el-col>
            <el-col :span="8">
                <div class="overview-card total-appointments">
                    <div class="card-icon">
                        <i class="el-icon-alarm-clock"></i>
                    </div>
                    <div class="card-content">
                        <div class="card-title">总预约数</div>
                        <div class="card-number">{{appointmentTotal}}</div>
                        <div class="card-footer">场地预约情况</div>
                    </div>
                </div>
            </el-col>
        </el-row>

        <!-- 图表区域 -->
        <el-row :gutter="20" class="chart-section">
            <el-col :span="12">
                <div class="chart-card">
                    <div class="chart-header">
                        <h3>数据统计</h3>
                    </div>
                    <div class="chart-container" ref="barContainer">
                        <echart v-if="chartReady" ref="barChart" :chartData="echartData.barRatio" isChart="bar"></echart>
                    </div>
                </div>
            </el-col>
            <el-col :span="12">
                <div class="chart-card">
                    <div class="chart-header">
                        <h3>近5天统计</h3>
                    </div>
                    <div class="chart-container" ref="pieContainer">
                        <echart v-if="chartReady" ref="pieChart" :chartData="echartData.scatterTotal" isChart="pie"></echart>
                    </div>
                </div>
            </el-col>
        </el-row>
    </div>
</template>

<script>
import bus from '../../assets/js/event-bus';
import Echart from "@/components/ECharts.vue";

export default {
    name: 'UserDashBoard',
    components: {
        Echart
    },
    data() {
        return {
            chartReady: false,
            currentTime: '',
            currentDate: '',
            loading: false,
            tableData: [],
            searchParams: {
                content: ''
            },
            rentalTotal: 0,
            appointmentTotal: 0,
            userTotal: 0,
            echartData: {
                barRatio: {
                    xData: ['租借器材', '预约场地'],
                    series: [{
                        name: '数量',
                        type: 'bar',
                        barWidth: '40%',
                        itemStyle: {
                            color: function(params) {
                                const colors = ['#1890ff', '#52c41a'];
                                return colors[params.dataIndex];
                            }
                        },
                        data: [0, 0]
                    }]
                },
                scatterTotal: {
                    series: [{
                        type: 'pie',
                        radius: ['40%', '70%'],
                        center: ['50%', '50%'],
                        label: {
                            show: true,
                            position: 'outside',
                            formatter: '{b}: {d}%',
                            color: '#333',
                            fontSize: 12,
                            fontWeight: 'normal'
                        },
                        labelLine: {
                            show: true,
                            length: 10,
                            length2: 10
                        },
                        data: [{
                            value: 0,
                            name: '租借器材',
                            itemStyle: { color: '#1890ff' }
                        }, {
                            value: 0,
                            name: '预约场地',
                            itemStyle: { color: '#52c41a' }
                        }]
                    }]
                }
            },
            loginUser: {
                headPic: "common/no_image.jpg"
            },
            timer: null
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
        this.initDateTime();
        this.timer = setInterval(this.updateDateTime, 1000);
        
        this.getLoginUser().then(() => {
            this.loadAllData();
        });
        
        bus.$on('refreshUserList', msg => {
            this.getLoginUser();
        });
    },
    beforeDestroy() {
        if (this.timer) {
            clearInterval(this.timer);
        }
    },
    mounted() {
        // 确保容器已渲染
        this.$nextTick(() => {
            setTimeout(() => {
                this.chartReady = true;
            }, 100);
        });
    },
    watch: {
        // 监听图表数据变化
        echartData: {
            handler(newVal) {
                if (this.chartReady) {
                    if (this.$refs.barChart) {
                        this.$refs.barChart.initChart();
                    }
                    if (this.$refs.pieChart) {
                        this.$refs.pieChart.initChart();
                    }
                }
            },
            deep: true
        }
    },
    methods: {
        initDateTime() {
            const now = new Date();
            this.updateDateTime();
        },
        updateDateTime() {
            const now = new Date();
            this.currentTime = now.toLocaleTimeString();
            this.currentDate = now.toLocaleDateString('zh-CN', { 
                year: 'numeric', 
                month: 'long', 
                day: 'numeric',
                weekday: 'long'
            });
        },
        loadAllData() {
            this.getAnnounceList();
            this.getUserTotal();
            
            // 获取租借和预约总数并更新柱状图
            Promise.all([this.getRentalTotal(), this.getAppointmentTotal()])
                .then(([rentalTotal, appointmentTotal]) => {
                    console.log('柱状图数据:', rentalTotal, appointmentTotal);
                    this.echartData.barRatio.series[0].data = [rentalTotal, appointmentTotal];
                })
                .catch(error => {
                    console.error('获取总数据失败:', error);
                });

            // 获取近5天的数据统计并更新环形图
            Promise.all([this.getRentalTotalByDay(), this.getAppointmentTotalByDay()])
                .then(([rentalData, appointmentData]) => {
                    const rentalSum = rentalData.reduce((a, b) => a + b, 0);
                    const appointmentSum = appointmentData.reduce((a, b) => a + b, 0);
                    
                    console.log('环形图数据:', rentalSum, appointmentSum);
                    
                    this.echartData.scatterTotal.series[0].data = [
                        {
                            value: rentalSum,
                            name: '租借器材',
                            itemStyle: { color: '#1890ff' }
                        },
                        {
                            value: appointmentSum,
                            name: '预约场地',
                            itemStyle: { color: '#52c41a' }
                        }
                    ];
                })
                .catch(error => {
                    console.error('获取5天统计数据失败:', error);
                });
        },
        getLoginUser() {
            return this.$ajax.post("/user/getLoginUser", {token: Tool.getLoginUser()}).then((response)=>{
                let resp = response.data;
                if(resp.code === 0){
                    if(resp.data) {
                        this.loginUser = resp.data;
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
.user-dashboard {
    padding: 0;
    background: #f0f2f5;
    min-height: 100vh;
}

/* 公告区域样式 */
.announcement-section {
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(10px);
    -webkit-backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 12px;
    padding: 12px;
    margin-bottom: 8px;
}

.section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8px;
}

.section-header h3 {
    margin: 0;
    font-size: 16px;
    color: #333;
}

.view-all {
    color: #1890ff;
    text-decoration: none;
    font-size: 14px;
    display: flex;
    align-items: center;
}

.view-all i {
    margin-left: 4px;
}

.announcement-content {
    display: flex;
    align-items: center;
    gap: 8px;
}

.announcement-content i {
    color: #1890ff;
}

/* 欢迎区域样式 */
.welcome-section {
    background: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%);
    border-radius: 16px;
    padding: 16px;
    color: white;
    margin-bottom: 8px;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.user-profile {
    display: flex;
    align-items: center;
}

.avatar {
    width: 64px;
    height: 64px;
    border-radius: 50%;
    border: 3px solid rgba(255, 255, 255, 0.3);
    margin-right: 20px;
}

.welcome-text h2 {
    margin: 0;
    font-size: 24px;
    font-weight: 600;
    color: white;
}

.welcome-text p {
    margin: 8px 0 0;
    opacity: 0.8;
    color: white;
}

.date-time {
    text-align: right;
}

.current-time {
    font-size: 32px;
    font-weight: 600;
    margin-bottom: 8px;
    color: white;
}

.current-date {
    opacity: 0.8;
    color: white;
}

/* 数据概览卡片样式 */
.data-overview {
    margin-bottom: 8px;
}

.overview-card {
    background: white;
    border-radius: 12px;
    padding: 16px;
    display: flex;
    align-items: center;
    transition: all 0.3s;
}

.overview-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.card-icon {
    width: 48px;
    height: 48px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 16px;
    font-size: 24px;
}

.total-users .card-icon {
    background: rgba(24, 144, 255, 0.1);
    color: #1890ff;
}

.total-rentals .card-icon {
    background: rgba(82, 196, 26, 0.1);
    color: #52c41a;
}

.total-appointments .card-icon {
    background: rgba(250, 140, 22, 0.1);
    color: #fa8c16;
}

.card-content {
    flex: 1;
}

.card-title {
    color: #666;
    margin-bottom: 8px;
}

.card-number {
    font-size: 24px;
    font-weight: 600;
    color: #333;
    margin-bottom: 4px;
}

.card-footer {
    font-size: 12px;
    color: #999;
}

/* 图表区域样式 */
.chart-section {
    margin-bottom: 8px;
}

.chart-card {
    background: white;
    border-radius: 12px;
    padding: 12px;
    height: 340px;
    box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.chart-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
}

.chart-header h3 {
    margin: 0;
    font-size: 16px;
    color: #333;
}

.chart-container {
    height: 270px;
    width: 100%;
    position: relative;
}

.chart-container > div {
    width: 100%;
    height: 100%;
}

.chart-legend {
    display: flex;
    gap: 16px;
}

.legend-item {
    display: flex;
    align-items: center;
    font-size: 14px;
    color: #666;
}

.legend-color {
    width: 12px;
    height: 12px;
    border-radius: 2px;
    margin-right: 8px;
}

/* Element UI 样式覆盖 */
:deep(.el-table) {
    border-radius: 8px;
    font-size: 13px;
}

:deep(.el-table td) {
    padding: 8px 0;
}

:deep(.el-table th) {
    padding: 8px 0;
}

:deep(.el-tag) {
    border-radius: 4px;
}
</style> 