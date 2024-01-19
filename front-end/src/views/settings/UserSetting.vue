<script setup>

import Card from "@/components/Card.vue";
import { Refresh, Select, User } from "@element-plus/icons-vue";
import {ref} from "vue";
import { Message } from "@element-plus/icons-vue";
import { useStore } from "@/store";
import { computed, reactive } from "vue";
import {get, post} from "@/net";
import {ElMessage} from "element-plus";


const store = useStore()
const registerTime = computed(() =>  new Date(store.user.registerTime).toLocaleString() )
const TodayBetweenRegister = computed(() => {
  const registerDate = new Date(store.user.registerTime);
  const currentDate = new Date();
  const diffTime = Math.abs(currentDate - registerDate);
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
})
const desc = ref()
const validateUsername = (rule, value, callback) =>{
  if(value === '') {
    callback(new Error('请输入用户名'))
  }else if (!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)){
    callback(new Error("用户名不能包含特殊字符"))
  }else {
    callback()
  }
}

const baseForm = reactive({
  username:'',
  gender:0,
  phone:'',
  qq:'',
  wx:'',
  desc:''
})
const emailForm = reactive({
  email:'',
  code:'',
})

const baseFormRef = ref()
const emailFormRef = ref()

const rules = {
  username:[
    {validator: validateUsername,trigger:['blur','change']},
    {min: 3, max: 15, message: '用户名长度在3-15个字符之间', trigger:['blur','change']},
  ],
  email: [
    {required: true, message: '请输入邮箱地址'},
    {type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change']},
  ],
  code:[
    {required: true, message: '请输入验证码'},
    {min:6, max:6, message: '请输入长度为6的验证码', trigger: ['blur', 'change']},
  ],
  //phone电话正则表达式验证
  phone:[
    {pattern: /^1[3456789]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur'},
    {required: true, message: '请输入手机号', trigger: 'blur'},
  ]
}

// const codeRules ={
//   code:[
//     {required: true, message: '请输入验证码'},
//     {min:6, max:6, message: '请输入长度为6的验证码', trigger: ['blur', 'change']},
//   ],
// }
const loading = reactive({
  form: true,
  base: false,
  baseContent:false
})
function saveDetails(){
  baseFormRef.value.validate((isValid)=>{
    if (isValid){
      loading.base = true
      loading.baseContent = true
      post('api/user/save-details',baseForm,()=>{
        ElMessage.success('用户信息保存成功')
        store.user.username = baseForm.username
        desc.value = baseForm.desc
        loading.base = false
        loading.baseContent = false
      },()=>{
        ElMessage.error('用户信息保存失败')
        loading.base = false
        loading.baseContent = false
      })
    }
    else {
      ElMessage.error('请按规则填入信息！')
    }
  })
}
get('api/user/details',(data)=>{
  console.log(data)
  baseForm.username = store.user.username
  emailForm.email = store.user.email
  // baseForm.username = data.username
  baseForm.gender = data.gender
  baseForm.phone = data.phone
  baseForm.qq = data.qq
  baseForm.wx = data.wx
  baseForm.desc = desc.value = data.desc
  loading.form = false
})

const codeColdTime = ref(0)
function sendModifyEmail(){
  emailFormRef.value.validateField('email',(isValid)=>{
    if (isValid){
      codeColdTime.value = 60;
      get(`api/auth/ask-code?email=${emailForm.email}&type=modify`,()=>{
        ElMessage.success(`验证码已发送到：${emailForm.email}，请注意查收`)
        const handle = setInterval(()=>{
          codeColdTime.value--
          if (codeColdTime <= 0){
            clearInterval(handle)
          }
        },1000)
      },()=>{
        ElMessage.error('验证码发送失败，请重试')
        codeColdTime.value = 0
      })
    }else {
      ElMessage.error('请填入合法的email！')
    }
  })
}
function submitCode(){
  //校验电子邮件
  emailFormRef.value.validateField('email',(isValid)=>{
    if (isValid){
      if (emailForm.code.length !== 6){
        ElMessage.error('请输入长度为6的验证码')
        return
      }
      post('api/user/modify-email',emailForm,()=>{
        ElMessage.success('邮箱修改成功')
        store.user.email = emailForm.email
        emailForm.code = ''
      })
    }else {
      ElMessage.error('请填入合法的email！')
    }
  })

}
</script>

<template>
  <div style="display: flex;max-width: 1000px;margin: 0 auto">
    <div class="settings-left">
      <Card :icon="User" title="账号信息设置" desc="编辑您的个人信息，您可以在隐私设置中选择是否展示某些信息" v-loading="loading.baseContent || loading.form" element-loading-text="正在处理，请稍后">
        <el-form  :model="baseForm" :rules="rules" ref="baseFormRef" label-position="top" style="margin: 0 10px 10px 10px">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="baseForm.username" maxlength="10"></el-input>
          </el-form-item>
          <el-form-item label="性别" prop="gender">
            <el-radio-group v-model="baseForm.gender">
              <el-radio :label="0">男</el-radio>
              <el-radio :label="1">女</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="手机号"  prop="phone">
            <el-input v-model="baseForm.phone" maxlength="11"></el-input>
          </el-form-item>
          <el-form-item label="QQ" prop="qq">
            <el-input v-model="baseForm.qq" maxlength="13"></el-input>
          </el-form-item>
          <el-form-item label="微信" prop="wx">
            <el-input v-model="baseForm.wx" maxlength="20"></el-input>
          </el-form-item>
          <el-form-item label="个人简介" prop="desc">
            <el-input v-model="baseForm.desc" type="textarea" maxlength="200" :rows="6"></el-input>
          </el-form-item>
          <div>
            <el-button type="success" @click="saveDetails" :icon="Select" :loading="loading.base">保存用户信息</el-button>
          </div>
        </el-form>
      </Card>
      <Card style="margin-top: 20px" :icon="Message" title="email设置" desc="在这里修改您的电子邮件地址">
        <el-form :model="emailForm" :rules="rules" ref="emailFormRef" label-position="top" style="margin: 0 10px 10px 10px">
          <el-form-item label="电子邮件" prop="email">
            <el-input v-model="emailForm.email">{{emailForm.email}}</el-input>
          </el-form-item>
          <el-form-item prop="code">
            <el-row style="width: 100%;" :gutter="10">
              <el-col :span="18">
                <el-input v-model="emailForm.code" placeholder="请获取验证码"></el-input>
              </el-col>
              <el-col :span="6">
                <el-button type="success" :disabled="codeColdTime > 0" @click="sendModifyEmail" plain style="width: 100%;">
                  {{codeColdTime > 0 ? `${codeColdTime}秒后重新获取` : '获取验证码'}}
                </el-button>
              </el-col>
            </el-row>
          </el-form-item>
          <div>
            <el-button type="success" @click="submitCode" :icon="Refresh">更新电子邮件</el-button>
          </div>
        </el-form>
      </Card>
    </div>
    <div class="settings-right">
      <div style="position: sticky;top: 20px;">
        <Card>
          <div style="text-align: center; padding: 5px 15px 0 15px;">
            <el-avatar :size="70" src="https://i0.imgs.ovh/2023/12/10/fzBHK.md.jpeg"></el-avatar>
            <div style="font-weight: bold;font-size: large;margin-top: 10px;">
              你好,{{ store.user.username }}
            </div>
            <el-divider style="margin: 10px 0;" />
            <div style="color: gray;padding: 10px; font-size: 14px;">
              {{desc || '这个人很懒，什么也没有留下' }}
            </div>
          </div>
        </Card>
        <Card style="margin-top: 20px;font-size: 14px;">
          <div style="font-size: 16px;">
            注册时间: {{ registerTime }}
          </div>
          <div style="color: grey;">
            欢迎加入本论坛！您已来到这里{{ TodayBetweenRegister }}天
          </div>
        </Card>
      </div>
    </div>

  </div>
</template>

<style scoped>
.settings-left {
  flex: 1;
  margin: 20px;
}

.settings-right {
  width: 30%;
  margin: 20px 30px 20px 0;
}
</style>
