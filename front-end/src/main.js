import {createApp} from 'vue';
import App from './App.vue';
import routes from "@/router";
import axios from "axios";
import 'element-plus/theme-chalk/dark/css-vars.css';
import 'element-plus/dist/index.css'
import {createPinia} from "pinia";

axios.defaults.baseURL = "http://localhost:8080";
const app = createApp(App);
app.use(routes);
app.use(createPinia())//使用pinia存储一些用户信息
app.mount('#app');
