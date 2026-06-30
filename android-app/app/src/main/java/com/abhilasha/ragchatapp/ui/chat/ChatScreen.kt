package com.abhilasha.ragchatapp.ui.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.abhilasha.ragchatapp.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    viewModel: ChatViewModel
) {

    val messages by viewModel.messages.collectAsState()

    val loading by viewModel.isLoading.collectAsState()
    
    val error by viewModel.error.collectAsState()

    var question by remember {
        mutableStateOf("")
    }

    val listState = rememberLazyListState()

    // Automatically scroll to the bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Text("RAG Chat")

                },
                
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }

            )

        }

    ) { padding ->

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(padding)

        ) {

            LazyColumn(

                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),

                state = listState

            ) {

                items(messages) { message ->

                    MessageBubble(message)

                }

            }

            if (loading) {

                Row(

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),

                    horizontalArrangement = Arrangement.Center

                ) {

                    CircularProgressIndicator()

                }

            }
            
            error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Row(

                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(12.dp),

                verticalAlignment = Alignment.CenterVertically

            ) {

                OutlinedTextField(

                    modifier = Modifier.weight(1f),

                    value = question,

                    onValueChange = {

                        question = it

                    },

                    placeholder = {

                        Text("Ask something...")

                    }

                )

                FloatingActionButton(

                    modifier = Modifier.padding(start = 8.dp),

                    containerColor = MaterialTheme.colorScheme.primary,

                    onClick = {

                        if (question.isNotBlank()) {

                            viewModel.askQuestion(question)

                            question = ""

                        }

                    }

                ) {

                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send"
                    )

                }

            }

        }

    }

}