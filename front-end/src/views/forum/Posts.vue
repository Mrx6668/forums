<script setup>

import LightCard from "@/components/LightCard.vue";
import {
  Calendar,
  Clock,
  CollectionTag,
  Compass,
  Document,
  Edit,
  EditPen,
  Link,
  Microphone,
  Picture, Pointer, Star
} from "@element-plus/icons-vue";
import {computed,reactive,watch} from "vue";
import {get} from "@/net";
import { ref, onMounted, onUnmounted } from 'vue';
import axios from "axios";
import {useStore} from "@/store";
import {ElMessage} from "element-plus";
import PostEditor from "@/components/PostEditor.vue";
import ColorDot from "@/components/ColorDot.vue";
import router from "@/router";
import {useRoute} from "vue-router";
import PostType from "@/components/PostType.vue";
const store = useStore()
const type = ref(0)
const postList = ref([])
const page = ref(0)
const end = ref(false)
const top = ref([])
const route = useRoute();
// const pid = route.params.pid
// const detail = ref({})
watch(type,()=>{
  resetList()
})
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
  // console.log("editor.types:",editor.types)
  const array = []
  array.push({id:0,title:'全部',color:'linear-gradient(45deg,white,red,orange,gold,green,blue)'})
  data.forEach(d=>array.push(d))
  store.forum.types = array
  console.log("types:",store.forum.types)
})

function onPostSuccess() {
  editor.value = false
  resetList()
}

function resetList(){
  page.value = 0
  end.value = false
  postList.value = []
  updateList()
}

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

function updateList(){
  if (route.name !== 'posts') return
  if (end.value){
    ElMessage.info("已经翻到底了哦")
    return
  }
  console.log("updateList!")
  get(`/api/forum/list-post?page=${page.value}&type=${type.value}`,(data)=>{
    if(data){
      data.forEach(d=>postList.value.push(d))
      page.value++
    }
    if (!data || data.length < 10){
      end.value = true
    }
    // postList.value = data
    // console.log("postList.value: "+data)
  })
}
get('api/forum/top-post',data=>{
  top.value = data
})
updateList()


// import { onActivated, onDeactivated } from 'vue'
//
// const scrollTop = ref(0)
//
//
//
// onActivated(() => {
//   console.log("恢复位置"+scrollTop.value)
//   // 激活时恢复滚动位置
//   window.scrollTo(0, scrollTop.value)
// })
//
// onDeactivated(() => {
//   // 失活时保存滚动位置
//   scrollTop.value =  document.body.scrollTop || window.pageYOffset
//   console.log("保存位置"+scrollTop.value)
// })

</script>

<template>
  <div style="display: flex;margin: 20px auto;gap: 30px;max-width: 1000px;" class="post">
    <div style="flex: 1;height: 2000px" >
      <LightCard >
        <div class="create-post" @click="editor= true">
          <el-icon><EditPen/></el-icon>
          点击发布帖子
        </div>
        <div style="margin-top: 10px;display: flex;gap: 13px;font-size: 18px;color: grey">
          <el-icon><Edit/></el-icon>
          <el-icon><Document/></el-icon>
          <el-icon><Compass/></el-icon>
          <el-icon><Picture/></el-icon>
          <el-icon><Microphone/></el-icon>
        </div>
      </LightCard>
      <LightCard style="margin-top: 10px;display: flex;gap: 7px">
        <div :class="`type-select-card ${type === item.id ? 'active' : ''}`"
             v-for="item in store.forum.types"
             @click="type = item.id">
          <ColorDot :color="item.color"/>
          <span>{{item.title}}</span>
        </div>
      </LightCard>
      <LightCard   style="margin-top: 10px;display: flex;flex-direction: column;gap: 10px">
        <div @click="router.push('/index/post-detail/'+item.id)" v-for="item in top" class="top-post" style="">
          <el-tag type="info">置顶</el-tag>
          <div class="top-post-title" style="">{{item.title}}</div>
          <div style="margin: auto 0">{{new Date(item.createTime).toLocaleDateString()}}</div>
        </div>
      </LightCard>

      <transition name="el-fade-in" mode="out-in">
        <div v-if="postList?.length">
          <div style="margin-top: 10px;display: flex;flex-direction: column;gap: 10px"
               v-if="store.forum.types" v-infinite-scroll="updateList" infinite-scroll-distance="15">
            <LightCard class="post-card" v-for="item in postList" @click="router.push('/index/post-detail/'+item.id)" >
              <div style="display:flex;">
                <div>
                  <el-avatar size="default" :src="store.avatarUserUrl(item.avatar)"></el-avatar>
                </div>
<!--                <div>-->
<!--                  <el-avatar size="default" :src="`${axios.defaults.baseURL}/api/image/get?imageName=`+item.avatar"></el-avatar>-->
<!--                </div>-->
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
                <PostType :type="item.postType"/>
                <span style="font-weight: bold;margin-left: 7px;font-size: 16px">{{item.title}}</span>
              </div>
              <el-text class="post-content" type="info">{{item.content}}</el-text>
              <!--          <div class="post-content">{{item.content}}</div>-->
              <div style="display: grid;grid-template-columns: repeat(3,1fr);grid-gap: 10px">
                <el-image class="post-image" v-for="img in item?.images" :src="img" fit="cover"></el-image>
              </div>
              <div style="display:flex;justify-content: space-between;margin-top: 10px">
                <div style="">
                  <el-button size="small"
                             :type="item.like ? 'success' : 'info'"
                             plain round :icon="Pointer"> {{item.like}}</el-button>
                  <el-button size="small"
                             :type="item.collect ? 'warning' : 'info'"
                             plain round :icon="Star"> {{item.collect}}</el-button>
                </div>
                <div>
                  <el-text type="info">浏览{{item.views}}次</el-text>
                </div>
              </div>


            </LightCard>
          </div>
        </div>

      </transition>
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
            本社区禁止讨论敏感内容，请友好交流！<br>禁止讨论原神！
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
    <PostEditor :show="editor" @createSuccess="onPostSuccess()" @close="editor = false"></PostEditor>
  </div>
</template>

<style lang="less" scoped>
.top-post{
  display: flex;
  .top-post-title{
      font-size: 14px;
      margin: auto 0 auto 10px;
      //margin-left: 10px;
      font-weight: bold;
      opacity: 0.8;
      transition: color .4s;
    &:hover{
      color: var(--el-color-primary);
    }
  }
  &:hover{
    cursor: pointer;
  }
  div:nth-of-type(2){
    flex: 1;
    color: grey;
    font-size: 13px;
    text-align: right;
  }
}

.type-select-card{
  background-color: #f5f5f5;
  padding: 2px 7px;
  font-size: 14px;
  border-radius: 3px;
  box-sizing: border-box;
  transition: background-color 0.3s;
  &.active{
    border: solid 1px var(--el-color-success);
  }
  &:hover{
    cursor: pointer;
    background-color: #dadada;
  }
}
.post-card{
  padding: 15px;
  transition: transform 0.3s linear;
  &:hover{
    transform: scale(1.02);
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
  background-color: var(--el-color-info-light-9);
  border-radius: 5px;
  height: 40px;
  font-size: 14px;
  line-height:40px ;
  padding: 0 10px;
  transition: background-color 0.3s ease;
  &:hover{
    cursor: pointer;
    background-color: var(--el-color-info-light-7);
  }
}
.dark{
  .dark .create-post{
    background-color: #303030;
  }
  .type-select-card{
    background-color: #282828;
    &.active{
      border: solid 1px #64594b;
    }
    &:hover{
      background-color: #5e5e5e;
    }
  }
}

</style>
