import type { Directive } from 'vue'

export const lazyDirective: Directive = {
  mounted(el, binding) {
    const observer = new IntersectionObserver(([entry]) => {
      if (entry?.isIntersecting) {
        if (el instanceof HTMLImageElement) {
          el.src = binding.value
        }
        observer.unobserve(el)
      }
    })
    observer.observe(el)
  },
}
