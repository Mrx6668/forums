<script setup>
import {Check, Document, EditPen} from "@element-plus/icons-vue";
import {reactive,computed,ref} from "vue";
import {Delta, Quill, QuillEditor} from "@vueup/vue-quill";
import "@vueup/vue-quill/dist/vue-quill.snow.css";
import ImageResize from "quill-image-resize-vue";
import {ImageExtend,QuillWatch} from "quill-image-super-solution-module"
import axios from "axios";
import {accessHeader, get, post} from "@/net";
import {ElMessage} from "element-plus";
import ColorDot from "@/components/ColorDot.vue";
import {useStore} from "@/store";
const store = useStore()
// const postList = ref(null)
Quill.register("modules/ImageExtend", ImageExtend);
Quill.register("modules/imageResize", ImageResize);
const props = defineProps({
  show: Boolean,
  defaultShowTitle:{
    type:String,
    default:'发布新的帖子'
  },
  defaultTitle:{
    default:'',
    type:String
  },
  defaultText:{
    default:"",
    type:String
  },
  defaultType:{
    default:null,
    type:Number
  },
  submitButton:{
    type:String,
    default:'立即发布'
  },
  // submitUrl:String,
  submitMethod:{
    type:Function,
    default:(editor,success)=>{
      post('api/forum/create-post',{
        type: editor.type.id,
        title: editor.title,
        content: editor.content
      },()=>{
        ElMessage.success("发布成功！")
        success()
      })
    }
  }
})
const editor = reactive({
  type: null,
  title: '',
  content: '',
  uploading: false,
  types: [],
})
// const types = [
//   {id: 1, name: '日常闲聊', desc: '在这里分享你的个人日常'},
//   {id: 2, name: '真诚交友', desc: '我想要交朋友'},
//   {id: 3, name: '问题反馈', desc: '感觉哪里不满意？'},
// ]


const emit = defineEmits(['close','createSuccess'])
const editorPotion = {
  modules:{
    toolbar:{
      container:[
        [{ header: [1, 2, 3, 4, 5, 6] }], // 标题
        [{ header: 1 }, { header: 2 }], // 1、2 级标题
        [{ size: ['12', '14', '16', '18', '20', '22', '24', '28', '32', '36'] }], // 字体大小
        [{ color: [] }, { background: [] }], // 字体颜色、字体背景颜色
        ['blockquote', 'code-block'], // 引用  代码块
        // [{ indent: '-1' }, { indent: '+1' }], // 缩进
        // [{ direction: 'rtl' }], // 文本方向
        ['bold', 'italic', 'underline', 'strike'], // 加粗 斜体 下划线 删除线
        [{ list: 'ordered' }, { list: 'bullet' }], // 有序、无序列表
        [{ script: 'sub' }, { script: 'super' }], // 上标/下标
          // "link",
        // [{ font: ['songti'] }], // 字体种类
        [{ align: [] }], // 对齐方式
        ['clean'], // 清除文本格式
        ['image','link'] // 链接、图片、视频
        // ['bold', 'italic', 'underline', 'strike'],
      ],
      handlers: {
        'image' : function (){
          QuillWatch.emit(this.quill.id)
        }
      }
    },
    imageResize: {
      modules: ['Resize', 'DisplaySize']
    },
    ImageExtend: {
      action:axios.defaults.baseURL + '/api/image/cache',
      name:'file',
      size:5,
      loading:true,
      accept:'image/*',
      response:(resp)=>{
        if (resp.data){
          return axios.defaults.baseURL + '/api/image/get?imageName=' + resp.data
        }else {
          if (resp.data.code !== 200){
            ElMessage.error(resp.data.message)
          }
          return null
        }
      },
      methods:'POST',
      headers: xhr =>{
        xhr.setRequestHeader('Authorization',accessHeader().Authorization)
      },
      start:()=>editor.uploading = true,
      success:()=>{
        ElMessage.success("图片上传成功！")
        editor.uploading = false
      },
      error:(data)=>{
        ElMessage.error("操作过于频繁，图片上传失败，请稍后再试！")
        editor.uploading = false
      }
    }
  }
}

function submitPost() {
  // console.info("editor-content-delta：",editor.content)
  // console.info("editor-content-text：",deltaToText(editor.content))
  const text = deltaToText(editor.content)
  if (!editor.title) {
    ElMessage.error("帖子标题不能为空！")
    return
  }
  if (text.length > 20000){
    ElMessage.error("帖子内容不能超过 20000 个字符！")
    return
  }
  if (!editor.type){
    ElMessage.error("帖子类型不能为空！")
    return
  }
  // post('api/forum/create-post',{
  //   type: editor.type.id,
  //   title: editor.title,
  //   content: editor.content
  // },()=>{
  //   ElMessage.success("发布成功！")
  //   emit('createSuccess')
  // })
  // props.submitMethod()
  props.submitMethod(editor,success=>{
    emit('createSuccess')
  })
}
function deltaToText(delta){
  if (!delta) return '';
  let str = '';
  delta.ops.forEach(op=>{
    str += op.insert
  })
  // return str
  return str.replace(/\s/g,"")
}
const contentLength = computed(()=>{
  return deltaToText(editor.content).length
})
const refEditor = ref()
function initEditor(){
  if (props.defaultText)
    editor.content = new Delta(JSON.parse(props.defaultText))
  else
    refEditor.value.setContents('','user')
  editor.title = props.defaultTitle
  editor.type = findTypeById(props.defaultType)
  // editor.content = ''
}

function findTypeById(id){
  for (let type of store.forum.types){
    if (type.id === id)
      return type
  }
}

const aiLoading = ref(false)

function AIGenerateTitle(){
  let content = deltaToText(editor.content)
  if (content.length < 5 || content.length > 5000 ){
    ElMessage.error ("使用该功能：帖子字数不能小于5 或 超过5000！")
    return
  }
  aiLoading.value = true
  post('/api/ai/title',{content:content},(data)=>{
    ElMessage.success("AI 生成标题：" + data)
    editor.title = data.replaceAll('"', "")
    aiLoading.value = false
  },()=>{
    ElMessage.error("请求过于频繁，请稍后再试")
    aiLoading.value = false
  })
}

</script>

<template>
  <div>
    <el-drawer
        @open="initEditor"
        style="min-height: 600px" :model-value="show" size="75%" direction="btt"
        @close="emit('close')" :close-on-click-modal="false">
      <template #header>
        <div>
          <el-text style="font-size: 20px;font-weight: bold" type="primary">{{props.defaultShowTitle}}</el-text>
          <br>
          <el-text type="info">请遵守相关规定，文明发帖</el-text>
        </div>
      </template>
      <div style="display: flex;gap: 10px">
        <div style="width: 150px;">
          <el-select v-model="editor.type" value-key="id" placeholder="请选择主题/类型" :disabled="!store.forum.types.length">
            <el-option v-for="item in store.forum.types.filter(type => type.id > 0)" :value="item" :label="item.title">
              <div>
                <ColorDot :color="item.color"></ColorDot>
                <span style="margin-left: 5px">{{item.title}}</span>
              </div>
            </el-option>
          </el-select>
        </div>
        <div style="flex: 1">
          <el-input v-model="editor.title" max-length="50" :prefix-icon="Document" placeholder="请输入帖子标题"
            v-loading="aiLoading" element-loading-text="loading..." maxlength="50" minlength="1"
          ></el-input>
        </div>
      </div>
      <div style="margin-top: 10px;display: flex;justify-content: space-between">
        <el-text :style="{color: editor.type ? editor.type.color : 'gray'}" type="info">
          <ColorDot :color="editor.type ? editor.type.color : 'gray'"></ColorDot>
          {{editor.type ? editor.type.desc : '请选择一个类别'}}
        </el-text>
        <el-button :loading="aiLoading" element-loading-text="正在生成" type="warning" :icon="EditPen" plain @click="AIGenerateTitle">AI总结-生成标题</el-button>
      </div>
      <div style="margin-top: 10px;height: 80%;overflow: hidden;border-radius: 5px"  v-loading="editor.uploading" element-loading-text="上传中，稍安勿躁">
        <quill-editor style="height: calc(100% - 45px)" v-model:content="editor.content" placeholder="今天心情怎么样"
                      content-type="delta" ref="refEditor"
          :options="editorPotion"
        />
      </div>
      <div style="display: flex;justify-content: space-between;margin-top: 5px">
        <el-text type="info" >当前字数{{contentLength}}（最大支持20000字）</el-text>
        <div>
          <el-button :icon="Check" @click="submitPost" plain type="primary">{{ submitButton }}</el-button>
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




</style>
