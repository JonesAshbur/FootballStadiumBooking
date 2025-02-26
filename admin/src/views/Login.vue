<template>
    <div class="login-wrap">
        <div class="ms-login">
            <div class="ms-title">用户登录</div>
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
                <el-form-item prop="captcha">
                    <div style="display: flex">
                        <el-input type="text" style="width: 62%" v-model="param.captcha" auto-complete="off" placeholder="请输入验证码" @keyup.enter.native="submitForm()">
                            <el-button slot="prepend" icon="el-icon-key"></el-button>
                        </el-input>
                        <TheCaptcha style="height: 14px" @generatorCode="generatorCode" :identify-code="param.correctCaptcha" />
                    </div>
                </el-form-item>
                <div class="login-btn">
                    <el-button type="primary" @click="submitForm()">用户登录</el-button>
                </div>
                <p class="login-tips">还没有账号？<router-link to="/register" style="color: #fff; text-decoration: underline;">快去注册！</router-link></p>
            </el-form>
        </div>
    </div>
</template>

<script>
    import TheCaptcha from "../components/TheCaptcha.vue";
    export default {
        data: function() {
            return {
                loading: false,
                param: {
                    username: '',
                    password: '',
                    captcha: '',
                    correctCaptcha: '',
                    isAdmin: false
                },
                rules: {
                    username: [{ required: true, message: '请输入用户昵称', trigger: 'blur' }],
                    password: [{ required: true, message: '请输入用户密码', trigger: 'blur' }],
                    captcha: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
                },
            };
        },
        // 注册组件
        components: {
            TheCaptcha
        },
        created() {
            this.generatorCode();
        },
        methods: {
            generatorCode() {
                this.$ajax.post("/captcha/generate_captcha").then((response)=>{
                    let resp = response.data;
                    if(resp.code === 0){
                        this.param.correctCaptcha = resp.data;
                    }
                });
            },
            submitForm() {
                this.$refs.login.validate(valid => {
                    if (valid) {
                        this.loading = true;
                        this.$ajax.post("/user/login", this.param).then((response)=>{
                            let resp = response.data;
                            this.loading = false;
                            if(resp.code === 0){
                                Tool.setLoginUser(resp.data.token);
                                this.$message.success(resp.msg);
                                this.$router.push("/");
                            }else{
                                this.generatorCode();
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
