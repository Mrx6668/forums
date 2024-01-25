<script setup>

import LightCard from "@/components/LightCard.vue";
import {Calendar, Clock, CollectionTag, EditPen, Link} from "@element-plus/icons-vue";
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

get('api/forum/types',(data)=>{
  console.log("types:",data)
  // console.log("editor.types:",editor.types)
  store.forum.types = data
})

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
const postList = ref(null)
function updateList(){
  get('/api/forum/list-post?page=0&type=0',(data)=>{
    postList.value = data
    // console.log("postList.value: "+data)
  })
}
updateList()
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
      <div style="margin-top: 10px;display: flex;flex-direction: column;gap: 10px" v-if="store.forum.types">
        <LightCard class="post-card" v-for="item in postList" >
          <div style="display:flex;">
            <div>
              <el-avatar size="default" :src="`${axios.defaults.baseURL}/api/image/get?imageName=`+item.avatar"></el-avatar>
            </div>
            <div style="margin-left: 7px">
              <el-text style="font-weight: bold;font-size: 13px;margin-bottom: 2px">{{item.username}}</el-text>
<!--              <el-text  type="info"></el-text>-->
              <div style="font-weight: bold;font-size: 12px;color: grey;margin-top: 2px">
                <el-icon><Clock/></el-icon>
                <div style="margin-left: 2px;display: inline-block;transform: translateY(-2px)">{{new Date(item.createTime).toLocaleString()}}</div>
              </div>
            </div>
          </div>
          <div style="margin-top: 5px">
            <el-text class="post-type" :style="{
              color:store.findTypeById(item.postType)?.color + 'EE',
              'border-color' : store.findTypeById(item.postType)?.color + '77',
              'background-color' : store.findTypeById(item.postType)?.color + '22'
            }">{{store.findTypeById(item.postType).title}}</el-text>
            <span style="font-weight: bold;margin-left: 7px;font-size: 16px">{{item.title}}</span>
          </div>
          <el-text class="post-content" type="info">{{item.content}}</el-text>
<!--          <div class="post-content">{{item.content}}</div>-->
          <div style="display: grid;grid-template-columns: repeat(3,1fr);grid-gap: 10px">
            <el-image class="post-image" v-for="img in item?.images" :src="img" fit="cover"></el-image>
          </div>
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
            <div style="margin-top: 25px;">
<!--              <el-divider style="margin: 10px 0"></el-divider>-->
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
    <PostEditor :show="editor" @createSuccess="editor=false;updateList()" @close="editor = false"></PostEditor>
  </div>
</template>

<style lang="less" scoped>
.post-card{
  padding: 15px;
  transition: transform 0.3s linear;
  &:hover{
    transform: scale(1.03);
    cursor: pointer;
    transition: transform 0.3s linear;
  }
  .post-content{
    font-size: 13px;
    margin: 5px 0;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 3;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  .post-type{
    display: inline-block;
    border: solid 0.1px grey;
    border-radius: 5px;
    font-size: 12px;
    padding: 1px 5px;
    vertical-align: middle; /* 垂直居中 */
  }
  .post-image{
    width: 100%;
    height: 100%;
    border-radius: 5px;
    max-height: 110px;
  }
}
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
.dark .create-post{
  background-color: #303030;
}

</style>
