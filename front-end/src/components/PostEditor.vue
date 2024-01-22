<script setup>
import {Check, Document} from "@element-plus/icons-vue";
import {reactive} from "vue";
import {QuillEditor} from "@vueup/vue-quill";
import "@vueup/vue-quill/dist/vue-quill.snow.css";


defineProps({
  show: Boolean
})
const editor = reactive({
  type: null,
  title: '',
  content: ''
})
const types = [
  {id: 1, name: '日常闲聊', desc: '在这里分享你的个人日常'},
  {id: 2, name: '真诚交友', desc: '我想要交朋友'},
  {id: 3, name: '问题反馈', desc: '感觉哪里不满意？'},
]
const emit = defineEmits(['close'])
</script>

<template>
  <div>
    <el-drawer
        style="min-height: 600px" :model-value="show" size="70%" direction="btt"
        @close="emit('close')" :close-on-click-modal="false">
      <template #header>
        <div>
          <el-text style="font-size: 20px;font-weight: bold" type="primary">发布新的帖子</el-text>
          <br>
          <el-text type="info">请遵守相关规定，文明发帖</el-text>
        </div>
      </template>
      <div style="display: flex;gap: 10px">
        <div style="width: 150px;">
          <el-select v-model="editor.type" placeholder="请选择主题/类型">
            <el-option v-for="item in types" :value="item.id" :label="item.name">
            </el-option>
          </el-select>
        </div>
        <div style="flex: 1">
          <el-input v-model="editor.title" :prefix-icon="Document" placeholder="请输入帖子标题"></el-input>
        </div>
      </div>
      <div style="margin-top: 20px;height: 80%;overflow: hidden">
        <quill-editor style="height: calc(100% - 45px)" v-model:content="editor.content" placeholder="今天心情怎么样"/>
      </div>
      <div style="display: flex;justify-content: space-between;margin-top: 5px">
        <el-text type="info" >当前字数200（最大支持20000字）</el-text>
        <div>
          <el-button :icon="Check" type="primary">立即发布</el-button>
        </div>
      </div>

    </el-drawer>
  </div>
</template>

<style scoped>
:deep(.el-drawer) {
  margin: auto;
  width: 60%;
  border-radius: 10px 10px 0 0;

}

:deep(.el-drawer__header) {
  margin: 0;
}

:deep(.ql-toolbar) {
  border-radius: 5px 5px 0 0;
  border-color: var(--el-border-color);
}

:deep(.ql-container) {
  border-radius: 5px 5px 0 0;
  border-color: var(--el-border-color);
}
:deep(.ql-editor) {
  font-size: 14px;
}
:deep(.ql-editor.ql-blank::before) {
  color: var(--el-text-color-placeholder);
  //font-style: normal;
}
</style>
