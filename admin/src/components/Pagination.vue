<template>
  <el-pagination
       class="page-box"
       @size-change="handleSizeChange"
       @current-change="handleCurrentChange"
       background
       :current-page="params.currentPage"
       :page-sizes="[5, 10, 20, 30]"
       :page-size="params.pageSize"
       layout="total, sizes, prev, pager, next"
       :total="params.total">
  </el-pagination>
</template>
<script>
export default {
  name: 'Pagination',
  props: ['params'],
  data() {
    return {
      pageParams: {
        currentPage: this.params.currentPage,
        pageSize: this.params.pageSize
      }
    }
  },
  methods: {
    handleSizeChange(val) {
      this.pageParams.pageSize = val;
      this.$emit('changePagination', this.pageParams)
    },
    handleCurrentChange(val) {
      this.pageParams.currentPage = val;
      this.$emit('changePagination', this.pageParams)
    }
  }
}
</script>

<style>
.page-box {
  margin: 10px auto;
}
</style>
