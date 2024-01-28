<script setup>
import {get, post} from "@/net"
import {ref} from "vue";
import LightCard from "@/components/LightCard.vue";
import router from "@/router";
import PostType from "@/components/PostType.vue";
import {ElMessage} from "element-plus";

defineProps({
  show: Boolean
})
const loading = ref(true)
const emit = defineEmits(['close'])
const list = ref([])

function init() {
  get('api/forum/collects', (data) => {
    list.value = data
    loading.value = false
  })
}

function deleteCollect(pid,index) {
  console.log("pid:" + pid)
  post(`api/forum/remove-collect/${pid}`, null, () => {
    // init()
    list.value.splice(index, 1)
    ElMessage.success('删除成功')
  })
}
</script>

<template>
  <div>
    <el-drawer :model-value="show" @close="emit('close')" @open="init()" title="我的收藏列表">
      <div element-loading-text="正在加载" v-loading="loading" class="collect-list">
        <LightCard v-for="item in list" class="post-card" @click="router.push(`/index/post-detail/${item.id}`)">
          <PostType :type="item.postType"/>
          <div class="title">
            <b style="font-size: 16px;" type="default">{{ item.title }}</b>
          </div>
          <el-link style="width: 50px;font-weight: bold" type="danger" @click.stop="deleteCollect(item.id,index)">删除
          </el-link>
        </LightCard>
        <el-divider style="margin: 5px 0"/>

      </div>
    </el-drawer>
  </div>
</template>

<style scoped>
.collect-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.post-card {
  background-color: rgba(128, 128, 128, 0.05);
  transition: .4s;
  display: flex;
  gap: 5px;
  justify-content: space-between;

}

.post-card:hover {
  cursor: pointer;
  scale: 1.2;
}

.title {
  margin-left: 5px;
  flex: 1;
  white-space: nowrap;
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  transition: .2s;
}

.title:hover {
  cursor: pointer;
  text-decoration: underline;
  color: var(--el-color-primary);
}
</style>
