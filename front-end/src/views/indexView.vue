<script setup>
import {logout, get} from "@/net";
import router from "@/router";
import {useStore} from "@/store";
import {ref,reactive} from "vue";
import {
  Back,
  Bell,
  ChatDotSquare, Check, Collection,
  Connection, DataLine,
  Document,
  EditPen,
  Files,
  Location, Lock, Message, Monitor, Operation,
  Position,
  School, Search, Star, User
} from "@element-plus/icons-vue";
import PostCollectList from "@/components/PostCollectList.vue";
import LightCard from "@/components/LightCard.vue";

const store = useStore()
const loading = ref(true)//加载效果
const collect = ref(false)
const searchInput = reactive({
  type:'1',
  text:''
})

get('api/user/info', (data) => {
  console.log(data);
  store.user = data
  loading.value = false
})

function userLogout() {
  logout(() => {
    router.push('/')
  });
}
const notification = ref([])

const loadNotification = ()=>{
  get('api/notification/list', (data) => {
    notification.value = data
  })
}
loadNotification()

function confirmNotification(id,url){
  get(`/api/notification/delete?id=${id}`,()=>{
    loadNotification()
    window.open(url)
  })
}

function deleteAllNotification(){
  get(`/api/notification/delete-all`,()=>{
    loadNotification()
  })
}

</script>

<template>
  <div class="main-content" v-loading="loading" element-loading-text="加载中，请稍后">
    <el-container style="height: 100vh" v-if="!loading">
      <el-header class="main-content-header">
        <el-image class="logo" src="https://element-plus.org/images/element-plus-logo.svg"></el-image>
        <div style="flex: 1;padding: 0 20px;text-align: center">
          <el-input v-model="searchInput.text"  style="width: 100%;max-width: 450px" placeholder="在此键入内容搜索...">
            <template #prefix>
              <el-icon><Search/></el-icon>
            </template>
            <template #append>
              <el-select v-model="searchInput.type" style="width: 100px" placeholder="选择分类">
                <el-option value="1" label="帖子广场"></el-option>
                <el-option value="2" label="校园活动"></el-option>
                <el-option value="3" label="表白墙"></el-option>
                <el-option value="4" label="教务通知"></el-option>
              </el-select>
            </template>
          </el-input>
        </div>
        <div class="user-info">

          <el-popover placement="bottom"
                      :width="350" trigger="hover">
            <template #reference>
              <el-badge style="margin-right: 30px" is-dot :hidden="!notification.length">
                <div class="notification">
                  <el-icon><Bell/></el-icon>
                  <div style="font-size: 13px">消息</div>
                </div>
              </el-badge>
            </template>
            <el-empty :image-size="80" description="暂无未读消息！" v-if="!notification.length"/>
            <el-scrollbar max-height="500" v-else>
              <LightCard v-for="item in notification" class="notification-item"
              @click="confirmNotification(item.id,item.url)"
              >
                <div>
                  <el-tag size="small" :type="item.type">消息</el-tag>
                  <span style="font-weight: bold;margin: auto 0 auto 3px">{{item.title}}</span>
                </div>
                <el-divider style="margin: 7px 0 3px 0"/>
                <div style="font-size: 13px;color: grey">
                  {{item.content}}
                </div>
              </LightCard>
            </el-scrollbar>
            <div style="margin-top: 20px">
              <el-button size="small" type="info" :icon="Check" plain
                         @click="deleteAllNotification" style="width: 100%"
              >清除全部未读消息</el-button>
            </div>
          </el-popover>
          <!--            style="flex: 1"  把剩余占满-->
          <div class="profile">
            <div>{{store.user.username}}</div>
            <div>{{store.user.email}}</div>
          </div>
          <el-dropdown size="large">
            <el-avatar  :src="store.avatarUrl"/>
            <template #dropdown>
              <el-dropdown-menu @click="routes.push('/index/user-setting')">
                <el-dropdown-item>
                  <el-icon><Operation/></el-icon>
                  个人设置
                </el-dropdown-item>
                <el-dropdown-item @click="collect = true">
                  <el-icon><Star/></el-icon>
                  我的收藏
                </el-dropdown-item>
                <el-dropdown-item>
                  <el-icon><Message/></el-icon>
                  我的消息
                </el-dropdown-item>
                <el-dropdown-item divided @click="userLogout">
                  <el-icon><Back/></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-container>
        <el-aside width="230px" >
          <el-scrollbar style="height: calc(100vh - 55px);">
            <el-menu
                router
                :default-active="$route.path"
                style="min-height: calc(100vh - 55px);"
                :default-openeds="['1','3']"
            >
              <el-sub-menu index="1">
                <template #title>
                  <el-icon><Location/></el-icon>
                  <span><b>校园论坛</b></span>
                </template>
                <el-menu-item index="/index/posts">
                  <template #title>
                    <el-icon><ChatDotSquare/></el-icon>
                    <b>帖子广场</b>
                  </template>
                </el-menu-item>
                <el-menu-item index="1-2">
                  <template #title>
                    <el-icon><Bell></Bell></el-icon>
                    失物招领
                  </template>
                </el-menu-item>
                <el-menu-item index="1-3">
                  <template #title>
                    <el-icon><School /></el-icon>
                    校园活动
                  </template>
                </el-menu-item>
                <el-menu-item index="1-4">
                  <template #title>
                    <el-icon><Connection /></el-icon>
                    表白墙
                  </template>
                </el-menu-item>
                <el-menu-item index="1-5">
                  <template #title>
                    <el-icon><EditPen /></el-icon>
                    某某考研
                    <el-tag style="margin-left: 8px" size="small">合作机构</el-tag>
                  </template>
                </el-menu-item>
              </el-sub-menu>
              <el-sub-menu index="2">
                <template #title>
                  <el-icon><Position/></el-icon>
                  <span><b>探索发现</b></span>
                </template>
                <el-menu-item >
                  <template #title>
                    <el-icon><Document /></el-icon>
                    成绩查询
                  </template>
                </el-menu-item>
                <el-menu-item >
                  <template #title>
                    <el-icon><Files /></el-icon>
                    课程表
                  </template>
                </el-menu-item>
                <el-menu-item >
                  <template #title>
                    <el-icon><Monitor /></el-icon>
                    教务通知
                  </template>
                </el-menu-item>
                <el-menu-item >
                  <template #title>
                    <el-icon><Collection /></el-icon>
                    在线图书馆
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><DataLine /></el-icon>
                    预约
                  </template>
                </el-menu-item>
              </el-sub-menu>
              <el-sub-menu index="3">
                <template #title>
                  <el-icon><Operation /></el-icon>
                  <b>个人信息</b>
                </template>
                <el-menu-item index="/index/user-setting">
                  <template #title>
                    <el-icon><User /></el-icon>
                    个人信息设置
                  </template>
                </el-menu-item>
                <el-menu-item index="/index/privacy-setting">
                  <template #title>
                    <el-icon><Lock /></el-icon>
                    账号安全设置
                  </template>
                </el-menu-item>

              </el-sub-menu>
            </el-menu>
          </el-scrollbar>

        </el-aside>
        <el-main class="main-content-page" >
          <el-scrollbar style="height: calc(100vh - 55px)">
<!--           <router-view> 匹配到对应的路由组件,并通过 v-slot 解构传递给 Component 变量
              将 Component变量动态绑定给 <component> 的 :is 属性
                当路由切换时,Component 变量会改变,因此 <component> 也会切换不同的组件 -->

              <router-view v-slot="{Component,route}">
                <transition name="el-fade-in-linear" mode="out-in">
<!--                  <keep-alive include="Posts">-->
                    <component :is="Component" style="height: 100%" />
<!--                  </keep-alive>-->
                </transition>
              </router-view>

          </el-scrollbar>
        </el-main>
      </el-container>
      <PostCollectList :show="collect" @close="collect = false"/>
    </el-container>
  </div>

  <!--    <div class="exit">-->
  <!--    <el-button @click="userLogout" type="danger">退出登录</el-button>-->
  <!--  </div>-->
</template>

<style lang="less" scoped>
.notification-item{
  transition: .4s;
  &:hover{
    opacity: 0.7;
    color: var(--el-color-primary);
    cursor: pointer;
  }
}
.notification{
  font-size: 22px;
  line-height: 14px;
  text-align: center;
  transition: color .3s;
  &:hover{
    color: grey;
    cursor: pointer;
  }
}
.main-content {
  height: 100vh;
  width: 100%;
  margin: 0;
}
.main-content-page{
  padding: 0;
  background-color: #f7f8fa;
}
.dark .main-content-page{
  background-color: #212225;
}
.main-content-header {
  border-bottom: solid 1px var(--el-border-color);
  height: 55px;
  display: flex;
  align-items: center;
  box-sizing: border-box;

  .logo {
    width: 120px;
  }
}

.user-info {
  display: flex;
  justify-content: flex-end;
  align-items: center;

  .el-avatar{
    transition: transform 0.3s ease-in-out;
    margin-right: 10px;
  }
  .el-avatar:hover {
    cursor: pointer;//鼠标变成手指
    transition: transform 0.3s ease-in-out;
    transform:  scale(1.2);
  }
  .profile {
    text-align: right;
    margin-right: 16px;

    :first-child {
      font-size: 18px;
      font-weight: bold;
      line-height: 20px;
    }

    :last-child {
      font-size: 10px;
      color: grey;

    }
  }
}
</style>
