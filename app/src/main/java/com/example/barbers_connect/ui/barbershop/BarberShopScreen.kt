package com.example.barbers_connect.ui.barbershop

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.barbers_connect.R
import com.example.barbers_connect.model.BarberShop
import com.example.barbers_connect.service.BarberShopService

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
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
    ) {
//        Column (
////            horizontalAlignment = Alignment.CenterHorizontally,
//            modifier = modifier
//                .height(350.dp)
//                .padding(16.dp)
//        ){
//            Row (
//                verticalAlignment = Alignment.Top,
//                horizontalArrangement = Arrangement.Center,
//                modifier = modifier.height(150.dp)
//            ){
//                Image(
//                painter = painterResource(R.drawable.barbearia_mock),
//                contentDescription = null,
//                contentScale = ContentScale.Crop,
//                modifier = modifier.height(50.dp)
//                )
//            }
//            Spacer(Modifier.height(16.dp))
//            Row (
//                verticalAlignment = Alignment.Bottom,
//                modifier = modifier.height(150.dp)
//            ){
//                Column (
//                    horizontalAlignment = Alignment.CenterHorizontally,
////                    modifier = modifier.height(100.dp)
//                ){
//                    Text(
//                        text = name,
//                        style = MaterialTheme.typography.titleMedium,
//                        color = Color(0xFF795548),
//                    )
//                    Text(
//                        text = address,
//                        style = MaterialTheme.typography.titleMedium,
//                        color = Color(0xFF795548),
//                    )
//                    Spacer(Modifier.height(16.dp))
//                    Text(
//                        text = "$startShift - $endShift",
//                        style = MaterialTheme.typography.titleMedium,
//                        color = Color(0xFF795548),
//                    )
//                    Spacer(Modifier.height(16.dp))
//                TextButton(onClick = onClickNavigate) {
//                    Text(
//                        "Agendar agora",
//                        color = Color(0xFF795548),
//                        textDecoration = TextDecoration.Underline
//                    )
//                }
//                ExtendedButton(text = "Agendar agora", color = Color(0xFF795548), onClick = onClickNavigate)
//            }
//            }

        Column(
            modifier = Modifier
                .fillMaxWidth()
//                .padding(16.dp)
        ) {
            // Row para a Imagem ocupando toda a largura
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.barbearia_mock),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.7f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(0.5f)
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
                }
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
                .weight(0.3f),
                verticalAlignment = Alignment.CenterVertically
            ){
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
            items(barbershopList.orEmpty()) { barbershop ->
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