import type { UserConfigExport } from "@tarojs/cli"

/** 本地开发改下一行为 true，连本机后端；正常保持 false = SIT */
const USE_LOCAL_API = false

const LOCAL_API_BASE_URL = 'http://localhost:8080'
const SIT_API_BASE_URL = 'https://www.credo-sit.fun'

export default {
  defineConstants: {
    API_BASE_URL: JSON.stringify(USE_LOCAL_API ? LOCAL_API_BASE_URL : SIT_API_BASE_URL),
  },
  mini: {},
  h5: {},
} satisfies UserConfigExport<'vite'>
