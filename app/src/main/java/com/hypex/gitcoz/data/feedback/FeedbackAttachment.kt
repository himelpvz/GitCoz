package com.hypex.gitcoz.data.feedback

data class FeedbackAttachment(
    val fileName: String,
    val mimeType: String,
    val bytes: ByteArray
)
