<template>
  <div class="admin-dashboard">
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>订单统计</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="loadOrders">刷新</el-button>
          </div>
          <el-row :gutter="20">
            <el-col :span="6">
              <div class="stat-card">
                <div class="stat-number">{{ stats.total }}</div>
                <div class="stat-label">总订单数</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-card warning">
                <div class="stat-number">{{ stats.pending }}</div>
                <div class="stat-label">待接单</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-card primary">
                <div class="stat-number">{{ stats.accepted }}</div>
                <div class="stat-label">已接单</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-card success">
                <div class="stat-number">{{ stats.completed }}</div>
                <div class="stat-label">已完成</div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>师傅统计</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="loadWorkers">刷新</el-button>
          </div>
          <el-row :gutter="20">
            <el-col :span="8">
              <div class="stat-card">
                <div class="stat-number">{{ workerStats.total }}</div>
                <div class="stat-label">师傅总数</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="stat-card success">
                <div class="stat-number">{{ workerStats.active }}</div>
                <div class="stat-label">在线师傅</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="stat-card danger">
                <div class="stat-number">{{ workerStats.lowRated }}</div>
                <div class="stat-label">低评分</div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>超时订单管理</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="loadTimeoutOrders">刷新</el-button>
          </div>
          <el-alert
            title="超时订单说明"
            type="warning"
            :closable="false"
            style="margin-bottom: 15px;"
          >
            <div slot="title">
              <strong>超时未接单：</strong>订单发布后超过60秒无人接单，自动释放重派
              <span style="margin-left: 20px;"><strong>超时未上门：</strong>师傅接单后超过300秒未上门，自动释放重派</span>
            </div>
          </el-alert>
          <el-tabs v-model="activeTab" @tab-click="handleTabClick">
            <el-tab-pane label="超时未接单" name="unaccepted">
              <el-table :data="timeoutUnacceptedOrders" border v-loading="loadingTimeoutOrders" empty-text="暂无超时未接单订单">
                <el-table-column prop="orderNo" label="订单号" width="180"></el-table-column>
                <el-table-column prop="type" label="维修类型" width="100"></el-table-column>
                <el-table-column prop="title" label="标题" width="150" show-overflow-tooltip></el-table-column>
                <el-table-column label="紧急" width="60">
                  <template slot-scope="scope">
                    <el-tag v-if="scope.row.urgent" type="danger">紧急</el-tag>
                    <span v-else>-</span>
                  </template>
                </el-table-column>
                <el-table-column prop="area" label="区域" width="80"></el-table-column>
                <el-table-column prop="userName" label="用户" width="80"></el-table-column>
                <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
                <el-table-column label="等待时长" width="120">
                  <template slot-scope="scope">
                    <el-tag type="warning">{{ getWaitTime(scope.row.createTime) }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="120" fixed="right">
                  <template slot-scope="scope">
                    <el-button
                      size="mini"
                      type="primary"
                      @click="reassignOrder(scope.row)"
                    >手动重派</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
            <el-tab-pane label="超时未上门" name="unvisited">
              <el-table :data="timeoutUnvisitedOrders" border v-loading="loadingTimeoutOrders" empty-text="暂无超时未上门订单">
                <el-table-column prop="orderNo" label="订单号" width="180"></el-table-column>
                <el-table-column prop="type" label="维修类型" width="100"></el-table-column>
                <el-table-column prop="title" label="标题" width="150" show-overflow-tooltip></el-table-column>
                <el-table-column label="紧急" width="60">
                  <template slot-scope="scope">
                    <el-tag v-if="scope.row.urgent" type="danger">紧急</el-tag>
                    <span v-else>-</span>
                  </template>
                </el-table-column>
                <el-table-column prop="area" label="区域" width="80"></el-table-column>
                <el-table-column prop="userName" label="用户" width="80"></el-table-column>
                <el-table-column prop="workerName" label="接单师傅" width="100"></el-table-column>
                <el-table-column prop="acceptTime" label="接单时间" width="180"></el-table-column>
                <el-table-column label="超时时长" width="120">
                  <template slot-scope="scope">
                    <el-tag type="danger">{{ getWaitTime(scope.row.acceptTime, 300) }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="180" fixed="right">
                  <template slot-scope="scope">
                    <el-button
                      size="mini"
                      type="danger"
                      @click="releaseOrder(scope.row)"
                    >释放重派</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>所有订单</span>
          </div>
          <el-table :data="allOrders" border v-loading="loadingOrders">
            <el-table-column prop="orderNo" label="订单号" width="180"></el-table-column>
            <el-table-column prop="type" label="维修类型" width="100"></el-table-column>
            <el-table-column prop="title" label="标题" width="150" show-overflow-tooltip></el-table-column>
            <el-table-column label="紧急" width="60">
                  <template slot-scope="scope">
                    <el-tag v-if="scope.row.urgent" type="danger">紧急</el-tag>
                    <span v-else>-</span>
                  </template>
                </el-table-column>
                <el-table-column label="推送状态" width="180">
                  <template slot-scope="scope">
                    <template v-if="scope.row.urgent && scope.row.pushedWorkerIds && scope.row.pushedWorkerIds.length > 0">
                      <el-tooltip placement="top">
                        <div slot="content">
                          <div v-for="workerId in scope.row.pushedWorkerIds" :key="workerId">
                            {{ getWorkerName(workerId) }}
                          </div>
                        </div>
                        <el-tag type="success" size="mini">
                          <i class="el-icon-bell"></i> 已推送 {{ scope.row.pushedWorkerIds.length }} 位
                        </el-tag>
                      </el-tooltip>
                    </template>
                    <el-tag v-else-if="scope.row.urgent" type="warning" size="mini">未推送</el-tag>
                    <span v-else>-</span>
                  </template>
                </el-table-column>
                <el-table-column prop="statusDesc" label="状态" width="180">
              <template slot-scope="scope">
                <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.statusDesc }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="userName" label="用户" width="80"></el-table-column>
            <el-table-column prop="workerName" label="师傅" width="80"></el-table-column>
            <el-table-column prop="area" label="区域" width="80"></el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
            <el-table-column prop="acceptTime" label="接单时间" width="180"></el-table-column>
            <el-table-column prop="completeTime" label="完成时间" width="180"></el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>师傅列表</span>
          </div>
          <el-table :data="allWorkers" border v-loading="loadingWorkers">
            <el-table-column prop="name" label="姓名" width="100"></el-table-column>
            <el-table-column prop="phone" label="电话" width="130"></el-table-column>
            <el-table-column prop="area" label="区域" width="100"></el-table-column>
            <el-table-column label="技能" width="300">
              <template slot-scope="scope">
                <el-tag v-for="skill in scope.row.skills" :key="skill" style="margin-right: 5px; margin-bottom: 5px;">{{ skill }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="评分" width="150">
              <template slot-scope="scope">
                <el-rate :value="scope.row.rating" disabled text-color="#ff9900" show-score></el-rate>
              </template>
            </el-table-column>
            <el-table-column label="服务分" width="150">
              <template slot-scope="scope">
                <el-progress :percentage="scope.row.serviceScore" :stroke-width="10"></el-progress>
              </template>
            </el-table-column>
            <el-table-column label="负载" width="120">
              <template slot-scope="scope">
                {{ scope.row.currentLoad }} / {{ scope.row.maxLoad }}
              </template>
            </el-table-column>
            <el-table-column prop="orderCount" label="接单量" width="80"></el-table-column>
            <el-table-column label="状态" width="100">
              <template slot-scope="scope">
                <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                  {{ scope.row.status === 1 ? '正常' : '暂停' }}
                </el-tag>
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
  name: 'AdminDashboard',
  data() {
    return {
      activeTab: 'unaccepted',
      allOrders: [],
      allWorkers: [],
      timeoutUnacceptedOrders: [],
      timeoutUnvisitedOrders: [],
      loadingOrders: false,
      loadingWorkers: false,
      loadingTimeoutOrders: false,
      stats: {
        total: 0,
        pending: 0,
        accepted: 0,
        completed: 0
      },
      workerStats: {
        total: 0,
        active: 0,
        lowRated: 0
      }
    }
  },
  mounted() {
    this.loadOrders()
    this.loadWorkers()
    this.loadTimeoutOrders()
  },
  methods: {
    handleTabClick(tab) {
      this.activeTab = tab.name
    },
    loadOrders() {
      this.loadingOrders = true
      this.$http.get('/orders').then(res => {
        if (res.data.code === 200) {
          this.allOrders = res.data.data
          this.stats.total = this.allOrders.length
          this.stats.pending = this.allOrders.filter(o => o.status === 0).length
          this.stats.accepted = this.allOrders.filter(o => o.status === 1).length
          this.stats.completed = this.allOrders.filter(o => o.status === 3).length
        }
      }).finally(() => {
        this.loadingOrders = false
      })
    },
    loadTimeoutOrders() {
      this.loadingTimeoutOrders = true
      this.$http.get('/orders').then(res => {
        if (res.data.code === 200) {
          const now = Date.now()
          const allOrders = res.data.data
          
          this.timeoutUnacceptedOrders = allOrders.filter(o => {
            if (o.status !== 0) return false
            const createTime = new Date(o.createTime).getTime()
            const waitSeconds = (now - createTime) / 1000
            return waitSeconds >= 60
          })
          
          this.timeoutUnvisitedOrders = allOrders.filter(o => {
            if (o.status !== 1) return false
            const acceptTime = new Date(o.acceptTime).getTime()
            const waitSeconds = (now - acceptTime) / 1000
            return waitSeconds >= 300
          })
        }
      }).finally(() => {
        this.loadingTimeoutOrders = false
      })
    },
    loadWorkers() {
      this.loadingWorkers = true
      this.$http.get('/workers').then(res => {
        if (res.data.code === 200) {
          this.allWorkers = res.data.data
          this.workerStats.total = this.allWorkers.length
          this.workerStats.active = this.allWorkers.filter(w => w.status === 1).length
          this.workerStats.lowRated = this.allWorkers.filter(w => w.rating < 4.5 || w.serviceScore < 80).length
        }
      }).finally(() => {
        this.loadingWorkers = false
      })
    },
    getWaitTime(timeStr, threshold = 60) {
      if (!timeStr) return '-'
      const now = Date.now()
      const time = new Date(timeStr).getTime()
      const diff = (now - time) / 1000 - threshold
      if (diff <= 0) return '0秒'
      if (diff < 60) return Math.floor(diff) + '秒'
      if (diff < 3600) return Math.floor(diff / 60) + '分钟'
      return Math.floor(diff / 3600) + '小时'
    },
    releaseOrder(order) {
      this.$confirm(`确定要释放订单 "${order.title}" 吗？释放后订单将重新进入订单池。`, '提示', {
        confirmButtonText: '确定释放',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http.post('/orders/' + order.id + '/release').then(res => {
          if (res.data.code === 200) {
            this.$message.success('订单已释放并重派')
            this.loadOrders()
            this.loadTimeoutOrders()
          } else {
            this.$message.error(res.data.message)
          }
        }).catch(err => {
          console.error('释放订单失败:', err)
          this.$message.error('释放失败，请稍后重试')
        })
      })
    },
    reassignOrder(order) {
      this.$confirm(`确定要重新派发订单 "${order.title}" 吗？`, '提示', {
        confirmButtonText: '确定重派',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http.post('/orders/' + order.id + '/reassign').then(res => {
          if (res.data.code === 200) {
            this.$message.success('订单已重新派发')
            this.loadOrders()
            this.loadTimeoutOrders()
          } else {
            this.$message.error(res.data.message)
          }
        }).catch(err => {
          console.error('重派订单失败:', err)
          this.$message.error('重派失败，请稍后重试')
        })
      })
    },
    getStatusType(status) {
      const types = ['warning', 'primary', 'info', 'success', 'danger', 'warning']
      return types[status] || 'info'
    },
    getWorkerName(workerId) {
      const worker = this.allWorkers.find(w => w.id === workerId)
      return worker ? worker.name : workerId
    }
  }
}
</script>

<style scoped>
.box-card {
  margin-bottom: 20px;
}
.stat-card {
  text-align: center;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
}
.stat-card.warning {
  background: #fdf6ec;
}
.stat-card.primary {
  background: #ecf5ff;
}
.stat-card.success {
  background: #f0f9eb;
}
.stat-card.danger {
  background: #fef0f0;
}
.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #409EFF;
}
.stat-card.warning .stat-number {
  color: #E6A23C;
}
.stat-card.primary .stat-number {
  color: #409EFF;
}
.stat-card.success .stat-number {
  color: #67C23A;
}
.stat-card.danger .stat-number {
  color: #F56C6C;
}
.stat-label {
  margin-top: 8px;
  color: #909399;
  font-size: 13px;
}
</style>
