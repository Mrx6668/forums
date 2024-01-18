<script setup>

import Card from "@/components/Card.vue";
import { Refresh, Select, User } from "@element-plus/icons-vue";
import { Message } from "@element-plus/icons-vue";
import { useStore } from "@/store";
import { computed } from "vue";
const store = useStore()
const registerTime = computed(() =>  new Date(store.user.registerTime).toLocaleString() )
const TodayBetweenRegister = computed(() => {
  const registerDate = new Date(store.user.registerTime);
  const currentDate = new Date();
  const diffTime = Math.abs(currentDate - registerDate);
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  return diffDays;
})
</script>

<template>
  <div style="display: flex;">
    <div class="settings-left">
      <Card :icon="User" title="账号信息设置" desc="编辑您的个人信息，您可以在隐私设置中选择是否展示某些信息">
        <el-form label-position="top" style="margin: 0 10px 10px 10px">
          <el-form-item label="用户名">
            <el-input></el-input>
          </el-form-item>
          <el-form-item label="性别">
            <el-radio-group>
              <el-radio label="1">男</el-radio>
              <el-radio label="2">女</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="手机号">
            <el-input></el-input>
          </el-form-item>
          <el-form-item label="QQ">
            <el-input></el-input>
          </el-form-item>
          <el-form-item label="微信">
            <el-input></el-input>
          </el-form-item>
          <el-form-item label="个人简介">
            <el-input type="textarea" :rows="6"></el-input>
          </el-form-item>
          <div>
            <el-button type="success" :icon="Select">保存用户信息</el-button>
          </div>
        </el-form>
      </Card>
      <Card style="margin-top: 20px" :icon="Message" title="email设置" desc="在这里修改您的电子邮件地址">
        <el-form label-position="top" style="margin: 0 10px 10px 10px">
          <el-form-item label="电子邮件">
            <el-input></el-input>
          </el-form-item>
          <el-form-item>
            <el-row style="width: 100%;" :gutter="10">
              <el-col :span="18">
                <el-input placeholder="请获取验证码"></el-input>
              </el-col>
              <el-col :span="6">
                <el-button type="success" plain style="width: 100%;">获取验证码</el-button>
              </el-col>
            </el-row>
          </el-form-item>
          <div>
            <el-button type="success" :icon="Refresh">更新电子邮件</el-button>
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
              Lorem, ipsum dolor sit amet consectetur adipisicing elit. Quia corporis autem natus quaerat! A officiis,
              temporibus vero quasi accusantium voluptas voluptatum tempora inventore, nulla dolor aliquam nostrum modi
              culpa adipisci.
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
