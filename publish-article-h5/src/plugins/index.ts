import type { App } from 'vue'

import { setupElementPlus } from './element'

export function setupPlugins(app: App) {
  setupElementPlus(app)
}
