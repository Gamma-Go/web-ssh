<template>
  <div class="first">
    <div class="btn-container" @click="handleClick">
      <img src="../assets/add.jpeg" alt="" class="pic" />
      <div class="word">新建连接</div>
    </div>
    <div>
      <!--弹窗-->
      <el-dialog title="新建连接" v-model="dialogVisible">
        <el-form :model="form" label-width="80px" ref="form">
          <el-form-item label="地址" prop="host">
            <el-input v-model="form.host"></el-input>
          </el-form-item>
          <el-form-item label="端口" prop="port">
            <el-input v-model="form.port"></el-input>
          </el-form-item>
          <el-form-item label="用户名" prop="user">
            <el-input v-model="form.user"></el-input>
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" type="password"></el-input>
          </el-form-item>
        </el-form>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="handleSubmit"> 确 定 </el-button>
        </div>
      </el-dialog>
    </div>
  </div>
</template>
<script>
import { JSEncrypt } from "jsencrypt";
export default {
  name: "homePage",
  data() {
    return {
      dialogVisible: false,
      form: {
        host: "",
        port: "",
        user: "",
        password: "",
      },
    };
  },
  methods: {
    handleClick() {
      this.dialogVisible = true;
    },
    handleSubmit() {
      const encrypt = new JSEncrypt();
      encrypt.setPublicKey(process.env.VUE_APP_SSH_CONNECT_KEY);
      const encryptStr = encrypt.encrypt(JSON.stringify(this.form));

      const { href } = this.$router.resolve({
        path: "/sshWindow",
        query: {
          params: encryptStr,
        },
      });
      window.open(href, "_blank");
      this.form = {};
      this.dialogVisible = false;
    },
  },
};
</script>
<style>
.first {
  width: 100%;
  height: 100%;
  background-color: #f5f5f5;
}
.btn-container {
  margin: 0 auto;
  vertical-align: middle;
  height: 200px;
  width: 160px;
  display: flex;
  flex-direction: column;
  cursor: pointer;
}
.pic {
  height: 160px;
  width: 160px;
}
.word {
  height: 40px;
  text-align: center;
  line-height: 40px;
}
</style>
