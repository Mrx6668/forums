<script setup>
defineProps(
    {
      icon: Object,
      title:String,
      desc:String
    }
)
</script>

<template>
  <div class="card">
    <div class="card-header" v-if="title">
      <div class="card-title">
        <el-icon style="margin-right: 4px;transform: translate(0,2.5px);">
          <component :is="icon"/>
        </el-icon>
        {{title}}
      </div>
      <div class="card-content">
        {{ desc }}
      </div>
    </div>
    <slot/>
  </div>
</template>

<style scoped>
.card {
  border-radius: 5px;
  border: solid 1px var(--el-border-color);
  background-color: var(--el-bg-color);
  box-sizing: border-box;
  min-height: 20px;
  padding: 10px;
}

.card-header {
  border-bottom: solid 1px var(--el-border-color);
  padding-bottom: 5px;
  margin-bottom: 10px;


}

.card-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 5px;
}

.card-content {
  font-size: 13px;
  color: grey;
}
</style>
