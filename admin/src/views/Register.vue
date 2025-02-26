<template>
    <div class="login-wrap">
        <div class="ms-login">
            <div class="ms-title">足球场预约管理系统</div>
            <el-form :model="param" :rules="rules" ref="login" label-width="0px" class="ms-content">
                <el-form-item prop="username">
                    <el-input v-model="param.username" placeholder="请输入用户昵称">
                        <el-button slot="prepend" icon="el-icon-user"></el-button>
                    </el-input>
                </el-form-item>
                <el-form-item prop="password">
                    <el-input
                            type="password"
                            placeholder="请输入用户密码"
                            v-model="param.password"
                    >
                        <el-button slot="prepend" icon="el-icon-lock"></el-button>
                    </el-input>
                </el-form-item>
                <el-form-item prop="rePassword">
                    <el-input
                            type="password"
                            placeholder="请输入确认密码"
                            v-model="param.rePassword"
                    >
                        <el-button slot="prepend" icon="el-icon-lock"></el-button>
                    </el-input>
                </el-form-item>
                <el-form-item prop="phone">
                    <el-input v-model="param.phone" placeholder="请输入手机号码"  @keyup.enter.native="submitForm()">
                        <el-button slot="prepend" icon="el-icon-mobile-phone"></el-button>
                    </el-input>
                </el-form-item>
                <div class="login-btn">
                    <el-button type="primary" @click="submitForm()">注册</el-button>
                </div>
                <p class="login-tips">已有账号？<router-link to="/login" style="color: #333; font-weight: bold; text-decoration: underline;">快去登录！</router-link></p>
            </el-form>
        </div>
    </div>
</template>

<script>
    export default {
        data: function() {
            return {
                loading: false,
                param: {
                    username: '',
                    password: '',
                    rePassword: '',
                    phone: ''
                },
                rules: {
                    username: [{ required: true, message: '请输入用户昵称', trigger: 'blur' }],
                    password: [{ required: true, message: '请输入用户密码', trigger: 'blur' }],
                    rePassword: [{ required: true, message: '请输入确认密码', trigger: 'blur' }],
                    phone: [{ required: true, message: '请输入手机号码', trigger: 'blur' }]
                },
            };
        },
        methods: {
            submitForm() {
                this.$refs.login.validate(valid => {
                    if (valid) {
                        this.loading = true;
                        this.$ajax.post("/user/register", this.param).then((response)=>{
                            let resp = response.data;
                            this.loading = false;
                            if(resp.code === 0){
                                this.param = {};
                                this.$message.success(resp.msg);
                            }else{
                                this.$message.error(resp.msg);
                            }
                        });
                    }
                });
            },
        },
    };
</script>

<style scoped>
    .login-wrap {
        position: relative;
        width: 100%;
        background-repeat: no-repeat;
        height: 100%;
        background-image: url(../assets/img/login_register_bgc.jpg);
        background-size: 100% 100%;
    }
    .ms-title {
        width: 100%;
        line-height: 50px;
        text-align: center;
        font-size: 20px;
        color: #fff;
        border-bottom: 1px solid #ddd;
    }
    .ms-login {
        position: absolute;
        left: 50%;
        top: 50%;
        width: 350px;
        margin: -190px 0 0 -175px;
        border-radius: 5px;
        background: rgba(255, 255, 255, 0.3);
        overflow: hidden;
    }
    .ms-content {
        padding: 30px 30px;
    }
    .login-btn {
        text-align: center;
    }
    .login-btn button {
        width: 100%;
        height: 36px;
        margin-bottom: 10px;
    }
    .login-tips {
        font-size: 14px;
        line-height: 30px;
        color: #fff;
    }
</style>
