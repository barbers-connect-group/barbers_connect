package com.example.barbers_connect.ui.barbershop

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.barbers_connect.R
import com.example.barbers_connect.model.BarberShop
import com.example.barbers_connect.service.BarberShopService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ExtendedButton(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF795548),
    onClick: () -> Unit
) {
    ExtendedFloatingActionButton (
        onClick = { onClick() },
        icon = { Icon(Icons.Filled.CalendarToday, "") },
        text = {Text(text = text)},
        contentColor = color,
        modifier = modifier
    )
}

@Composable
fun BarbershopCard(
    name: String,
    address: String,
    startShift: String,
    endShift: String,
    onClickNavigate: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isImageLoading by remember { mutableStateOf(true) }

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Image with loading state
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                // Show placeholder while "loading"
                if (isImageLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.LightGray)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                // Actual image loaded with a side effect to track loading state
                Image(
                    painter = painterResource(id = R.drawable.barbearia_mock),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .onSizeChanged {
                            // This gets called after layout, so image should be loaded
                            isImageLoading = false
                        }
                )
            }

            // Info section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF795548),
                )
                Text(
                    text = address,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF795548),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$startShift - $endShift",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF795548),
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Button
                ExtendedButton(
                    text = "Agendar agora",
                    color = Color(0xFF795548),
                    onClick = onClickNavigate,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun BarberShopsScreen(
    barbershopId: Int,
    onChangeBarbershopId: (Int) -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    onLogout: () -> Unit,
) {
    val context = LocalContext.current
    var message by remember { mutableStateOf("") }
    var barbershopList by remember { mutableStateOf<List<BarberShop>?>(null) }

    LaunchedEffect(Unit) {
        BarberShopService.getAllBarberShops(context) { result ->
            val status = result["status"].toString()
            val data = result["data"]
            message = result["message"].toString()

            if (status == "success" && data is List<*>) {
                barbershopList = data.filterIsInstance<BarberShop>()
            }

        }
    }
    if (barbershopList != null) Log.d("barbersList", barbershopList.toString())
    if (message.isNotEmpty()) {
        Text(text = message, color = Color.Red)
    }
    barbershopList?.let {

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            modifier = modifier
        ) {
            items(
                items = barbershopList.orEmpty(),
                key = { it.id ?: it.hashCode() }
            ) { barbershop ->
                BarbershopCard(
                    name = barbershop.name,
                    address = barbershop.address,
                    startShift = barbershop.startShift,
                    endShift = barbershop.endShift,
                    onClickNavigate = { barbershop.id?.let { id ->
                        onChangeBarbershopId(id)
                        onNavigateToProfile()
                    } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .height(300.dp)

                )
            }
        }
    }
}