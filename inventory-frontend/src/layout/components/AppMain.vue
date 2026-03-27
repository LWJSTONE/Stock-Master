<template>
  <section class="app-main">
    <transition name="fade-transform" mode="out-in">
      <keep-alive :include="cachedViews">
        <router-view :key="key" />
      </keep-alive>
    </transition>
  </section>
</template>

<script>
export default {
  name: 'AppMain',
  computed: {
    cachedViews() {
      return []
    },
    key() {
      return this.$route.path
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/assets/styles/variables.scss';

.app-main {
  min-height: calc(100vh - #{$navbar-height});
  width: 100%;
  position: relative;
  overflow: hidden;
  background-color: $bg-page;
  padding: 20px;
}
</style>
