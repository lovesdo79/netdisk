<template>
  <el-form ref="form" :model="user" :rules="rules" label-width="120px">
    <el-form-item label="用户昵称" prop="nickName">
      <el-input v-model="user.nickName" maxlength="30" placeholder="请输入您的姓名"/>
    </el-form-item>
    <el-form-item label="性别">
      <el-radio-group v-model="user.sex">
        <el-radio label="0">男</el-radio>
        <el-radio label="1">女</el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item label="年龄" prop="age">
      <el-input v-model="user.age" maxlength="50" placeholder="请输入您的年龄"/>
    </el-form-item>
    <el-form-item label="手机号码" prop="phonenumber">
      <el-input v-model="user.phonenumber" maxlength="11" placeholder="请输入手机号码"/>
    </el-form-item>
    <el-form-item label="邮箱" prop="email">
      <el-input v-model="user.email" maxlength="50" placeholder="请输入您的邮箱地址"/>
    </el-form-item>
    <el-form-item label="地址" prop="address">
      <el-input v-model="user.address" maxlength="255" placeholder="请输入您的籍贯"/>
    </el-form-item>
    <el-form-item label="密码提示问题" prop="question">
      <el-input v-model="user.question" maxlength="255" placeholder="请输入密码提示问题"/>
    </el-form-item>
    <el-form-item label="问题回答" prop="answer">
      <el-input v-model="user.answer" maxlength="255" placeholder="请输入问题回答" />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" size="mini" @click="submit">保存</el-button>
      <el-button type="danger" size="mini" @click="close">关闭</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { updateUserProfile } from "@/api/system/user";

export default {
  props: {
    user: {
      type: Object
    }
  },
  data() {
    return {
      // 表单校验
      rules: {
        nickName: [
          { required: true, message: "用户昵称不能为空", trigger: "blur" }
        ],
        email: [
          { required: true, message: "邮箱地址不能为空", trigger: "blur" },
          {
            type: "email",
            message: "请输入正确的邮箱地址",
            trigger: ["blur", "change"]
          }
        ],
        phonenumber: [
          { required: true, message: "手机号码不能为空", trigger: "blur" },
          {
            pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
            message: "请输入正确的手机号码",
            trigger: "blur"
          }
        ],
        question: [
          { required: true, message: "密码提示问题不能为空", trigger: "blur" }
        ],
        answer: [
          { required: true, message: "问题答案不能为空", trigger: "blur" }
        ]
      }
    };
  },
  methods: {
    submit() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          updateUserProfile(this.user).then(response => {
            this.$modal.msgSuccess("修改成功");
          });
        }
      });
    },
    close() {
      this.$tab.closePage();
    }
  }
};
</script>
