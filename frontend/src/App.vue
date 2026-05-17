<template>
  <div id="app">
    <el-container>
      <el-header style="background: #409EFF; color: white; font-size: 20px; line-height: 60px; padding: 0 20px;">
        <i class="el-icon-s-tools" style="margin-right: 10px;"></i>
        维修师傅抢单系统
        <el-menu
          mode="horizontal"
          :default-active="$route.path"
          background-color="#409EFF"
          text-color="white"
          active-text-color="#ffd04b"
          router
          style="float: right; border: none;"
        >
          <el-menu-item index="/user">用户端</el-menu-item>
          <el-menu-item index="/worker">师傅端</el-menu-item>
          <el-menu-item index="/admin">管理端</el-menu-item>
        </el-menu>
      </el-header>
      <el-main>
        <router-view/>
      </el-main>
    </el-container>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
#app {
  font-family: 'Microsoft YaHei', Arial, sans-serif;
}
.el-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
</style>
