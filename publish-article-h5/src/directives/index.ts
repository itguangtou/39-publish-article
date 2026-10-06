import type { App } from 'vue'

import { debounceDirective } from './debounce'
import { lazyDirective } from './lazy'

export function setupDirectives(app: App) {
  app.directive('debounce', debounceDirective)
  app.directive('lazy', lazyDirective)
}
