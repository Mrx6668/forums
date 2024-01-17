import { createApp } from 'vue';
import App from './App.vue';
import routes from "@/router";
import axios from "axios";
import 'element-plus/theme-chalk/dark/css-vars.css';
import 'element-plus/dist/index.css'

axios.defaults.baseURL = "http://localhost:8080";
const app = createApp(App);
app.use(routes);

app.mount('#app');
