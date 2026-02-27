package com.hypex.gitcoz.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hypex.gitcoz.data.feedback.FeedbackAttachment
import com.hypex.gitcoz.data.feedback.TelegramFeedbackSender
import com.hypex.gitcoz.domain.model.GitHubUser
import com.hypex.gitcoz.domain.repository.GitHubRepositoryContract
import com.hypex.gitcoz.presentation.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class FeedbackUiState(
    val userState: UiState<GitHubUser> = UiState.Loading,
    val feedbackText: String = "",
    val attachmentCount: Int = 0,
    val submitState: UiState<Boolean> = UiState.Idle,
    val isSubmitting: Boolean = false
)

class FeedbackViewModel(
    private val repository: GitHubRepositoryContract,
    private val telegramFeedbackSender: TelegramFeedbackSender
) : ViewModel() {

    private val _state = MutableStateFlow(FeedbackUiState())
    val state: StateFlow<FeedbackUiState> = _state.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            try {
                repository.getAuthenticatedUser()
                    .collect { user ->
                        _state.update { it.copy(userState = UiState.Success(user)) }
                    }
            } catch (e: Exception) {
                _state.update {
                    it.copy(userState = UiState.Error(e.message ?: "Unable to load profile."))
                }
            }
        }
    }

    fun onFeedbackChanged(value: String) {
        _state.update { it.copy(feedbackText = value) }
    }

    fun onAttachmentCountChanged(count: Int) {
        _state.update { it.copy(attachmentCount = count) }
    }

    fun submitFeedback(attachments: List<FeedbackAttachment>) {
        val feedback = state.value.feedbackText.trim()
        if (feedback.isBlank()) {
            _state.update { it.copy(submitState = UiState.Error("Please write feedback first.")) }
            return
        }

        val user = (state.value.userState as? UiState.Success)?.data
        if (user == null) {
            _state.update { it.copy(submitState = UiState.Error("User profile not loaded yet.")) }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isSubmitting = true,
                    submitState = UiState.Loading
                )
            }

            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val result = telegramFeedbackSender.sendFeedback(
                timestamp = timestamp,
                username = "@${user.login}",
                feedbackBody = feedback,
                attachments = attachments
            )

            result.fold(
                onSuccess = {
                    _state.update {
                        it.copy(
                            feedbackText = "",
                            attachmentCount = 0,
                            isSubmitting = false,
                            submitState = UiState.Success(true)
                        )
                    }
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            isSubmitting = false,
                            submitState = UiState.Error(error.message ?: "Failed to submit feedback.")
                        )
                    }
                }
            )
        }
    }

    fun consumeSubmitState() {
        _state.update { it.copy(submitState = UiState.Idle) }
    }
}
