<template>
    <div class="login-wrap">
        <div class="ms-login">
            <div class="ms-title">足球场预约管理系统 - 管理员登录</div>
            <el-form :model="param" :rules="rules" ref="login" label-width="0px" class="ms-content">
                <el-form-item prop="username">
                    <el-input v-model="param.username" placeholder="请输入管理员账号">
                        <el-button slot="prepend" icon="el-icon-user"></el-button>
                    </el-input>
                </el-form-item>
                <el-form-item prop="password">
                    <el-input
                            type="password"
                            placeholder="请输入管理员密码"
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
                    <el-button type="primary" @click="submitForm()">管理员登录</el-button>
                </div>
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
                    isAdmin: true
                },
                rules: {
                    username: [{ required: true, message: '请输入管理员账号', trigger: 'blur' }],
                    password: [{ required: true, message: '请输入管理员密码', trigger: 'blur' }],
                    captcha: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
                },
            };
        },
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
        height: 100%;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    }
    .ms-title {
        width: 100%;
        line-height: 50px;
        text-align: center;
        font-size: 20px;
        color: #fff;
        border-bottom: 1px solid rgba(255, 255, 255, 0.2);
    }
    .ms-login {
        position: absolute;
        left: 50%;
        top: 50%;
        width: 350px;
        margin: -190px 0 0 -175px;
        border-radius: 15px;
        backdrop-filter: blur(10px);
        background: rgba(255, 255, 255, 0.2);
        box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.37);
        border: 1px solid rgba(255, 255, 255, 0.18);
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
        background: rgba(255, 255, 255, 0.2);
        border: 1px solid rgba(255, 255, 255, 0.3);
        color: #fff;
        font-weight: bold;
    }
    .login-btn button:hover {
        background: rgba(255, 255, 255, 0.3);
        border-color: rgba(255, 255, 255, 0.4);
    }
    .login-tips {
        font-size: 14px;
        line-height: 30px;
        color: #fff;
        text-align: center;
    }
    .login-tips a {
        color: #fff !important;
        text-decoration: underline !important;
    }
    /* 修改输入框样式 */
    ::v-deep .el-input__inner {
        background: rgba(255, 255, 255, 0.1);
        border: 1px solid rgba(255, 255, 255, 0.2);
        color: #fff;
    }
    ::v-deep .el-input__inner::placeholder {
        color: rgba(255, 255, 255, 0.7);
    }
    ::v-deep .el-button {
        background: rgba(255, 255, 255, 0.1);
        border: 1px solid rgba(255, 255, 255, 0.2);
        color: #fff;
    }
</style> 