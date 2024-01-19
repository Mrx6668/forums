<script setup>
import {computed, reactive} from "vue";
import {EditPen, Lock, Message, User} from "@element-plus/icons-vue";
import router from "@/router";
import {ElMessage} from "element-plus";
import {get, post} from "@/net";
import {ref} from "vue";

const form = reactive({
  username: '',
  password: '',
  password_repeat: '',
  email: '',
  code: ''
})

let timer = null; // 用于存储定时器ID

const  codeTime = ref(0)
const formRef = ref()

const validateUsername = (rule, value, callback) =>{
  if(value === '') {
    callback(new Error('请输入用户名'))
  }else if (!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)){
    callback(new Error("用户名不能包含特殊字符"))
  }else {
    callback()
  }
}

const validatePassword = (rule, value, callback) =>{
  if(value === '') {
    callback(new Error('请再次输入密码'))
  }else if (value !== form.password){
    callback(new Error('两次输入密码不一致'))
  }else {
    callback()
  }
}

const rule = {
  username:[
    {validator: validateUsername,trigger:['blur','change']},
    {min: 3, max: 15, message: '用户名长度在3-15个字符之间', trigger:['blur','change']},
  ],
  password: [
    {required: true, message: '请输入密码'},
    {min: 6, max: 20, message: '密码长度在6-20个字符之间', trigger: ['blur', 'change']},
  ],
  password_repeat:[
    {validator: validatePassword,trigger:['blur','change']},
  ],
  email: [
    {required: true, message: '请输入邮箱地址'},
    {type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change']},
  ],
  code:[
    {required: true, message: '请输入验证码'},
    {min:6, max:6, message: '请输入长度为6的验证码', trigger: ['blur', 'change']},
  ]
}

function askCode(){
  if (!isEmailValid){
    ElMessage.error('邮箱格式不正确');
  }else {
    codeTime.value = 60
    get(`/api/auth/ask-code?email=${form.email}&type=register`,()=>{
      ElMessage.success(`已发送到${form.email},请查收`);
      // setInterval(()=> codeTime.value--,1000)
      timer = setInterval(() => {
        codeTime.value--;
        if(codeTime.value <= 0) {
          clearInterval(timer); // 当codeTime.value小于0时，停止倒计时
          codeTime.value = 0; // 将codeTime.value重置为0
        }
      }, 1000);
    },(message)=>{ElMessage.error(message);codeTime.value = 0})
  }

}


function register(){
  formRef.value.validate((valid)=>{
    if (valid){
      post('/api/auth/register',{...form},()=>{
        ElMessage.success('注册成功，请登录');
        router.push('/')
      })
    }else {
      ElMessage.error('请正确填写注册信息');
    }
  })
}
const  isEmailValid = computed(()=>{
  return /^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\.[a-zA-Z0-9-]+)*\.[a-zA-Z0-9]{2,6}$/.test(form.email)
})

// function isEmailValid(){
//   var pattern = /^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\.[a-zA-Z0-9-]+)*\.[a-zA-Z0-9]{2,6}$/
//   return pattern.test(form.email)
// }
defineExpose({
  validateUsername
})


</script>

<template>
  <div style="text-align: center;margin: 0 20px">
    <div style="margin-top: 40%;font-size: 30px;font-weight: bold">
      注册新用户
    </div>
    <div style="margin-top:30px;font-size: 15px;color:grey">欢迎来到网站，请输入相关信息注册</div>
    <div style="margin-top: 50px">
      <el-form :model="form" :rules="rule" ref="formRef">
        <el-form-item prop="username">
          <el-input v-model="form.username" size="large" maxlength="10" placeholder="用户名" type="text">
            <template #prefix>
              <el-icon>
                <User/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input v-model="form.password" size="large" maxlength="20" minlength="6" placeholder="密码" type="password">
            <template #prefix>
              <el-icon>
                <Lock/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="password_repeat">
          <el-input v-model="form.password_repeat" size="large" maxlength="20" minlength="6" placeholder="确认密码" type="password">
            <template #prefix>
              <el-icon>
                <Lock/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="email">
          <el-input v-model="form.email" size="large" maxlength="30" placeholder="邮箱地址" type="email">
            <template #prefix>
              <el-icon>
                <Message/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="code">
          <el-row :gutter="10" style="width: 100%">
            <el-col :span="17">
              <el-input v-model="form.code"  size="large" maxlength="6" placeholder="输入验证码" type="text">
                <template #prefix>
                  <el-icon>
                    <EditPen/>
                  </el-icon>
                </template>
              </el-input>
            </el-col>
<!--            <el-col :span="2"></el-col>-->
            <el-col :span="6" style=" display: flex;align-items: center;">
              <el-button @click="askCode" :disabled="codeTime || !isEmailValid" type="primary" >
                {{codeTime > 0 ? `等待${codeTime}秒` : `获取验证码`}}
              </el-button>
            </el-col>
          </el-row>
        </el-form-item>
      </el-form>

      <el-button style="margin-top: 20px;width: 50%;" @click="register" type="warning">立即注册</el-button>
      <el-divider>
        <span style="font-size: 14px;color: grey">已有账号？</span>
      </el-divider>
      <div>
        <el-button plain style="width: 50%" type="success" @click="router.push('/')">立即登录</el-button>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>
