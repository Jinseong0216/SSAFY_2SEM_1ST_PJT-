<template>
  <Teleport to="body">
    <div v-if="isWakeWordDetected" class="fixed top-[12vh] right-3 flex items-center space-x-2 bg-gray-800 text-white px-4 py-2 rounded-lg shadow-md" style="z-index: 2147483647 !important;">
      <img src="@/assets/images/pesocom.png" alt="파소콘" class="w-8 h-8 object-contain" />
      <span>명령어 듣는 중...</span>
      <MicIcon class="w-6 h-6 text-red-400" />
      <div class="relative w-8 h-8">
        <svg class="absolute top-0 left-0 w-full h-full">
          <circle
            class="text-gray-700"
            stroke="currentColor"
            stroke-width="4"
            fill="transparent"
            r="12"
            cx="16"
            cy="16"
          />
          <circle
            class="text-blue-400 transition-all"
            :style="{ strokeDasharray: '75.4', strokeDashoffset: `${(timer / 5) * 75.4}` }"
            stroke="currentColor"
            stroke-width="4"
            fill="transparent"
            r="12"
            cx="16"
            cy="16"
          />
       </svg>
     </div>
   </div>
   <div class="hidden"></div>
  </Teleport>
</template>

<script setup>
import { ref, onMounted, onUnmounted, defineEmits } from "vue"
import beepSound from "@/assets/musics/beep.mp3"
const playBeep = () => {
  const beepAudio = new Audio(beepSound)
  beepAudio.play().catch(error => console.error("오디오 재생 실패:", error))
}

const emit = defineEmits(["voice-control"])
const transcript = ref("")
const isListening = ref(false)
const isWakeWordDetected = ref(false)
const timer = ref(0)
let recognition = null

const wakeWord = "파소콘"
const commands = ["종료","상태", "상태창", "설정","전적","달력","운동","캐릭터","퀘스트"]
const emits = { "종료": "close", "상태": "status", "상태창": "status", "설정": "setting", "전적": "record", "달력": "calendar", "운동": "fitness", "캐릭터": "character", "퀘스트": "quest" }
const sendEmit = (command) => { emit("voice-control", command) }

onMounted(() => {
if (!("webkitSpeechRecognition" in window || "SpeechRecognition" in window)) {
 console.warn("⚠️ 음성 인식을 지원하지 않는 브라우저입니다.")
 return
}

recognition = new (window.SpeechRecognition || window.webkitSpeechRecognition)()
recognition.continuous = true
recognition.interimResults = false // 🔥 최종 결과만 반영
recognition.lang = "ko-KR"

recognition.onstart = () => (isListening.value = true)
recognition.onend = () => {
  console.log("🛑 음성 인식 종료됨")
  isListening.value = false
  
  setTimeout(() => {
    if (!isListening.value) {
      console.log("🔄 음성 인식 다시 시작")
      recognition.start()
    }
  }, 500)
}

recognition.onerror = (event) => {
  console.error("⚠️ 음성 인식 오류 발생:", event)
  isListening.value = false

  setTimeout(() => {
    if (!isListening.value) {
      console.log("오류 후 음성 인식 다시 시작")
      recognition.start()
    }
  }, 3000)
}

recognition.onresult = (event) => {
 let finalTranscript = event.results[event.results.length - 1][0].transcript.trim()
 transcript.value = normalizeText(finalTranscript)
 processSpeech(transcript.value)
}

recognition.start()
})

const processSpeech = (text) => {
  if (isWakeWordDetected.value) {
  checkCommand(text)
  return
  }

  const words = text.split(/\s+/) // 단어 단위로 분리
  for (const word of words) {
   if (levenshteinDistance(word, wakeWord) <= 2) {
     console.log(`🟢 웨이크워드 "${word}" 감지!`)
     startCommandListening()
     return // 웨이크워드 감지 시 즉시 종료
   }
  }
}

const checkCommand = (text) => {
const words = text.split(/\s+/)
for (const word of words) {
 for (const command of commands) {
  if (levenshteinDistance(word, command) <= (command.length === 2 ? 1 : command.length === 3 ? 2 : 3)) {
     console.log(`✅ 명령어 "${word}" 감지됨!`)
     executeCommand(command)
     isWakeWordDetected.value = false
     return
   }
 }
}
}

const executeCommand = (command) => {
sendEmit(emits[command])
}

const startCommandListening = () => {
console.log("🔔 명령어 입력 대기 시작 (5초)")
isWakeWordDetected.value = true
timer.value = 5

// 🔊 비프음 재생
playBeep()

const countdown = setInterval(() => {
 timer.value--
 if (timer.value <= 0) {
   clearInterval(countdown)
   isWakeWordDetected.value = false
   console.log("❌ 명령어 대기 종료")
 }
}, 1000)
}

const normalizeText = (text) => text.replace(/\s+/g, " ").trim()

// 🎯 Levenshtein 거리 계산 함수 (한글 자모 비교)
const levenshteinDistance = (inputWord, targetWord) => {  
  const a = inputWord.normalize("NFD")
  const b = targetWord.normalize("NFD")

  const lenA = a.length
  const lenB = b.length

  if (!lenA) return lenB
  if (!lenB) return lenA

  const matrix = Array.from({ length: lenA + 1 }, () => Array(lenB + 1).fill(0))

  for (let i = 0; i <= lenA; i++) matrix[i][0] = i
  for (let j = 0; j <= lenB; j++) matrix[0][j] = j

  for (let i = 1; i <= lenA; i++) {
    for (let j = 1; j <= lenB; j++) {
      if (a[i - 1] === b[j - 1]) {
        matrix[i][j] = matrix[i - 1][j - 1] // 같은 글자면 그대로
      } else {
        matrix[i][j] = Math.min(
          matrix[i - 1][j] + 1, // 삭제
          matrix[i][j - 1] + 1, // 삽입
          matrix[i - 1][j - 1] + 1 // 대체
        )
      }
    }
  }
  console.log('인식단어:', inputWord, '비교단어:', targetWord, '거리차이:', matrix[lenA][lenB])
  return matrix[lenA][lenB]
}


onUnmounted(() => {
if (recognition) {
 recognition.onend = null // 자동 재시작 방지
 recognition.stop()
}
})
</script>
<!-- <template>
  <div>

  </div>
</template>

<script setup>

</script>

<style lang="scss" scoped>

</style> -->