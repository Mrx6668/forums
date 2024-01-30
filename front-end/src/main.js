import {createApp} from 'vue';
import App from './App.vue';
import routes from "@/router";
import axios from "axios";
import 'element-plus/theme-chalk/dark/css-vars.css';
import 'element-plus/dist/index.css'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import {createPinia} from "pinia";
import('@/assets/quill.css')

axios.defaults.baseURL = "http://localhost:8080";
const app = createApp(App);
const pinia = createPinia();
pinia.use(piniaPluginPersistedstate);
app.use(routes);
app.use(pinia)//使用pinia存储一些用户信息
app.mount('#app');
