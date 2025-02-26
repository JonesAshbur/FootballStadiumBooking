import Vue from 'vue'
import App from './App.vue'
import ElementUI from 'element-ui';
import MessageBox from "element-ui/packages/message-box/src/main";
import 'element-ui/lib/theme-chalk/index.css';
import router from './router'
import axios from 'axios'


axios.defaults.baseURL = process.env.VUE_APP_URL;

Vue.use(ElementUI);
Vue.config.productionTip = false;
//全局使用的一个标识
Vue.prototype.$ajax = axios;


/**
 * axios拦截器
 */
axios.interceptors.request.use(function (config) {
  let token = Tool.getLoginUser();
  config.headers.token = token;
  config.headers.type = "axios";
  return config;
}, error => {
  return Promise.reject(error);
});


axios.interceptors.response.use(function (response) {
  console.log("返回结果：", response);
  if(response.data.code === -6){
    MessageBox.alert(response.data.msg, '消息提示', {
      confirmButtonText: "重新登录",
      showClose: false,
      callback: action => {
        window.location.href = "/login";
      }
    });
  }else {
    // 正常返回数据
    return response;
  }
}, error => {
});

// 路由登录拦截
router.beforeEach((to, from, next) => {
  // 更新页面标题
  if (to.meta.title) {
    document.title = to.meta.title;
  }
  
  // 要不要对meta.loginRequire属性做监控拦截
  if (to.matched.some(function (item) {
    return item.meta.loginRequire
  })) {
    let loginUser = Tool.getLoginUser();
    if (Tool.isEmpty(loginUser)) {
      next('/login');
    } else {
      next();
    }
  } else {
    next();
  }
});

new Vue({
  router,
  render: h => h(App),
  data: {
    // 空的实例放到根组件下，所有的子组件都能调用
    Bus: new Vue()
  }
}).$mount('#app');

console.log("环境：", process.env.NODE_ENV);
