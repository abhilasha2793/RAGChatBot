package com.abhilasha.ragchatapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.abhilasha.ragchatapp.ui.chat.ChatScreen
import com.abhilasha.ragchatapp.ui.upload.UploadScreen
import com.abhilasha.ragchatapp.viewmodel.ChatViewModel

object Routes {
    const val UPLOAD = "upload"
    const val CHAT = "chat"
}

@Composable
fun NavGraph(
    modifier: Modifier = Modifier
) {

    val navController = rememberNavController()

    val chatViewModel: ChatViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.UPLOAD,
        modifier = modifier
    ) {

        composable(Routes.UPLOAD) {

            UploadScreen(
                navController = navController,
                viewModel = chatViewModel
            )

        }

        composable(Routes.CHAT) {

            ChatScreen(
                navController = navController,
                viewModel = chatViewModel
            )

        }

    }

}