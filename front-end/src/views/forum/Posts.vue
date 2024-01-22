<script setup>

import LightCard from "@/components/LightCard.vue";
import {Calendar, CollectionTag, EditPen, Link} from "@element-plus/icons-vue";
import {computed,reactive} from "vue";
import {get} from "@/net";
import { ref, onMounted, onUnmounted } from 'vue';
import axios from "axios";
import {useStore} from "@/store";
import {ElMessage} from "element-plus";
import PostEditor from "@/components/PostEditor.vue";
const store = useStore()

let currentTime = ref(new Date().toLocaleString());
let intervalId;
onMounted(() => {
  intervalId = setInterval(() => {
    currentTime.value = new Date().toLocaleString();
  }, 1000);
});
onUnmounted(() => {
  clearInterval(intervalId);
});

let quote = ref('');
onMounted(async () => {
  const response = await axios.get('https://api.vvhan.com/api/ian');
  quote.value = response.data;
});

const weather = reactive({
  location:{},
  now:{},
  hourly:{},
  success:false
})
navigator.geolocation.getCurrentPosition((position)=>{
  const longitude = position.coords.longitude
  const latitude = position.coords.latitude
  get(`api/forum/weather?longitude=${longitude}&latitude=${latitude}`,(data)=>{
    Object.assign(weather,data) //拷贝数据
    weather.success = true
  })
},(error)=>{
  ElMessage.error("无法获取您的位置")
  console.log('位置信息错误：'+error)
  get('api/forum/weather?longitude=116.40529&latitude=39.90499',(data)=>{
    Object.assign(weather,data) //拷贝数据
    weather.success = true
  })
},{
  timeout:3000,
  enableHighAccuracy:true
})

const WeatherImageUrl = computed(() => `https://api.vvhan.com/api/ip?tip=Hello  ${encodeURIComponent(store.user.username)}%20欢迎来到论坛！`);
let imageLoaded = ref(false);
const onImageLoad = () => {
  imageLoaded.value = true;
};

const editor = ref(false)

</script>

<template>
  <div style="display: flex;margin: 20px auto;gap: 30px;max-width: 1000px;">
    <div style="flex: 1;height: 2000px">
      <LightCard >
        <div class="create-post" @click="editor= true">
          <el-icon><EditPen/></el-icon>
          点击发布帖子
        </div>
      </LightCard>
      <div style="margin-top: 10px;display: flex;flex-direction: column;gap: 10px">
        <LightCard style="height: 30px">

        </LightCard>
        <LightCard style="height: 100px" v-for="item in 10">

        </LightCard>
      </div>
    </div>
    <div style="width: 300px;">
      <div style="position: sticky;top: 20px">
        <LightCard>
          <el-icon>
            <CollectionTag/>
          </el-icon>
          <el-text style="font-weight: bold;font-size: 18px" class="mx-1" type="primary">
            论坛公告
          </el-text>
          <el-divider style="margin: 8px 0"></el-divider>
          <el-text style="font-size: 14px;margin:10px;" class="mx-1" type="info">
            fasdfasdfsadfasdfsdagfigadsfgjasdgfkjasdgfkasdfgsa
            寒潮天气不断深入我国，中东部地区气温继续走低，并伴有大风天气，中央气象台目前已发布暴雪蓝色、寒潮黄色、大风蓝色预警。预计21至22日，内蒙古西部、东北地区中东部、西北地区东部、华北大部、黄淮东部、江淮大部、江南中东部和南部、西南地区南部、华南大部等
          </el-text>
        </LightCard>

          <LightCard style="margin-top: 10px">
            <el-icon><Calendar/></el-icon>
            <el-text style="font-weight: bold;font-size: 18px" class="mx-1" type="warning">
              天气信息
            </el-text>
            <el-divider style="margin: 8px 0"></el-divider>
            <Weather :data="weather"/>
            <div style="margin-top: 10px">
              <el-collapse-transition style="transition: 1s ease-in-out;">
                <el-image  v-show="imageLoaded" @load="onImageLoad" :src="WeatherImageUrl"></el-image>
              </el-collapse-transition>
            </div>

          </LightCard>
          <LightCard  style="margin-top: 10px">
            <el-text size="large">
              <div>{{currentTime}}</div>
              <div style="margin-top: 3px">{{quote}}</div>
<!--              <div class="info-text">-->
<!--                <div>IP:</div>-->
<!--                <div>127.0.0.1</div>-->
<!--              </div>-->

            </el-text>
          </LightCard>
        <div style="font-size: 16px;margin-top: 10px;color: grey">
          <el-icon><Link/></el-icon>
          友情链接
          <el-divider style="margin: 10px 0"></el-divider>
        </div>
        <div style="margin-top: 10px">
          <div class="info-text">
            <el-text size="large">Mr.X's Blog</el-text>
            <el-link  href="http://1.94.60.217" target="_blank" type="primary">1.94.60.217</el-link>
          </div>
        </div>
      </div>
    </div>
    <PostEditor :show="editor" @close="editor = false"></PostEditor>
  </div>
</template>

<style lang="less" scoped>
.info-text{
  display: flex;
  justify-content: space-between;
}
.create-post{
  background-color: grey;
  border-radius: 5px;
  height: 40px;
  font-size: 14px;
  line-height:40px ;
  padding: 0 10px;
  &:hover{
    cursor: pointer;
  }
}
</style>
