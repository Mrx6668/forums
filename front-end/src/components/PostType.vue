<script setup>
import {useStore} from "@/store";

const store = useStore()
defineProps({
  type: Number,
})
</script>

<template>
  <el-text class="post-type" :style="{
              color:store.findTypeById(type)?.color + 'EE',
              'border-color' : store.findTypeById(type)?.color + '77',
              'background-color' : store.findTypeById(type)?.color + '22'}">
    {{ store.findTypeById(type)?.title }}
  </el-text>
</template>

<style scoped>
.post-type {
  display: inline-block;
  border: solid 0.1px grey;
  border-radius: 5px;
  font-size: 12px;
  padding: 1px 5px;
  vertical-align: middle; /* 垂直居中 */
  transform: translateY(-1.6px);
}
</style>
