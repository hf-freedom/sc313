<template>
  <div class="worker-dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>师傅信息</span>
          </div>
          <el-select v-model="selectedWorkerId" placeholder="请选择师傅" @change="onWorkerChange" style="width: 100%; margin-bottom: 15px;" popper-append-to-body>
            <el-option
              v-for="worker in workers"
              :key="worker.id"
              :label="worker.name + ' (' + worker.area + ')'"
              :value="worker.id"
            ></el-option>
          </el-select>
          <el-descriptions :column="1" border v-if="currentWorker">
            <el-descriptions-item label="姓名">{{ currentWorker.name }}</el-descriptions-item>
            <el-descriptions-item label="电话">{{ currentWorker.phone }}</el-descriptions-item>
            <el-descriptions-item label="区域">{{ currentWorker.area }}</el-descriptions-item>
            <el-descriptions-item label="技能">
              <el-tag v-for="skill in currentWorker.skills" :key="skill" style="margin-right: 5px; margin-bottom: 5px;">{{ skill }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="评分">
              <el-rate :value="currentWorker.rating" disabled text-color="#ff9900" show-score></el-rate>
            </el-descriptions-item>
            <el-descriptions-item label="服务分">
              <el-progress :percentage="currentWorker.serviceScore" :stroke-width="12"></el-progress>
            </el-descriptions-item>
            <el-descriptions-item label="当前负载">
              <div style="margin-bottom: 5px;">{{ currentWorker.currentLoad }} / {{ currentWorker.maxLoad }}</div>
              <el-progress 
                :percentage="(currentWorker.currentLoad / currentWorker.maxLoad) * 100" 
                :stroke-width="8"
                :color="currentWorker.currentLoad >= currentWorker.maxLoad ? '#F56C6C' : '#67C23A'"
              ></el-progress>
              <div v-if="currentWorker.currentLoad >= currentWorker.maxLoad" style="color: #F56C6C; font-size: 12px; margin-top: 5px;">
                <i class="el-icon-warning"></i> 已达接单上限
              </div>
            </el-descriptions-item>
            <el-descriptions-item label="接单数量">{{ currentWorker.orderCount }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="currentWorker.status === 1 ? 'success' : 'danger'">
                {{ currentWorker.status === 1 ? '正常' : '暂停' }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card class="box-card" style="margin-top: 20px;">
          <div slot="header" class="clearfix">
            <span>抢单校验说明</span>
          </div>
          <div class="check-info">
            <div class="check-item">
              <i class="el-icon-circle-check" style="color: #67C23A;"></i>
              <span>技能匹配：订单维修类型需在您的技能列表中</span>
            </div>
            <div class="check-item">
              <i class="el-icon-circle-check" style="color: #67C23A;"></i>
              <span>距离限制：订单地址需在您20公里范围内</span>
            </div>
            <div class="check-item">
              <i class="el-icon-circle-check" style="color: #67C23A;"></i>
              <span>负载限制：当前接单量未达上限</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="18">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>可接订单</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="loadAvailableOrders">刷新</el-button>
          </div>
          <el-table :data="availableOrders" border v-loading="loadingAvailable">
            <el-table-column prop="orderNo" label="订单号" width="180"></el-table-column>
            <el-table-column prop="type" label="维修类型" width="110">
              <template slot-scope="scope">
                <div>
                  {{ scope.row.type }}
                  <el-tag 
                    :type="checkSkillMatch(scope.row.type) ? 'success' : 'danger'" 
                    size="mini"
                    style="margin-left: 5px;"
                  >
                    {{ checkSkillMatch(scope.row.type) ? '匹配' : '不匹配' }}
                  </el-tag>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="title" label="标题" width="150" show-overflow-tooltip></el-table-column>
            <el-table-column label="紧急" width="60">
              <template slot-scope="scope">
                <el-tag v-if="scope.row.urgent" type="danger">紧急</el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column label="推送状态" width="110">
              <template slot-scope="scope">
                <el-tag 
                  v-if="scope.row.urgent && isPushedToMe(scope.row)" 
                  type="success"
                  size="mini"
                >
                  <i class="el-icon-bell"></i> 已推送
                </el-tag>
                <el-tag 
                  v-else-if="scope.row.urgent" 
                  type="info"
                  size="mini"
                >
                  已推送他人
                </el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="area" label="区域" width="80"></el-table-column>
            <el-table-column label="距离" width="100">
              <template slot-scope="scope">
                <div>
                  {{ getDistance(scope.row) }}
                  <el-tag 
                    :type="getDistance(scope.row) <= 20 ? 'success' : 'danger'" 
                    size="mini"
                    style="margin-left: 5px;"
                  >
                    {{ getDistance(scope.row) <= 20 ? '可接' : '超距' }}
                  </el-tag>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="负载状态" width="100">
              <template slot-scope="scope">
                <el-tag 
                  :type="currentWorker && currentWorker.currentLoad < currentWorker.maxLoad ? 'success' : 'danger'" 
                  size="mini"
                >
                  {{ currentWorker && currentWorker.currentLoad < currentWorker.maxLoad ? '可接单' : '已满' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="address" label="地址" width="180" show-overflow-tooltip></el-table-column>
            <el-table-column prop="userName" label="用户" width="70"></el-table-column>
            <el-table-column prop="createTime" label="发布时间" width="170"></el-table-column>
            <el-table-column label="操作" width="120" fixed="right">
              <template slot-scope="scope">
                <el-tooltip 
                  :content="getAcceptDisabledReason(scope.row)" 
                  :disabled="canAcceptOrder(scope.row)"
                  placement="top"
                >
                  <el-button
                    size="mini"
                    type="primary"
                    @click="acceptOrder(scope.row)"
                    :disabled="!canAcceptOrder(scope.row)"
                  >抢单</el-button>
                </el-tooltip>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <el-card class="box-card" style="margin-top: 20px;">
          <div slot="header" class="clearfix">
            <span>我的订单</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="loadMyOrders">刷新</el-button>
          </div>
          <el-table :data="myOrders" border v-loading="loadingMyOrders">
            <el-table-column prop="orderNo" label="订单号" width="180"></el-table-column>
            <el-table-column prop="type" label="维修类型" width="100"></el-table-column>
            <el-table-column prop="title" label="标题" width="150" show-overflow-tooltip></el-table-column>
            <el-table-column label="紧急" width="60">
              <template slot-scope="scope">
                <el-tag v-if="scope.row.urgent" type="danger">紧急</el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="statusDesc" label="状态" width="160">
              <template slot-scope="scope">
                <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.statusDesc }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="userName" label="用户" width="70"></el-table-column>
            <el-table-column prop="acceptTime" label="接单时间" width="170"></el-table-column>
            <el-table-column label="操作" width="180" fixed="right">
              <template slot-scope="scope">
                <el-button
                  size="mini"
                  type="success"
                  @click="confirmVisit(scope.row)"
                  v-if="scope.row.status === 1"
                >确认上门</el-button>
                <el-button
                  size="mini"
                  type="primary"
                  @click="completeOrder(scope.row)"
                  v-if="scope.row.status === 2"
                >完成维修</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: 'WorkerDashboard',
  data() {
    return {
      workers: [],
      selectedWorkerId: '',
      currentWorker: null,
      availableOrders: [],
      myOrders: [],
      loadingAvailable: false,
      loadingMyOrders: false
    }
  },
  mounted() {
    this.loadWorkers()
  },
  methods: {
    loadWorkers() {
      this.$http.get('/workers').then(res => {
        if (res.data.code === 200) {
          this.workers = res.data.data
          if (this.workers.length > 0) {
            this.selectedWorkerId = this.workers[0].id
            this.$nextTick(() => {
              this.onWorkerChange()
            })
          }
        }
      }).catch(err => {
        console.error('加载师傅列表失败:', err)
      })
    },
    onWorkerChange() {
      this.currentWorker = this.workers.find(w => w.id === this.selectedWorkerId)
      this.loadAvailableOrders()
      this.loadMyOrders()
    },
    loadAvailableOrders() {
      if (!this.selectedWorkerId) return
      this.loadingAvailable = true
      this.$http.get('/orders/available/' + this.selectedWorkerId).then(res => {
        if (res.data.code === 200) {
          this.availableOrders = res.data.data
        }
      }).catch(err => {
        console.error('加载可接订单失败:', err)
      }).finally(() => {
        this.loadingAvailable = false
      })
    },
    loadMyOrders() {
      if (!this.selectedWorkerId) return
      this.loadingMyOrders = true
      this.$http.get('/orders/worker/' + this.selectedWorkerId).then(res => {
        if (res.data.code === 200) {
          this.myOrders = res.data.data
        }
      }).catch(err => {
        console.error('加载我的订单失败:', err)
      }).finally(() => {
        this.loadingMyOrders = false
      })
    },
    checkSkillMatch(orderType) {
      if (!this.currentWorker || !this.currentWorker.skills) return false
      return this.currentWorker.skills.includes(orderType)
    },
    getDistance(order) {
      if (!this.currentWorker || !order.latitude || !order.longitude) {
        return '未知'
      }
      const distance = this.calculateDistance(
        this.currentWorker.latitude,
        this.currentWorker.longitude,
        order.latitude,
        order.longitude
      )
      return distance.toFixed(2) + 'km'
    },
    calculateDistance(lat1, lon1, lat2, lon2) {
      const radLat1 = Math.PI * lat1 / 180
      const radLat2 = Math.PI * lat2 / 180
      const a = radLat1 - radLat2
      const b = Math.PI * lon1 / 180 - Math.PI * lon2 / 180
      let s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
        Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)))
      s = s * 6378.137
      return Math.round(s * 10000) / 10000
    },
    canAcceptOrder(order) {
      if (!this.currentWorker) return false
      
      if (this.currentWorker.status !== 1) return false
      
      if (this.currentWorker.currentLoad >= this.currentWorker.maxLoad) return false
      
      if (!this.checkSkillMatch(order.type)) return false
      
      const distance = this.calculateDistance(
        this.currentWorker.latitude,
        this.currentWorker.longitude,
        order.latitude,
        order.longitude
      )
      if (distance > 20) return false
      
      return true
    },
    getAcceptDisabledReason(order) {
      if (!this.currentWorker) return '请先选择师傅'
      if (this.currentWorker.status !== 1) return '您的账号已被暂停接单'
      if (this.currentWorker.currentLoad >= this.currentWorker.maxLoad) return '您的接单已达上限'
      if (!this.checkSkillMatch(order.type)) return '您不具备该维修类型技能'
      const distance = this.calculateDistance(
        this.currentWorker.latitude,
        this.currentWorker.longitude,
        order.latitude,
        order.longitude
      )
      if (distance > 20) return '订单距离超过20公里'
      return '可以抢单'
    },
    acceptOrder(order) {
      this.$confirm('确定要接这个订单吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http.post('/orders/' + order.id + '/accept', { workerId: this.selectedWorkerId }).then(res => {
          if (res.data.code === 200) {
            this.$message.success('抢单成功！')
            this.loadAvailableOrders()
            this.loadMyOrders()
            this.loadWorkers()
          } else {
            this.$message.error(res.data.message)
          }
        }).catch(err => {
          console.error('抢单失败:', err)
          this.$message.error('抢单失败，请稍后重试')
        })
      })
    },
    confirmVisit(order) {
      this.$confirm('确认已上门服务？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http.post('/orders/' + order.id + '/visit', { workerId: this.selectedWorkerId }).then(res => {
          if (res.data.code === 200) {
            this.$message.success('已确认上门')
            this.loadMyOrders()
          } else {
            this.$message.error(res.data.message)
          }
        })
      })
    },
    completeOrder(order) {
      this.$confirm('确认维修已完成？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http.post('/orders/' + order.id + '/complete', { workerId: this.selectedWorkerId }).then(res => {
          if (res.data.code === 200) {
            this.$message.success('订单已完成')
            this.loadMyOrders()
            this.loadWorkers()
          } else {
            this.$message.error(res.data.message)
          }
        })
      })
    },
    getStatusType(status) {
      const types = ['warning', 'primary', 'info', 'success', 'danger', 'warning']
      return types[status] || 'info'
    },
    isPushedToMe(order) {
      if (!this.selectedWorkerId || !order.pushedWorkerIds || !order.pushedWorkerIds.length) {
        return false
      }
      return order.pushedWorkerIds.includes(this.selectedWorkerId)
    }
  }
}
</script>

<style scoped>
.box-card {
  margin-bottom: 20px;
}
.check-info {
  font-size: 13px;
  color: #606266;
}
.check-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  line-height: 1.5;
}
.check-item i {
  margin-right: 8px;
  font-size: 16px;
}
.worker-dashboard ::v-deep .el-select {
  width: 100%;
}
</style>
