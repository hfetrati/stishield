package com.hemad.stishield.model.common

sealed class ChatItem {
    data class MessageItemWithServerResult(val message:Message, val serverResult:Result<Unit>?):ChatItem()
    data class MessageItem(val message: Message): ChatItem()
    object LoadingIndicator: ChatItem()
}