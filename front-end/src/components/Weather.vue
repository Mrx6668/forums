<script setup>

</script>

<template>
  <div style="display: flex;justify-content: space-between;margin: 10px 20px;align-items: center">
    <div style="font-size: 45px">
      <i class="qi-100-fill"></i>
    </div>
    <el-text style="font-weight: bold;text-align: center" type="primary">
      <div style="font-size: 24px;">20℃</div>
      <div style="font-size: 15px">多云</div>
    </el-text>
    <el-text style="margin: 13px;font-weight: bold" type="info">
      <div style="font-size: 15px;color: #A3A6AD;margin-bottom: 3px">中国 河北省</div>
      <div style="font-size: 13px">定州市 西城区</div>
    </el-text>
  </div>
  <el-divider style="margin: 10px"></el-divider>
  <div style="display: grid;grid-template-columns: repeat(5, 1fr);text-align: center">
    <div v-for="item in 5">
      <div style="font-size: 13px;color: #A3A6AD">{{item}}时</div>
      <div style="font-size: 24px;margin-top: 5px"><i class="qi-100-fill"></i></div>
      <div style="font-size: 13px;margin-top: 5px;color: #A3A6AD">20℃</div>
    </div>
  </div>
</template>

<style scoped>

</style>
