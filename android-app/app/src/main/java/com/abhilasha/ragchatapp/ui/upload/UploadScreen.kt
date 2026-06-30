package com.abhilasha.ragchatapp.ui.upload

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.abhilasha.ragchatapp.ui.navigation.Routes
import com.abhilasha.ragchatapp.viewmodel.ChatViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadScreen(
    navController: NavController,
    viewModel: ChatViewModel
) {

    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val loading by viewModel.isLoading.collectAsState()

    val uploadResponse by viewModel.uploadResponse.collectAsState()

    val error by viewModel.error.collectAsState()

    var selectedUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var fileName by remember {
        mutableStateOf("")
    }

    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()
        ) { uri ->

            uri?.let {

                selectedUri = it

                fileName = getFileName(
                    context,
                    it
                )

            }

        }

    LaunchedEffect(uploadResponse) {

        uploadResponse?.let {

            scope.launch {

                snackbarHostState.showSnackbar(
                    "PDF uploaded successfully"
                )

            }

            navController.navigate(Routes.CHAT)

        }

    }

    LaunchedEffect(error) {

        error?.let {

            scope.launch {

                snackbarHostState.showSnackbar(it)

            }

            viewModel.clearError()

        }

    }

    Scaffold(

        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },

        topBar = {

            TopAppBar(
                title = {
                    Text("GenAI RAG Chat")
                }
            )

        }

    ) { padding ->

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),

            verticalArrangement = Arrangement.Center,

            horizontalAlignment = Alignment.CenterHorizontally

        ) {

            Text(
                text = "Upload PDF",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(30.dp))

            OutlinedButton(

                onClick = {

                    launcher.launch(
                        arrayOf("application/pdf")
                    )

                }

            ) {

                Text("Choose PDF")

            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(

                value = fileName,

                onValueChange = {},

                readOnly = true,

                modifier = Modifier.fillMaxWidth(),

                label = {

                    Text("Selected File")

                }

            )

            Spacer(modifier = Modifier.height(30.dp))

            Button(

                enabled = !loading && selectedUri != null,

                modifier = Modifier.fillMaxWidth(),

                onClick = {

                    selectedUri?.let {

                        uploadPdf(
                            context = context,
                            uri = it,
                            viewModel = viewModel
                        )

                    }

                }

            ) {

                Text("Upload")

            }

            Spacer(modifier = Modifier.height(30.dp))

            if (loading) {

                CircularProgressIndicator()

            }

        }

    }

}

/**
 * Upload PDF to backend
 */
private fun uploadPdf(
    context: Context,
    uri: Uri,
    viewModel: ChatViewModel
) {

    val file = createTempFile(
        context = context,
        uri = uri
    )

    val requestBody = file.asRequestBody(
        "application/pdf".toMediaType()
    )

    val multipartFile = MultipartBody.Part.createFormData(
        name = "file",
        filename = file.name,
        body = requestBody
    )

    viewModel.uploadPdf(multipartFile)

}

/**
 * Get selected file name
 */
private fun getFileName(
    context: Context,
    uri: Uri
): String {

    var name = "document.pdf"

    val cursor = context.contentResolver.query(
        uri,
        null,
        null,
        null,
        null
    )

    cursor?.use {

        val index =
            it.getColumnIndex(OpenableColumns.DISPLAY_NAME)

        if (it.moveToFirst() && index >= 0) {

            name = it.getString(index)

        }

    }

    return name

}
/**
 * Copy URI to temporary File
 */
private fun createTempFile(
    context: Context,
    uri: Uri
): File {

    val fileName = getFileName(
        context,
        uri
    )

    val tempFile = File(
        context.cacheDir,
        fileName
    )

    context.contentResolver.openInputStream(uri)?.use { input ->

        FileOutputStream(tempFile).use { output ->

            input.copyTo(output)

        }

    }

    return tempFile

}