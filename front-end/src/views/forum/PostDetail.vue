<script setup>
import {ref,reactive,computed} from "vue";
import {useRoute} from "vue-router";
import {get} from "@/net";
import axios from "axios";
import {ArrowLeft, CircleCheck, Compass, Female, Hide, Male, Pointer, Star} from "@element-plus/icons-vue";
import {QuillDeltaToHtmlConverter} from "quill-delta-to-html";
import Card from "@/components/Card.vue";
import PostType from "@/components/PostType.vue";
import InteractButton from "@/components/InteractButton.vue";
import {ElMessage} from "element-plus";
const route = useRoute();
const pid = route.params.pid

const post = reactive({
  data:null,
  comments:[]
})

const interact = reactive({
  like:false,
  collect:false,
})

function getDetail() {
  get(`api/forum/post?pid=${pid}`, (data) => {
    console.log("post detail: " + data)
    post.data = data
  })
}

getDetail();

const content = computed(()=>{
  const ops = JSON.parse(post.data.content).ops
  const converter = new QuillDeltaToHtmlConverter(ops,{inlineStyle:true})
  return converter.convert()
})

function clickInteract(type,message) {
  // interact[type] = !interact[type]
  // if (interact[type]) {
  //   ElMessage.success(`${message}成功`)
  // }else {
  //   ElMessage.success(`已取消${message}`)
  // }

  get(`api/forum/interact?pid=${pid}&type=${type}&state=${!interact[type]}`, (data) => {
    interact[type] = !interact[type]
    if (interact[type]) {
      ElMessage.success(`${message}成功`)
    }else {
      ElMessage.success(`已取消${message}`)
    }

  })
}
</script>

<template>
  <div class="post-page" v-if="post.data">
    <div class="post-main" style="position: sticky;top: 0;z-index: 10">
      <Card style="display: flex;width: 100%">
        <el-button :icon="ArrowLeft" size="small" round plain type="info" @click="$router.go(-1)">返回列表</el-button>
        <div style="flex: 1;text-align: center;margin: auto 0" v-if="post.data?.postType" >
          <PostType :type="post.data.postType"/>
          <el-text style="margin-left: 5px" size="large">{{post.data.title}}</el-text>
        </div>
      </Card>
    </div>
    <div class="post-main">
      <div class="post-main-left">
        <el-avatar size="large"
                   :src="axios.defaults.baseURL+'/api/image/get?imageName='+post.data.user.avatar"></el-avatar>
        <div>
          <el-text type="primary" size="large" style="font-weight: bold">
            {{post.data.user.username}}
            <span style="color: hotpink" v-if="post.data.user.gender === 1">
              <el-icon><Female/></el-icon>
            </span>
            <span style="color: dodgerblue" v-if="post.data.user.gender === 0">
              <el-icon><Male/></el-icon>
            </span>
            <span style="color: grey" v-if="post.data.user.gender === null">
              <el-icon><Hide /></el-icon>
            </span>
          </el-text><br>
          <el-text type="info">
            {{post.data.user.email}}
          </el-text>
          <el-divider style="margin: 5px 0"></el-divider>
          <div class="desc" type="info" style="text-align: left;font-size: 14px;margin: 0 5px" >
            <div>微信：{{post.data.user.wx || '***'}}</div>
            <div>QQ：{{post.data.user.qq || '***'}}</div>
            <div>手机：{{post.data.user.phone || '***'}}</div>
          </div>
          <el-divider style="margin: 5px 0"></el-divider>
          <el-text type="warning" style="margin: 0 5px;opacity: 0.6">{{post.data.user.desc}}</el-text>
        </div>
        <div style="text-align: right;margin-top: 15px;">
          <el-icon style="transform: translateY(2px)"><Compass /></el-icon>
          <el-text style="margin-left: 3px;opacity: 0.7" type="success">{{new Date(post.data.createTime).toLocaleString()}}</el-text>
        </div>
      </div>
      <div class="post-main-right">
        <div class="post-content" v-html="content"></div>
        <div style="text-align: right;margin-top: 30px">
<!--          <interact-button name="点赞">-->
<!--            <el-icon><CircleCheck/></el-icon>-->
<!--          </interact-button>-->
<!--          <interact-button name="收藏">-->
<!--            <el-icon><Star/></el-icon>-->
<!--          </interact-button>-->
          <el-button @click="clickInteract('like','点赞')"
                     :type="interact.like ? 'success' : 'info'"
                     plain round :icon="Pointer">{{interact.like ? '已点赞':'点赞'}}</el-button>
          <el-button @click="clickInteract('collect','收藏')"
                     :type="interact.collect ? 'warning' : 'info'"
                     plain round :icon="Star">{{interact.collect ? '已收藏':'收藏'}}</el-button>
        </div>
      </div>
    </div>
    <div>

    </div>
  </div>
</template>

<style scoped>
.post-page {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 10px 0;
}
.post-main {
  display: flex;
  //flex-direction: row;
  border-radius: 7px;
  margin: 0 auto;
  background-color: var(--el-bg-color);
  width: 70%;
  min-width: 500px;
}
.post-main-left {
  //display: flex;
  gap: 10px;
  //flex-direction: column;
  //align-items: center;
  width: 25%;
  padding: 10px;
  text-align: center;
  border-right: solid 1px var(--el-border-color);
}
.post-main-right {
  padding: 10px 20px;
  width: 75%;
}
.post-content{
  font-size: 14px;
  line-height: 22px;
  opacity: 0.8;
}
.desc{
  font-size: 13px;
  color: grey;
}
:deep(.ql-image){
  max-width: 100%;
}
</style>
