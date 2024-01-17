<script setup>
import {reactive} from "vue";
import {User} from '@element-plus/icons-vue'
import {Lock} from '@element-plus/icons-vue'
import {login} from "@/net";
import {ref} from "vue";
import router from "@/router";

const formRef = ref()
const form = reactive({
  username: '',
  password: '',
  remember: false
})
const rule = {
  username: [
    {required: true, message: '请输入用户名'},
  ],
  password: [
    {required: true, message: '请输入密码'},
  ]
}

function userLogin() {
  console.log("userLogin")
  formRef.value.validate((valid) => {
    if (valid) {
      login(form.username, form.password, form.remember, () => {
        router.push('/index')
      })
    }
  })
}
</script>

<template>
  <div style="text-align: center;margin: 0 20px 0 20px">
    <div style="margin-top: 50%">
      <div style="font-size: 30px;font-weight: bold">登录</div>
      <div style="margin-top:30px;font-size: 15px;color:grey ">请输入用户名和密码</div>
    </div>
    <div style="margin-top: 40px">
      <el-form ref="formRef" :model="form" :rules="rule" size="large">
        <el-form-item prop="username">
          <el-input v-model="form.username" maxlength="20" placeholder="用户名/邮箱" type="text">
            <template #prefix>
              <el-icon>
                <User/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" maxlength="20" placeholder="密码" type="password">
            <template #prefix>
              <el-icon>
                <Lock/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-row>
          <el-col :span="12" style="text-align: left">
            <el-form-item prop="remember">
              <el-switch v-model="form.remember" active-text="记住我"></el-switch>
              <!--            <el-checkbox v-model="form.remember" label="记住我"></el-checkbox>-->
            </el-form-item>
          </el-col>
          <el-col :span="12" style="text-align: right">
            <el-link @click="router.push('/reset')" style="margin-top: 10px">忘记密码？</el-link>
          </el-col>
        </el-row>
      </el-form>
      <div style="margin-top: 40px">
        <el-button style="width: 50%" type="primary" @click="userLogin()">立即登录</el-button>
      </div>
      <el-divider>
        <span style="font-size: 14px;color: grey">没有账号？</span>
      </el-divider>
    </div>
    <div>
      <el-button plain style="width: 50%" type="success" @click="router.push('/register')">立即注册</el-button>
    </div>
  </div>
</template>

<style scoped>

</style>