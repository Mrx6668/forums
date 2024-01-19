<script setup>

import Card from "@/components/Card.vue";
import {EditPen, Hide, Lock, Setting, Switch} from "@element-plus/icons-vue";
import {reactive,ref,watch} from "vue";
import {ElMessage} from "element-plus";
import {logout, post,get} from "@/net";
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

const privacyLoading = ref(true)
const privacy = reactive({
  phone:true,
  wx:true,
  qq:true,
  email:true,
  gender:true
})

get('/api/user/get-privacies', (data) => {
  privacy.phone = data.phone
  privacy.wx = data.wx
  privacy.qq = data.qq
  privacy.email = data.email
  privacy.gender = data.gender
  privacyLoading.value = false
})

function savePrivacy() {
  privacyLoading.value = true
  post('api/user/save-privacies',privacy,()=>{
    ElMessage.success('隐私设置修改成功')
    privacyLoading.value = false
  })
}
const selectAll = ref(false)
function changeSelect() {
  for (const key in privacy) {
    privacy[key] = selectAll.value
  }
}

/**
 * watch函数用于监视privacy对象的所有属性。当任何一个属性的值改变时，watch函数会重新运行。
 * 在这个函数中，我们使用Object.values(privacy)来获取privacy对象的所有值，
 * 然后使用every函数来检查所有的值是否都为true。如果是，那么selectAll的值会被设置为true；
 * 否则，selectAll的值会被设置为false。这样，无论何时复选框的状态改变，selectAll的值都会立即更新，
 *
 * every方法会测试数组中的所有元素是否都满足提供的函数。如果所有元素都满足条件，那么every方法返回true；否则，返回false。
 * 在这段代码中，newValues.every(value => value)会检查newValues数组中的所有值是否都为true。这个函数value => value是一个箭头函数，它接受一个参数value，并直接返回这个参数。因此，这个箭头函数实际上就是检查value是否为true。
 * 如果newValues数组中的所有值都为true，那么newValues.every(value => value)的结果就是true，否则，结果就是false。这个结果然后被赋值给selectAll.value。
 * 总的来说，这段代码的作用是：如果newValues数组中的所有值都为true，那么selectAll.value就被设置为true；否则，就被设置为false。
 */
watch(() => Object.values(privacy), (newValues) => {
  selectAll.value = newValues.every(value => value)
}, { immediate: true })

</script>

<template>
<div style="margin: auto;max-width: 600px">
  <div style="margin-top: 20px">
    <Card v-loading="privacyLoading" :icon="Setting" title="隐私设置" desc="修改您的隐私设置">
      <el-switch v-model="selectAll" @click="changeSelect" style="display: flex;justify-content: flex-end;right: 20px" active-text="全选">

      </el-switch>
      <div  class="checkbox-list" style="display: flex;flex-direction: column;gap: 5px; padding: 0 15px 0 15px">
        <el-checkbox v-model="privacy.phone">公开展示手机号</el-checkbox>
        <el-checkbox v-model="privacy.gender">公开展示性别</el-checkbox>
        <el-checkbox v-model="privacy.email">公开展示电子邮件</el-checkbox>
        <el-checkbox v-model="privacy.wx">公开展示微信</el-checkbox>
        <el-checkbox v-model="privacy.qq">公开展示QQ</el-checkbox>
      </div>
      <div style="text-align: center;">
        <el-button type="primary"  plain @click="savePrivacy" :icon="Hide" round style="width: 30%;min-width: 100px">保存</el-button>
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
