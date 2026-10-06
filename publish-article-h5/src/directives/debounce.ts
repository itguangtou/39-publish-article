import type { Directive } from 'vue'

export const debounceDirective: Directive = {
  mounted(el, binding) {
    const delay = Number(binding.arg) || 300
    let timer: ReturnType<typeof setTimeout> | null = null

    el.addEventListener('click', () => {
      if (timer) clearTimeout(timer)
      timer = setTimeout(() => {
        binding.value?.()
      }, delay)
    })
  },
}
