<script setup>
import "@vueup/vue-quill/dist/vue-quill.snow.css";
import {Delta, Quill, QuillEditor} from "@vueup/vue-quill";
import {ref} from "vue";
import {post} from "@/net";
import {ElMessage} from "element-plus";

const props = defineProps({
  show: Boolean,
  pid: String,
  quote: Number
})
const emit = defineEmits(['close'])

const content = ref()

function submitComment() {
  post('/api/forum/add-comment', {
    pid: props.pid,
    quote: props.quote,
    content: JSON.stringify(content.value)
  },()=>{
    ElMessage.success("发表评论成功！")
    emit('close')
  })
}
</script>

<template>
  <div>
    <el-drawer :model-value="show" title="发表评论"
               @close="emit('close')"
               direction="btt" size="270"
               :close-on-click-modal="false"
    >
      <div>
        <div>
          <quill-editor style="height: 120px" v-model:content="content"
                        placeholder="请输入评论内容，友善交流"/>
        </div>
        <div style="margin-top: 10px;text-align: right">
          <el-button plain type="primary" @click="submitComment">发表评论</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<style scoped>
:deep(.el-drawer) {
  margin: 20px auto;
  width: 60%;
  border-radius: 10px
}

:deep(.el-drawer__header) {
  margin: 0;
}

:deep(.el-drawer__body) {
  padding: 10px;
}
</style>
