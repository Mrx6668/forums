<script setup>
//返回上一页
function back() {
  window.history.go(-1)
}
</script>

<template>
  <div class="main">
    <el-col :sm="12" :lg="6">
      <el-result
          icon="warning"
          title="您的请求过于频繁，已被限流"
          sub-title="请稍后返回，并30秒后刷新重试！"
      >
        <template #extra>
          <el-button @click="back" type="primary">返回</el-button>
        </template>
      </el-result>
    </el-col>
  </div>
</template>

<style scoped>
.main{
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  width: 100%;
}
</style>
