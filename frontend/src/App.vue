<template>
  <main class="chat-shell">
    <section class="chat-panel" aria-label="RoleLLM 聊天窗口">
      <header class="chat-header">
        <div>
          <p class="eyebrow">RoleLLM</p>
          <h1>LLM 对话系统</h1>
        </div>
        <div class="header-actions">
          <el-tag v-if="lastModel" effect="plain" type="info">{{ lastModel }}</el-tag>
          <el-button :disabled="loading && messages.length === 0" @click="startNewConversation">
            新对话
          </el-button>
        </div>
      </header>

      <div ref="messageListRef" class="message-list" aria-live="polite">
        <div v-for="message in messages" :key="message.id" :class="['message-row', message.role]">
          <div class="message-bubble">
            <div class="message-author">{{ message.role === 'user' ? '你' : 'AI' }}</div>
            <div class="message-content">{{ message.content }}</div>
            <audio
              v-if="message.audioUrl"
              class="message-audio"
              :src="message.audioUrl"
              controls
              preload="none"
            />
          </div>
        </div>

        <div v-if="loading" class="message-row assistant">
          <div class="message-bubble loading-bubble">
            <div class="message-author">AI</div>
            <el-skeleton animated :rows="2" />
          </div>
        </div>

        <div v-if="messages.length === 0 && !loading" class="empty-state">
          <h2>开始一次对话</h2>
          <p>输入一句话，后端会返回文字回复；开启 TTS 后，AI 回复下方会出现中文语音播放器。</p>
        </div>
      </div>

      <el-alert
        v-if="errorMessage"
        class="error-alert"
        :title="errorMessage"
        type="error"
        show-icon
        :closable="true"
        @close="errorMessage = ''"
      />

      <form class="composer" @submit.prevent="handleSend">
        <el-input
          v-model="input"
          class="composer-input"
          type="textarea"
          :autosize="{ minRows: 2, maxRows: 5 }"
          resize="none"
          maxlength="8000"
          show-word-limit
          placeholder="输入消息，Enter 发送，Shift + Enter 换行"
          :disabled="loading"
          @keydown.enter.exact.prevent="handleSend"
        />
        <el-button
          class="send-button"
          type="primary"
          native-type="submit"
          :loading="loading"
          :disabled="!canSend"
        >
          发送
        </el-button>
      </form>
    </section>
  </main>
</template>

<script setup>
import { computed, nextTick, ref } from 'vue'
import { sendChatMessage } from './api/chat'

const messages = ref([])
const input = ref('')
const loading = ref(false)
const errorMessage = ref('')
const lastModel = ref('')
const conversationId = ref('')
const messageListRef = ref(null)

const canSend = computed(() => input.value.trim().length > 0 && !loading.value)

async function handleSend() {
  const text = input.value.trim()
  if (!text || loading.value) {
    return
  }

  errorMessage.value = ''
  messages.value.push(createMessage('user', text))
  input.value = ''
  loading.value = true
  await scrollToBottom()

  try {
    const result = await sendChatMessage(text, conversationId.value)
    messages.value.push(createMessage('assistant', result.reply || '', result.audioUrl || ''))
    lastModel.value = result.model || ''
    conversationId.value = result.conversationId || conversationId.value
  } catch (error) {
    errorMessage.value = error.message || '请求失败，请稍后重试'
  } finally {
    loading.value = false
    await scrollToBottom()
  }
}

function startNewConversation() {
  messages.value = []
  input.value = ''
  errorMessage.value = ''
  lastModel.value = ''
  conversationId.value = ''
}

function createMessage(role, content, audioUrl = '') {
  return {
    id: `${Date.now()}-${Math.random().toString(16).slice(2)}`,
    role,
    content,
    audioUrl
  }
}

async function scrollToBottom() {
  await nextTick()
  const element = messageListRef.value
  if (element) {
    element.scrollTop = element.scrollHeight
  }
}
</script>
