package com.abhilasha.ragchatapp.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Immutable
data class ChatMessage(
    val text: String,
    val isUser: Boolean
)

@androidx.compose.runtime.Composable
fun MessageBubble(
    message: ChatMessage
) {

    val arrangement =
        if (message.isUser)
            Arrangement.End
        else
            Arrangement.Start

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 12.dp,
                vertical = 6.dp
            ),
        horizontalArrangement = arrangement
    ) {

        Card(

            shape = RoundedCornerShape(16.dp),

            colors = CardDefaults.cardColors(

                containerColor =
                    if (message.isUser)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.surfaceVariant

            )

        ) {

            Box(

                modifier = Modifier
                    .background(
                        if (message.isUser)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                    .padding(14.dp)

            ) {

                Text(

                    text = message.text,

                    color =
                        if (message.isUser)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onSurface,

                    style = MaterialTheme.typography.bodyLarge,

                    fontWeight = FontWeight.Normal

                )

            }

        }

    }

}