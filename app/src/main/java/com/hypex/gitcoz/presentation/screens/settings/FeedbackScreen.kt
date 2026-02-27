package com.hypex.gitcoz.presentation.screens.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hypex.gitcoz.R
import com.hypex.gitcoz.data.feedback.FeedbackAttachment
import com.hypex.gitcoz.presentation.AppViewModelProvider
import com.hypex.gitcoz.presentation.UiState
import com.hypex.gitcoz.presentation.components.LoadingCardListPlaceholder
import com.hypex.gitcoz.presentation.components.ProfileCard
import com.hypex.gitcoz.presentation.components.SaaSTopBar
import com.hypex.gitcoz.ui.theme.CardButton
import com.hypex.gitcoz.ui.theme.CardPrimaryText
import com.hypex.gitcoz.ui.theme.CardSecondaryText
import com.hypex.gitcoz.ui.theme.ElectricViolet
import kotlinx.coroutines.delay

@Composable
fun FeedbackScreen(
    onBack: () -> Unit,
    viewModel: FeedbackViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val attachments = remember { mutableStateListOf<FeedbackAttachment>() }
    var showThanksTitle by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        val newAttachments = uris.mapNotNull { uri ->
            runCatching {
                val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                if (bytes == null || bytes.isEmpty()) {
                    null
                } else {
                    val safeBytes: ByteArray = bytes
                    FeedbackAttachment(
                        fileName = uri.lastPathSegment ?: "attachment.jpg",
                        mimeType = context.contentResolver.getType(uri) ?: "image/jpeg",
                        bytes = safeBytes
                    )
                }
            }.getOrNull()
        }
        attachments.clear()
        attachments.addAll(newAttachments)
        viewModel.onAttachmentCountChanged(attachments.size)
    }

    LaunchedEffect(state.submitState) {
        if (state.submitState is UiState.Success) {
            attachments.clear()
            showThanksTitle = true
            delay(2200)
            showThanksTitle = false
            viewModel.consumeSubmitState()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = showThanksTitle,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "feedback_topbar_transition"
        ) { thanksState ->
            SaaSTopBar(
                title = if (thanksState) {
                    "thanks for your feedback. feedback send"
                } else {
                    "Feedback"
                },
                subtitle = if (thanksState) {
                    "Message delivered successfully"
                } else {
                    "Tell us what to improve"
                },
                actions = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp)
        ) {
            item {
                when (val userState = state.userState) {
                    is UiState.Loading -> LoadingCardListPlaceholder(count = 1)
                    is UiState.Success -> ProfileCard(user = userState.data)
                    is UiState.Error -> Text(
                        text = userState.message,
                        color = MaterialTheme.colorScheme.error
                    )
                    else -> {}
                }
            }

            item {
                Text(
                    text = "Your Feedback",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CardPrimaryText
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.feedbackText,
                    onValueChange = viewModel::onFeedbackChanged,
                    placeholder = { Text("Write your feedback here...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .border(
                            width = 1.dp,
                            color = ElectricViolet.copy(alpha = 0.25f),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
                        ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CardButton,
                        unfocusedContainerColor = CardButton,
                        focusedBorderColor = ElectricViolet,
                        unfocusedBorderColor = ElectricViolet.copy(alpha = 0.3f),
                        cursorColor = ElectricViolet
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium
                )
            }

            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { launcher.launch("image/*") },
                    color = CardButton,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_attach_file),
                                contentDescription = null,
                                tint = ElectricViolet,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Add image attachments",
                                style = MaterialTheme.typography.bodyMedium,
                                color = CardPrimaryText
                            )
                        }
                        Text(
                            text = "${state.attachmentCount} selected",
                            style = MaterialTheme.typography.labelLarge,
                            color = CardSecondaryText
                        )
                    }
                }
            }

            item {
                if (state.submitState is UiState.Error) {
                    Text(
                        text = (state.submitState as UiState.Error).message,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Button(
                    onClick = { viewModel.submitFeedback(attachments.toList()) },
                    enabled = !state.isSubmitting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricViolet,
                        contentColor = Color.White,
                        disabledContainerColor = ElectricViolet.copy(alpha = 0.45f)
                    )
                ) {
                    Text(
                        text = if (state.isSubmitting) "Submitting..." else "Submit",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
