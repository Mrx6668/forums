<script setup>
defineProps({
  name: String
})
</script>

<template>
  <div class="interact-button">
    <span class="icon">
      <slot/>
    </span>
    <span class="name">
      {{ name }}
    </span>
  </div>
</template>

<style scoped>
.interact-button {
  display: inline-block;
  height: 20px;
}

.name {
  font-size: 13px;
  margin-left: 5px;
  opacity: 0.7;
}

.icon {
  transition: .4s;
}
.icon:hover {
  cursor: pointer;
  font-size: 18px;
}
</style>
