<script setup>

import Card from "@/components/Card.vue";
import {EditPen, Lock, Setting, Switch} from "@element-plus/icons-vue";
import {reactive,ref} from "vue";
import {ElMessage} from "element-plus";
import {logout, post} from "@/net";
import userLogout from "../indexView.vue"
import router from "@/router";

const password = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})
const pwdRef = ref()

const validatePassword = (rule, value, callback) =>{
  if(value === '') {
    callback(new Error('请再次输入密码'))
  }else if (value !== password.newPassword){
    callback(new Error('两次输入密码不一致'))
  }else {
    callback()
  }
}
const rules = {
  oldPassword: [
    {required: true, message: '请输入密码'},
    {min: 6, max: 20, message: '密码长度在6-20个字符之间', trigger: ['blur', 'change']},
  ],
  newPassword: [
    {required: true, message: '请输入新密码'},
    {min: 6, max: 20, message: '密码长度在6-20个字符之间', trigger: ['blur', 'change']},
  ],
  confirmPassword: [
    {required: true, message: '请再次输入新密码'},
    {validator: validatePassword,trigger:['blur','change']},
  ]
}

function checkPwdRepeat() {
  if (password.newPassword === password.oldPassword) {
    ElMessage.error('新密码不能与旧密码相同')
    return true
  }
}

function changePassword(){
  pwdRef.value.validate((valid) => {
    if (valid) {
      if (checkPwdRepeat()) return
      post('/api/user/change-password', password,() =>{
        ElMessage.success('修改成功，请重新登录，请牢记您的密码')
        pwdRef.value.resetFields()
        logout(() => {
          router.push('/')
        });
      })
    }
  })

}
</script>

<template>
<div style="margin: auto;max-width: 600px">
  <div style="margin-top: 20px">
    <Card :icon="Setting" title="隐私设置" desc="修改您的隐私设置">
      <div class="checkbox-list" style="display: flex;flex-direction: column;gap: 5px; padding: 0 15px 0 15px">
        <el-checkbox>公开展示手机号</el-checkbox>
        <el-checkbox>公开展示性别</el-checkbox>
        <el-checkbox>公开展示电子邮件</el-checkbox>
        <el-checkbox>公开展示微信</el-checkbox>
        <el-checkbox>公开展示QQ</el-checkbox>
      </div>
    </Card>
    <Card :icon="Setting" style="margin-top: 20px" title="修改密码" desc="在这里修改密码，请牢记您的密码" >
      <el-form ref="pwdRef" style="padding: 0 15px 0 15px" label-width="100px" :model="password" label-position="top" :rules="rules">
        <el-form-item label="当前密码" prop="oldPassword">
          <el-input v-model="password.oldPassword" :prefix-icon="Lock" type="password" placeholder="请输入您的当前密码"></el-input>
        </el-form-item>

        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="password.newPassword" :prefix-icon="EditPen" type="password" placeholder="请输入您的新密码"></el-input>
        </el-form-item>
        <el-form-item label="重复新密码" prop="confirmPassword">
          <el-input v-model="password.confirmPassword" :prefix-icon="EditPen" type="password" placeholder="请再次输入您的新密码"></el-input>
        </el-form-item>
        <div style="text-align: center">
          <el-button type="primary" @click="changePassword" :icon="Switch" round style="width: 30%;min-width: 100px">重置密码</el-button>
        </div>

      </el-form>
    </Card>

  </div>
</div>
</template>

<style scoped>

</style>
