<script setup>
import {ref} from "vue";
import {useRoute} from "vue-router";
import {get} from "@/net";

const route = useRoute();
const pid = route.params.pid
const detail = ref({})
get(`api/forum/post?pid=${pid}`,(data)=>{
  console.log("post detail: "+data)
  detail.value = data
})
</script>

<template>
  <div>
    {{detail}}
  </div>
</template>

<style scoped>

</style>
