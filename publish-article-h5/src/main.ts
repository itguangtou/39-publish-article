import { createApp } from 'vue'

import App from './App.vue'
import { setupDirectives } from './directives'
import { setupPlugins } from './plugins'
import router from './router'
import { setupStore } from './store'

import './styles/tailwind.css'
import './styles/element-overwrite.scss'
import './styles/admin-mobile.scss'
import './styles/transition.scss'

const app = createApp(App)

setupStore(app)
setupPlugins(app)
setupDirectives(app)
app.use(router)

app.mount('#app')
