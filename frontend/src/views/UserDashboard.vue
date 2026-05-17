<template>
  <div class="user-dashboard">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>发布维修需求</span>
          </div>
          <el-form :model="orderForm" label-width="80px">
            <el-form-item label="选择用户">
              <el-select v-model="orderForm.userId" placeholder="请选择用户" @change="onUserChange" style="width: 100%;" popper-append-to-body>
                <el-option
                  v-for="user in users"
                  :key="user.id"
                  :label="user.name + ' (' + user.phone + ')'"
                  :value="user.id"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="维修类型">
              <el-select v-model="orderForm.type" placeholder="请选择维修类型" style="width: 100%;" popper-append-to-body>
                <el-option label="空调维修" value="空调维修"></el-option>
                <el-option label="冰箱维修" value="冰箱维修"></el-option>
                <el-option label="洗衣机维修" value="洗衣机维修"></el-option>
                <el-option label="水电维修" value="水电维修"></el-option>
                <el-option label="灯具安装" value="灯具安装"></el-option>
                <el-option label="管道疏通" value="管道疏通"></el-option>
                <el-option label="家电维修" value="家电维修"></el-option>
                <el-option label="家具安装" value="家具安装"></el-option>
                <el-option label="门窗维修" value="门窗维修"></el-option>
                <el-option label="锁具服务" value="锁具服务"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="标题">
              <el-input v-model="orderForm.title" placeholder="请输入维修标题"></el-input>
            </el-form-item>
            <el-form-item label="描述">
              <el-input type="textarea" v-model="orderForm.description" placeholder="请描述维修问题" :rows="3"></el-input>
            </el-form-item>
            <el-form-item label="区域">
              <el-select v-model="orderForm.area" placeholder="请选择区域" style="width: 100%;" popper-append-to-body>
                <el-option label="朝阳区" value="朝阳区"></el-option>
                <el-option label="海淀区" value="海淀区"></el-option>
                <el-option label="西城区" value="西城区"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="地址">
              <el-input v-model="orderForm.address" placeholder="请输入详细地址"></el-input>
            </el-form-item>
            <el-form-item label="紧急订单">
              <el-switch v-model="orderForm.urgent" active-text="是" inactive-text="否"></el-switch>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="submitOrder" :loading="submitting" style="width: 100%;">提交订单</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>我的订单</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="loadUserOrders">刷新</el-button>
          </div>
          <el-table :data="userOrders" border v-loading="loadingOrders">
            <el-table-column prop="orderNo" label="订单号" width="180"></el-table-column>
            <el-table-column prop="type" label="维修类型" width="100"></el-table-column>
            <el-table-column prop="title" label="标题" width="150"></el-table-column>
            <el-table-column label="紧急" width="60">
              <template slot-scope="scope">
                <el-tag v-if="scope.row.urgent" type="danger">紧急</el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="statusDesc" label="状态" width="180">
              <template slot-scope="scope">
                <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.statusDesc }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="workerName" label="接单师傅" width="100">
              <template slot-scope="scope">
                <span>{{ scope.row.workerName || '-' }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
              <template slot-scope="scope">
                <el-button
                  size="mini"
                  type="danger"
                  @click="cancelOrder(scope.row)"
                  v-if="scope.row.status === 0 || scope.row.status === 1 || scope.row.status === 2"
                >取消</el-button>
                <el-button
                  size="mini"
                  type="primary"
                  @click="openReviewDialog(scope.row)"
                  v-if="scope.row.status === 3 && !isReviewed(scope.row.id)"
                >评价</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog title="评价服务" :visible.sync="reviewDialogVisible" width="400px" append-to-body>
      <el-form :model="reviewForm" label-width="80px">
        <el-form-item label="评分">
          <el-rate v-model="reviewForm.rating"></el-rate>
        </el-form-item>
        <el-form-item label="评价">
          <el-input type="textarea" v-model="reviewForm.comment" placeholder="请输入评价内容" :rows="3"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="reviewDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitReview" :loading="reviewSubmitting">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'UserDashboard',
  data() {
    return {
      users: [],
      orderForm: {
        userId: '',
        type: '',
        title: '',
        description: '',
        area: '',
        address: '',
        urgent: false,
        latitude: 39.9,
        longitude: 116.4
      },
      submitting: false,
      userOrders: [],
      loadingOrders: false,
      reviewDialogVisible: false,
      reviewForm: {
        orderId: '',
        userId: '',
        rating: 5,
        comment: ''
      },
      reviewSubmitting: false,
      reviewedOrders: new Set()
    }
  },
  mounted() {
    this.loadUsers()
  },
  methods: {
    loadUsers() {
      this.$http.get('/users').then(res => {
        if (res.data.code === 200) {
          this.users = res.data.data
          if (this.users.length > 0) {
            this.orderForm.userId = this.users[0].id
            this.$nextTick(() => {
              this.onUserChange()
            })
          }
        }
      }).catch(err => {
        console.error('加载用户失败:', err)
        this.$message.error('加载用户失败')
      })
    },
    onUserChange() {
      const user = this.users.find(u => u.id === this.orderForm.userId)
      if (user) {
        this.orderForm.area = user.area
        this.orderForm.address = user.address
        this.loadUserOrders()
      }
    },
    submitOrder() {
      console.log('提交订单数据:', this.orderForm)
      
      if (!this.orderForm.userId) {
        this.$message.warning('请选择用户')
        return
      }
      if (!this.orderForm.type) {
        this.$message.warning('请选择维修类型')
        return
      }
      if (!this.orderForm.title || !this.orderForm.title.trim()) {
        this.$message.warning('请输入维修标题')
        return
      }
      if (!this.orderForm.area) {
        this.$message.warning('请选择区域')
        return
      }
      if (!this.orderForm.address || !this.orderForm.address.trim()) {
        this.$message.warning('请输入详细地址')
        return
      }

      this.submitting = true
      this.$http.post('/orders', this.orderForm).then(res => {
        console.log('订单提交响应:', res.data)
        if (res.data.code === 200) {
          this.$message.success('订单发布成功！')
          this.orderForm.title = ''
          this.orderForm.description = ''
          this.orderForm.urgent = false
          this.loadUserOrders()
        } else {
          this.$message.error(res.data.message || '订单发布失败')
        }
      }).catch(err => {
        console.error('提交订单失败:', err)
        this.$message.error('网络错误，请稍后重试')
      }).finally(() => {
        this.submitting = false
      })
    },
    loadUserOrders() {
      if (!this.orderForm.userId) return
      this.loadingOrders = true
      this.$http.get('/orders/user/' + this.orderForm.userId).then(res => {
        if (res.data.code === 200) {
          this.userOrders = res.data.data
        }
      }).catch(err => {
        console.error('加载订单失败:', err)
      }).finally(() => {
        this.loadingOrders = false
      })
    },
    cancelOrder(order) {
      this.$confirm('确定要取消该订单吗？取消后可能会扣除服务分。', '提示', {
        confirmButtonText: '确定取消',
        cancelButtonText: '再想想',
        type: 'warning'
      }).then(() => {
        this.$http.post('/orders/' + order.id + '/cancel', { userId: this.orderForm.userId }).then(res => {
          if (res.data.code === 200) {
            this.$message.success('订单已取消')
            this.loadUserOrders()
          } else {
            this.$message.error(res.data.message)
          }
        })
      })
    },
    openReviewDialog(order) {
      this.reviewForm.orderId = order.id
      this.reviewForm.userId = this.orderForm.userId
      this.reviewForm.rating = 5
      this.reviewForm.comment = ''
      this.reviewDialogVisible = true
    },
    submitReview() {
      this.reviewSubmitting = true
      this.$http.post('/reviews', this.reviewForm).then(res => {
        if (res.data.code === 200) {
          this.$message.success('评价成功')
          this.reviewDialogVisible = false
          this.reviewedOrders.add(this.reviewForm.orderId)
          this.loadUserOrders()
        } else {
          this.$message.error(res.data.message)
        }
      }).finally(() => {
        this.reviewSubmitting = false
      })
    },
    getStatusType(status) {
      const types = ['warning', 'primary', 'info', 'success', 'danger', 'warning']
      return types[status] || 'info'
    },
    isReviewed(orderId) {
      return this.reviewedOrders.has(orderId)
    }
  }
}
</script>

<style scoped>
.box-card {
  margin-bottom: 20px;
}
.user-dashboard ::v-deep .el-select {
  width: 100%;
}
.user-dashboard ::v-deep .el-input {
  width: 100%;
}
</style>
