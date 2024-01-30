<script setup>
import "@vueup/vue-quill/dist/vue-quill.snow.css";
import {Delta, QuillEditor} from "@vueup/vue-quill";
import {ref} from "vue";
import {post} from "@/net";
import {ElMessage} from "element-plus";

const props = defineProps({
  show: Boolean,
  pid: String,
  quote: Number
})
const emit = defineEmits(['close', 'comment'])

const init = () => content.value = new Delta()

const content = ref()

function submitComment() {
  if (deltaToText(content.value).length > 2000) {
    ElMessage.warning("评论最大只支持2000字，请修改后再试！")
    return
  }
  post('/api/forum/add-comment', {
    pid: props.pid,
    quote: props.quote ? props.quote.id : -1,
    content: JSON.stringify(content.value)
  }, () => {
    ElMessage.success("发表评论成功！")
    emit('comment')
  })
}

function deltaToSimpleText(delta) {
  if (delta === null || delta === undefined) return
  let str = deltaToText(JSON.parse(delta))
  if (str.length > 35) str = str.substring(0, 35) + "..."
  return str
}


function deltaToText(delta) {
  if (!delta?.ops) return '';
  let str = '';
  delta.ops.forEach(op => {
    str += op.insert
  })
  // return str
  return str.replace(/\s/g, "")
}
</script>

<template>
  <div>
    <el-drawer :model-value="show"
               :title="quote ? `发表对评论：${deltaToSimpleText(quote.content)} 的回复` : '发表帖子评论'"
               @close="emit('close')" @open="init"
               direction="btt" size="270"
               :close-on-click-modal="false"
    >
      <div>
        <div>
          <quill-editor style="height: 120px" v-model:content="content"
                        placeholder="请输入评论内容，友善交流"/>
        </div>
        <div style="margin-top: 10px;text-align: right;display: flex;justify-content: space-between">
          <div style="font-size: 13px;color: grey;margin-top: 5px">
            字数统计：{{ deltaToText(content).length }} / max = 2000
          </div>
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

:deep(.el-drawer__title) {
  color: var(--el-color-primary);
}
</style>
