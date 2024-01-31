<script setup>

import {get} from "@/net";
import {useStore} from "@/store";
const store = useStore()
get('api/forum/types',(data)=>{
  // console.log("editor.types:",editor.types)
  const array = []
  array.push({id:0,title:'全部',color:'linear-gradient(45deg,white,red,orange,gold,green,blue)'})
  data.forEach(d=>array.push(d))
  store.forum.types = array
  // console.log("types:",store.forum.types)
})
</script>

<template>
  <router-view v-slot="{Component}">
    <transition name="el-fade-in-linear" mode="out-in">
      <keep-alive include="Posts">
        <component :is="Component" style="height: 100%"/>
      </keep-alive>
    </transition>
  </router-view>
  <el-backtop target=".main-content-page .el-scrollbar__wrap" :right="30" :bottom="90"/>
</template>
