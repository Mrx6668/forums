<script setup>
  import {logout} from "@/net";
  import router from "@/router";

  function userLogout() {
    logout(() => {
      router.push('/')
    });
  }
</script>

<template>

</template>

<style scoped>

</style>
