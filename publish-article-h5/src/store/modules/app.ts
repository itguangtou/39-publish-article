import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  const pageLoading = ref(false)

  function setPageLoading(loading: boolean) {
    pageLoading.value = loading
  }

  return { pageLoading, setPageLoading }
})
