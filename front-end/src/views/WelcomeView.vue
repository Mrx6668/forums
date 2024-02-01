<script setup>

</script>

<template>
  <div style="width: 100vw; height: 100vh;overflow: hidden;display: flex">
    <div style="flex: 1;background-color: black">
      <el-image style="width: 100%;height: 100%;" fit="cover" src="src/assets/welcome.jpeg"/>
    </div>
    <div class="welcome-title">
      <div style="font-size: 32px;font-weight: bold;letter-spacing: 2px">欢迎登录本社区</div>
      <div style="margin-top: 15px;letter-spacing: 1px">design by</div>
      <div style="margin-top: 5px;letter-spacing: 1px">vue3 + element-ui + Java SpringBoot + jwt</div>
    </div>
    <div class="right-card">
      <router-view v-slot="{Component}">
        <transition name="el-fade-in-linear"  mode="out-in">
          <component :is="Component"/>
        </transition>
      </router-view>
    </div>
<!--    <router-view/>-->
  </div>


</template>

<style scoped>
  .right-card{
    width: 450px;
    z-index: 1;
    background-color: var(--el-bg-color);
  }
  .welcome-title{
    position: absolute;
    bottom: 30px;
    left: 30px;
    color: white;
    text-shadow: 0 0 10px black;
    //line-height: 30px;
  }
</style>
