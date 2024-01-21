<script setup>

import LightCard from "@/components/LightCard.vue";
import {Calendar, CollectionTag} from "@element-plus/icons-vue";
import Weather from "@/components/Weather.vue";
import {computed} from "vue";

import { ref, onMounted, onUnmounted } from 'vue';
import axios from "axios";
import {useStore} from "@/store";
const store = useStore()

let currentTime = ref(new Date().toLocaleString());
let intervalId;
onMounted(() => {
  intervalId = setInterval(() => {
    currentTime.value = new Date().toLocaleString();
  }, 1000);
});
onUnmounted(() => {
  clearInterval(intervalId);
});

let quote = ref('');
onMounted(async () => {
  const response = await axios.get('https://api.vvhan.com/api/ian');
  quote.value = response.data;
});

// let WeatherImg = ref('');
//
// onMounted(async () => {
//   const response = await axios.get(`https://api.vvhan.com/api/ip?tip=Hello${store.user.username}%20欢迎来到论坛！`, { responseType: 'arraybuffer' });
//   const base64 = btoa(
//       new Uint8Array(response.data).reduce(
//           (data, byte) => data + String.fromCharCode(byte),
//           '',
//       ),
//   );
//   WeatherImg.value = `data:${response.headers['content-type'].toLowerCase()};base64,${base64}`;
// });

const WeatherImageUrl = computed(() => `https://api.vvhan.com/api/ip?tip=Hello  ${encodeURIComponent(store.user.username)}%20欢迎来到论坛！`);

</script>

<template>
  <div style="display: flex;margin: 20px auto;gap: 20px;max-width: 1000px;">
    <div style="flex: 1;height: 2000px">
      <LightCard>

      </LightCard>
    </div>
    <div style="width: 300px;">
      <div style="position: sticky;top: 20px">
        <LightCard>
          <el-icon>
            <CollectionTag/>
          </el-icon>
          <el-text style="font-weight: bold;font-size: 18px" class="mx-1" type="primary">
            论坛公告
          </el-text>
          <el-divider style="margin: 8px 0"></el-divider>
          <el-text style="font-size: 14px;margin:10px;" class="mx-1" type="info">
            fasdfasdfsadfasdfsdagfigadsfgjasdgfkjasdgfkasdfgsa
            寒潮天气不断深入我国，中东部地区气温继续走低，并伴有大风天气，中央气象台目前已发布暴雪蓝色、寒潮黄色、大风蓝色预警。预计21至22日，内蒙古西部、东北地区中东部、西北地区东部、华北大部、黄淮东部、江淮大部、江南中东部和南部、西南地区南部、华南大部等地将先后降温6至8℃，其中，内蒙古中部及河套地区、东北地区东部、陕西北部、山西中北部等地降温可达10至14℃。此外，我国东部沿海等地的部分地区将有5至6级、阵风7至8级大风，东部和南部海域将有6至8级大风，阵风9至10级
          </el-text>
        </LightCard>
        <el-collapse-transition>
          <LightCard style="margin-top: 10px">
            <el-icon><Calendar/></el-icon>
            <el-text style="font-weight: bold;font-size: 18px" class="mx-1" type="warning">
              天气信息
            </el-text>
            <el-divider style="margin: 8px 0"></el-divider>
            <Weather/>
            <el-image :src="WeatherImageUrl"></el-image>
          </LightCard>
          <LightCard  style="margin-top: 10px">
            <el-text size="large">
              <div>{{currentTime}}</div>
              <div>{{quote}}</div>
            </el-text>
          </LightCard>
        </el-collapse-transition>

      </div>
    </div>
  </div>
</template>

<style scoped>

</style>
