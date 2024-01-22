<script setup>
defineProps({
  data: Object
})
function returnHour(timeString) {
  let date = new Date(timeString);
  return date.getHours();
}
</script>

<template>
  <div style="height: 160px" v-loading="!data.success" element-loading-text="正在加载天气信息。。。">
    <div v-if="data.success" style="display: flex;justify-content: space-between;margin: 10px 20px;align-items: center">
      <div style="font-size: 45px">
        <i :class="`qi-${data.now.icon}-fill`"></i>
      </div>
      <el-text style="font-weight: bold;text-align: center" type="primary">
        <div style="font-size: 24px;">{{data.now.temp}}℃</div>
        <div style="font-size: 15px">{{data.now.text}}</div>
      </el-text>
      <el-text style="margin: 13px;font-weight: bold" type="info">
        <div style="font-size: 15px;color: #A3A6AD;margin-bottom: 3px">{{data.location.country}} {{data.location.adm1}}</div>
        <div style="font-size: 13px">{{data.location.adm2}} {{data.location.name}}区</div>
      </el-text>
    </div>
    <el-divider style="margin: 10px"></el-divider>
    <div style="display: grid;grid-template-columns: repeat(5, 1fr);text-align: center">
      <div v-for="item in data.hourly">
        <div style="font-size: 13px;color: #A3A6AD">{{ returnHour(item.fxTime) }}时</div>
        <div style="font-size: 24px;margin-top: 5px"><i :class="`qi-${item.icon}-fill`"></i></div>
        <div style="font-size: 13px;margin-top: 5px;color: #A3A6AD">{{item.temp}}℃</div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>
