// stores/mainStore.js
import { defineStore } from "pinia"
import { ref } from "vue"
import axios from "axios"

// 튜토리얼 타입 상수화 (확장성 고려)
export const TUTORIAL_IDS = {
  UI: 99,
  PUSHUP: 1,
  SQUAT: 2,
  LUNGE: 3,
  PLANK: 4,
}

export const useMainStore = defineStore("main", () => {
  const tutorial = ref([])
  const authToken = ref(localStorage.getItem("authToken"))
  const uiTutorialCompleted = ref(false)

  const api = axios.create({
    baseURL: "https://i12e103.p.ssafy.io:8076/api/v1",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${authToken.value}`,
    },
  })

  // 인터셉터 설정 (디버깅 강화)
  api.interceptors.request.use((config) => {
    console.groupCollapsed(`[🌐 REQ] ${config.method?.toUpperCase()} ${config.url}`)
    console.log("Headers:", config.headers)
    return config
  })

  api.interceptors.response.use(
    (response) => {
      console.log("✅ RES:", response.status)
      console.table(response.data)
      console.groupEnd()
      return response
    },
    (error) => {
      console.error("🚨 ERR:", error.response?.status || "NO_STATUS")
      console.error("Data:", error.response?.data || "NO_DATA")
      console.groupEnd()
      return Promise.reject(error)
    }
  )

  // 통합 튜토리얼 완료 함수
  async function completeTutorial(tutorialId) {
    try {
      console.time(`[⏱️] Tutorial #${tutorialId}`)
      const { data } = await api.post(`/Tutorial/${tutorialId}`)

      // 상태 업데이트
      if (tutorialId === TUTORIAL_IDS.UI) {
        uiTutorialCompleted.value = true
      } else {
        const target = tutorial.value.find((t) => t.tutorialId === tutorialId)
        console.log("운동데이터", target)
        if (target) target.completed = data.completed
      }

      return data.success
    } catch (error) {
      console.error(`[🔥] Tutorial #${tutorialId} Error:`, {
        message: error.message,
        config: error.config,
      })
      return false
    } finally {
      console.timeEnd(`[⏱️] Tutorial #${tutorialId}`)
    }
  }

  // 튜토리얼 데이터 로드
  async function loadTutorial() {
    try {
      const response = await api.get("/Tutorial/simple")
      tutorial.value = response.data
      console.log("[📊] Tutorial Data:", JSON.parse(JSON.stringify(tutorial.value)))
    } catch (error) {
      console.error("[❗] Load Error:", error)
      throw error
    }
  }

  return {
    tutorial,
    authToken,
    uiTutorialCompleted,
    TUTORIAL_IDS, // 상수 노출
    loadTutorial,
    completeTutorial,
  }
})
