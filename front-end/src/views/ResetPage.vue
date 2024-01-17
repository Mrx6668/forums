<script setup>
import {ref, reactive, computed} from "vue";
import {EditPen, Lock, Message} from "@element-plus/icons-vue";
import {ElMessage} from "element-plus";
import {get, post} from "@/net";
import routes from "@/router";

const active = ref(0)
const codeTime = ref(0)
const formRef = ref()
const form = reactive({
  email:'',
  code:'',
  password:'',
  password_repeat:''
})

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
  // username:[
  //   {validator: validateUsername,trigger:['blur','change']},
  //   {min: 3, max: 15, message: '用户名长度在3-15个字符之间', trigger:['blur','change']},
  // ],
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


let timer = null
function askCode(){
  if (!isEmailValid){
    ElMessage.error('邮箱格式不正确');
  }else {
    codeTime.value = 60
    get(`/api/auth/ask-code?email=${form.email}&type=reset`,()=>{
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

const  isEmailValid = computed(()=>{
  return /^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\.[a-zA-Z0-9-]+)*\.[a-zA-Z0-9]{2,6}$/.test(form.email)
})

function reset() {
  formRef.value.validate((valid)=>{
    if (valid){
      post('/api/auth/reset',{
        email: form.email,
        code: form.code,
      },()=>{
        ElMessage.success('信息正确，请继续');
        active.value++
      },(error)=>{ElMessage.error(`重置密码失败：${error}`);codeTime.value = 0})
    }
  })
}

function ConfirmReset(){
  formRef.value.validate((valid)=>{
    if (valid){
      post('/api/auth/reset-confirm',{
        ...form
      },()=>{
        ElMessage.success('重置成功，请登录');
        // active.value++
        routes.push("/")
      },(error)=>{ElMessage.error(`重置密码失败：${error}`);codeTime.value = 0})
    }
  })
}

</script>

<template>
  <div style="text-align: center;">
    <div style="margin-top: 15%">
      <el-steps :active="active" finish-status="success" align-center>
        <el-step title="验证电子邮件">

        </el-step>

        <el-step title="修改密码">

        </el-step>
      </el-steps>
      <div style="padding: 30% 0 30% 0">
        <div v-if="active === 0">
          <div>
            <div style="font-size: 28px;font-weight: bold;margin-top: 20px">重置密码</div>
            <div style="margin-top:16px;font-size: 15px;color:grey ">请输入对应的电子邮件地址</div>
          </div>
          <div >
            <el-form :model="form" :rules="rule" ref="formRef" style="margin: 40px 30px 0 30px">
              <el-form-item prop="email">
                <el-input v-model="form.email" size="large" type="email" placeholder="电子邮件地址">
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
            <div style="margin-top: 50px">
              <el-button @click="reset" plain style="width: 40%" type="primary">开始重置密码</el-button>
            </div>
          </div>
        </div>
        <div v-if="active===1">
          <div>
            <div style="font-size: 28px;font-weight: bold;margin-top: 20px">重置密码</div>
            <div style="margin-top:16px;font-size: 15px;color:grey ">请填写新密码，请妥善保管</div>
            <div style="margin-top: 40px">
              <el-form :model="form" :rules="rule" ref="formRef" style="margin: 40px 30px 0 30px">
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
              </el-form>

            </div>
          </div>
          <div style="margin-top: 50px">
            <el-button @click="ConfirmReset" plain style="width: 40%" type="primary">确认重置密码</el-button>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<style scoped>


</style>