<script setup>
import {ref, reactive, computed} from "vue";
import {useRoute} from "vue-router";
import {get,post as _post} from "@/net";
import axios from "axios";
import {
  ArrowLeft,
  CircleCheck,
  Compass,
  EditPen,
  Female,
  Hide,
  Male,
  Plus,
  Pointer,
  Star, Top
} from "@element-plus/icons-vue";
import {QuillDeltaToHtmlConverter} from "quill-delta-to-html";
import Card from "@/components/Card.vue";
import PostType from "@/components/PostType.vue";
import InteractButton from "@/components/InteractButton.vue";
import {ElMessage} from "element-plus";
import {useStore} from "@/store";
import PostEditor from "@/components/PostEditor.vue";
import PostCommentEditor from "@/components/PostCommentEditor.vue";

const route = useRoute();
const pid = route.params.pid
const store = useStore()
const post = reactive({
  data: null,
  comments: null,
  page:1
})

const interact = reactive({
  like: false,
  collect: false,
})

const edit = ref(false);

function getDetail() {
  get(`api/forum/post?pid=${pid}`, (data) => {
    console.log("post detail: " + data)
    post.data = data
    // 循环赋值
    for (let key in interact) {
      if (data.interact[key]) {
        interact[key] = data.interact[key]
      }
    }
    // interact.like = data.interact.like
    // interact.collect = data.interact.collect
  })
}

getDetail();
loadComments(1)
function convertToHTML(content){
  const ops = JSON.parse(content).ops
  const converter = new QuillDeltaToHtmlConverter(ops, {inlineStyle: true})
  return converter.convert()
}

function clickInteract(type, message) {
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
    } else {
      ElMessage.success(`已取消${message}`)
    }

  })
}

function updatePost(editor){
  _post(`api/forum/update-post`,{
    id:pid,
    type: editor.type.id,
    title: editor.title,
    content: editor.content
  },()=>{
    edit.value = false
    ElMessage.success("修改成功！")
    getDetail()
  })
}


const comment = reactive({
  show:false,
  text:'',
  quote: -1
})

function loadComments(page){
  post.comments = null;
  post.page = page;
  get(`api/forum/comments?pid=${pid}&page=${page-1}`,(data)=>{
    post.comments = data
  })

}
function onCommentAdd(){
  comment.show = false
  loadComments(Math.floor(++post.data.comment/10)+1)
}

</script>

<template>
  <div class="post-page" v-if="post.data">
    <div class="post-main" style="position: sticky;top: 0;z-index: 10">
      <Card style="display: flex;width: 100%">
        <el-button :icon="ArrowLeft" size="small" round plain type="info" @click="$router.go(-1)">返回列表</el-button>
        <div style="flex: 1;text-align: center;margin: auto 0" v-if="post.data?.postType">
          <PostType :type="post.data.postType"/>
          <el-text style="margin-left: 5px" size="large">{{ post.data.title }}</el-text>
        </div>
      </Card>
    </div>
    <div class="post-main">
      <div class="post-main-left">
        <el-avatar size="large"
                   :src="axios.defaults.baseURL+'/api/image/get?imageName='+post.data.user.avatar"></el-avatar>
        <div>
          <el-text type="primary" size="large" style="font-weight: bold">
            {{ post.data.user.username }}
            <span style="color: hotpink" v-if="post.data.user.gender === 1">
              <el-icon><Female/></el-icon>
            </span>
            <span style="color: dodgerblue" v-if="post.data.user.gender === 0">
              <el-icon><Male/></el-icon>
            </span>
            <span style="color: grey" v-if="post.data.user.gender === null">
              <el-icon><Hide/></el-icon>
            </span>
          </el-text>
          <br>
          <el-text type="info">
            {{ post.data.user.email }}
          </el-text>
          <el-divider style="margin: 5px 0"></el-divider>
          <div class="desc" type="info" style="text-align: left;font-size: 14px;margin: 0 5px">
            <div>微信：{{ post.data.user.wx || '***' }}</div>
            <div>QQ：{{ post.data.user.qq || '***' }}</div>
            <div>手机：{{ post.data.user.phone || '***' }}</div>
          </div>
          <el-divider style="margin: 5px 0"></el-divider>
          <el-text type="warning" style="margin: 0 5px;opacity: 0.6">{{ post.data.user.desc }}</el-text>
        </div>
      </div>
      <div class="post-main-right">
        <div class="post-content" v-html="convertToHTML(post.data.content)"></div>
        <el-divider></el-divider>
        <div style="margin-top: 30px;display: flex;justify-content: space-between">
          <div>
            <el-text style="margin-left: 3px;opacity: 0.7;" type="info">
              发帖时间：{{ new Date(post.data.createTime).toLocaleString() }}
            </el-text>
            <el-text style="margin-left: 10px;opacity: 0.7;" type="info">浏览{{post.data.views}}次</el-text>
          </div>
            <div>
              <el-button @click="edit = true"
                         type="primary" v-if="store.user.id === post.data.user.id"
                         plain round :icon="EditPen">编辑帖子
              </el-button>
              <el-button @click="clickInteract('like','点赞')"
                         :type="interact.like ? 'success' : 'info'"
                         plain round :icon="Pointer">{{ interact.like ? '已点赞' : '点赞' }}
              </el-button>
              <el-button @click="clickInteract('collect','收藏')"
                         :type="interact.collect ? 'warning' : 'info'"
                         plain round :icon="Star">{{ interact.collect ? '已收藏' : '收藏' }}
              </el-button>
            </div>
        </div>
      </div>
    </div>
    <transition name="el-fade-in-linear" mode="out-in">
      <div v-if=post.comments>
        <div class="post-main" style="margin-top: 10px" v-for="item in post.comments">
          <div class="post-main-left">
            <el-avatar size="large"
                       :src="axios.defaults.baseURL+'/api/image/get?imageName='+item.user.avatar"></el-avatar>
            <div>
              <el-text type="primary" size="large" style="font-weight: bold">
                {{ item.user.username }}
                <span style="color: hotpink" v-if="item.user.gender === 1">
              <el-icon><Female/></el-icon>
            </span>
                <span style="color: dodgerblue" v-if="item.user.gender === 0">
              <el-icon><Male/></el-icon>
            </span>
                <span style="color: grey" v-if="item.user.gender === null">
              <el-icon><Hide/></el-icon>
            </span>
              </el-text>
              <br>
              <el-text type="info">
                {{ item.user.email }}
              </el-text>
              <el-divider style="margin: 5px 0"></el-divider>
              <div class="desc" type="info" style="text-align: left;font-size: 14px;margin: 0 5px">
                <div>微信：{{ item.user.wx || '***' }}</div>
                <div>QQ：{{ item.user.qq || '***' }}</div>
                <div>手机：{{ item.user.phone || '***' }}</div>
              </div>
            </div>
          </div>
          <div class="post-main-right">
            <el-text style="margin-left: 3px;opacity: 0.7;" type="info">
              发表时间：{{ new Date(item.time).toLocaleString() }}
            </el-text>
            <div class="post-content" v-html="convertToHTML(item.content)"></div>
          </div>
        </div>
        <div style="width: fit-content;margin: 20px auto">
          <el-pagination background layout="prev,pager,next"
                         @current-change="loadComments" :total="post.data.comment"
                         :page-size="10" v-model:current-page="post.page"
                          hide-on-single-page
          />
        </div>
      </div>
    </transition>
    <PostEditor v-if="post.data && store.user.id === post.data.user.id && store.forum.types"
                :default-type="post.data.postType" :default-title="post.data.title" :default-text="post.data.content"
                submit-button="更新帖子内容" :submit-method="updatePost" default-show-title="修改当前帖子"
                :show="edit" @close="edit = false"/>
    <post-comment-editor :show="comment.show" @close="comment.show = false" :pid="pid"
                         :quote="comment.quote" @comment="onCommentAdd"/>
    <div class="add-comment"  @click="comment.show = true" style="bottom: 30px;right: 30px;">
      <el-icon><Plus/></el-icon>
    </div>

<!--    <a href="#top">-->
<!--      <div class="add-comment" style="bottom: 100px;right: 30px;">-->
<!--        <el-icon><Top /></el-icon>-->
<!--      </div>-->
<!--    </a>-->
  </div>
</template>

<style scoped>
.add-comment{
  position: fixed;
  width: 40px;
  height: 40px;
  color: var(--el-color-primary);
  text-align: center;
  line-height: 45px;
  border-radius: 50%;
  background: var(--el-bg-color-overlay);
  box-shadow: var(--el-box-shadow-lighter);
  transition: transform 0.5s ease;
}
.add-comment:hover{
  background: var(--el-border-color-extra-light);
  cursor: pointer;
  transform: rotate(360deg) scale(1.05);
}
.post-page {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 10px 0;
}

.post-main {
  display: flex;
//flex-direction: row; border-radius: 7px;
  margin: 0 auto;
  background-color: var(--el-bg-color);
  width: 70%;
  min-width: 500px;
}

.post-main-left {
//display: flex; gap: 10px;
//flex-direction: column; //align-items: center; width: 25%;
  padding: 10px;
  text-align: center;
  border-right: solid 1px var(--el-border-color);
}

.post-main-right {
  padding: 10px 20px;
  width: 75%;
}

.post-content {
  font-size: 14px;
  line-height: 22px;
  opacity: 0.8;
}

.desc {
  font-size: 13px;
  color: grey;
}

:deep(.ql-image) {
  max-width: 100%;
}
</style>
