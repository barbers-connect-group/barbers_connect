package com.example.barbers_connect.ui.barbershop

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.barbers_connect.service.BarberShopService
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReviewScreen(
    barbershopId: Int,
    barbershopName: String,
    onNavigateBack: () -> Unit
) {
    var rating by remember { mutableIntStateOf(0) }
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val isFormValid = rating > 0 && description.isNotBlank()

    val createTempFile = {
        try {
            File.createTempFile("image_", ".jpg", context.cacheDir)
        } catch (e: IOException) {
            null
        }
    }

    var tempImageFile by remember { mutableStateOf(createTempFile()) }

    var tempImageUri by remember {
        mutableStateOf(
            tempImageFile?.let {
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    it
                )
            }
        )
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempImageUri != null) {
            imageUri = tempImageUri
        }
    }

    val prepareCamera = {
        tempImageFile = createTempFile()
        tempImageUri = tempImageFile?.let {
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                it
            )
        }


        tempImageUri?.let { uri ->
            cameraLauncher.launch(uri)
        }
    }

    val submitReview = {
        if (isFormValid) {
            isSubmitting = true


            val imageFile = imageUri?.let { uri ->
                try {
                    if (uri.toString().startsWith("content://")) {
                        val inputStream = context.contentResolver.openInputStream(uri)
                        val outputFile = File(context.cacheDir, "review_image.jpg")
                        inputStream?.use { input ->
                            outputFile.outputStream().use { output ->
                                input.copyTo(output)
                            }
                        }
                        outputFile
                    } else if (uri.toString().startsWith("file://")) {
                        File(uri.path ?: "")
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    null
                }
            }

            val compressedImageFile = imageFile?.let { compressImage(it, context) }

            BarberShopService.createReview(
                context = context,
                barbershopId = barbershopId,
                description = description,
                rating = rating,
                imageFile = compressedImageFile
            ) { result ->
                isSubmitting = false

                Toast.makeText(
                    context,
                    result["message"] as String,
                    Toast.LENGTH_LONG
                ).show()

                if (result["status"] == "success") {
                    onNavigateBack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Adicionar Avaliação") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))


            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Barbershop name section
                    Column {
                        Text(
                            text = "Barbearia",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = barbershopName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 2. Star Rating section
                    Column {
                        Text(
                            text = "Avalie o serviço",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        StarRating(
                            rating = rating,
                            onRatingChanged = { rating = it },
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 3. Description section
                    Column {
                        Text(
                            text = "Descreva sua experiência",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            placeholder = { Text("Como foi sua experiência na barbearia?") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 4. Camera/Photo section
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Compartilhe seu look",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(12.dp))


                        val currentImageUri = imageUri
                        if (currentImageUri != null) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                            ) {
                                AsyncImage(
                                    model = currentImageUri,
                                    contentDescription = "Imagem selecionada",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(200.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .align(Alignment.Center)
                                )

                                IconButton(
                                    onClick = { imageUri = null },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(36.dp)
                                        .padding(4.dp)
                                        .clip(RoundedCornerShape(50))
                                        .background(MaterialTheme.colorScheme.errorContainer)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remover foto",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedButton(
                                onClick = { prepareCamera() }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Camera,
                                    contentDescription = "Trocar foto",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Trocar foto")
                            }
                        } else {
                            Button(
                                onClick = { prepareCamera() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Camera,
                                    contentDescription = "Tirar foto",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Tirar Foto")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { submitReview() },
                enabled = isFormValid && !isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Enviando...", color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text(
                        "Enviar Avaliação",
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = if (isFormValid) 1f else 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun StarRating(
    rating: Int,
    onRatingChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(5) { index ->
            val starIndex = index + 1
            IconButton(
                onClick = { onRatingChanged(starIndex) },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = if (starIndex <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = "Estrela $starIndex",
                    tint = if (starIndex <= rating) Color(0xFFFFD700) else Color.Gray,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

private fun compressImage(imageFile: File, context: Context): File {
    val maxWidth = 1024
    val maxHeight = 1024
    val quality = 80 // 0-100, higher is better quality but larger file

    val outputFile = File(context.cacheDir, "compressed_${imageFile.name}")

    try {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeFile(imageFile.path, options)

        // Calculate sample size
        var width = options.outWidth
        var height = options.outHeight
        var scale = 1

        while (width > maxWidth || height > maxHeight) {
            width /= 2
            height /= 2
            scale *= 2
        }

        // Decode with sample size
        val decodeOptions = BitmapFactory.Options().apply {
            inSampleSize = scale
        }
        val bitmap = BitmapFactory.decodeFile(imageFile.path, decodeOptions)

        // Compress and save
        val out = FileOutputStream(outputFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
        out.flush()
        out.close()

        return outputFile
    } catch (e: Exception) {
        e.printStackTrace()
        return imageFile
    }
}