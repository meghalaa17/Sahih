package com.sahih.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.sahih.data.NewsItem
import com.sahih.ui.theme.CardBg
import com.sahih.ui.theme.Gold
import com.sahih.ui.theme.Hairline
import com.sahih.ui.theme.Ink
import com.sahih.ui.theme.TextMuted
import com.sahih.ui.theme.TextPrimary

private data class FeatureTile(val icon: ImageVector, val title: String, val desc: String, val route: String)

private val features = listOf(
    FeatureTile(Icons.Default.Share, "Share and check", "Share from WhatsApp, IG, TikTok", "verify"),
    FeatureTile(Icons.Default.ShoppingCart, "Checkout guard", "Catches mismatches before you pay", "checkout"),
    FeatureTile(Icons.Default.Call, "Call detector", "Runs in the background", "callshield"),
    FeatureTile(Icons.Default.FactCheck, "Reporting radar", "Evidence packs to SSM and NSRC", "radar"),
    FeatureTile(Icons.Default.Person, "Seller check", "Score an IG/Telegram seller", "sellercheck"),
)

@Composable
fun HomeScreen(navController: NavHostController, newsViewModel: NewsViewModel = viewModel()) {

    // IMPORTANT: collectAsState() must be called here, in the composable function body,
    // NOT inside the LazyColumn { } block below. LazyListScope's block is not itself
    // a @Composable context, so state reads and other composable calls placed directly
    // inside it (outside an item{}/items{} content lambda) will fail to compile.
    val state by newsViewModel.state.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        item {
            // --- Welcome intro ---
            Text("Welcome to", color = TextMuted, fontSize = 13.sp)
            Text("Sahih", color = TextPrimary, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(
                "Your shield against scams in Malaysia",
                color = TextMuted,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(Modifier.height(18.dp))

            // --- Greeting ---
            Text("Selamat petang, Aisyah", color = TextMuted, fontSize = 13.sp)

            Spacer(Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardBg, RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = Gold,
                        modifier = Modifier.height(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "No need to open the app",
                        color = Gold,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    "Tap \"Share to Sahih\" from any app to check a seller, number or link",
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    lineHeight = 22.sp
                )
                Spacer(Modifier.height(14.dp))
                Button(
                    onClick = { navController.navigate("verify") },
                    colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Ink),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("See how it works", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(16.dp))

            // --- Feature grid: 2x2, built with Rows instead of a nested LazyVerticalGrid ---
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                features.chunked(2).forEach { rowItems ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        rowItems.forEach { f ->
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(CardBg, RoundedCornerShape(16.dp))
                                    .clickable { navController.navigate(f.route) }
                                    .padding(14.dp)
                            ) {
                                Icon(
                                    f.icon,
                                    contentDescription = f.title,
                                    tint = Gold,
                                    modifier = Modifier.height(18.dp)
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(f.title, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Spacer(Modifier.height(2.dp))
                                Text(f.desc, color = TextMuted, fontSize = 11.sp, lineHeight = 15.sp)
                            }
                        }
                        // Fill remaining space if the last row has an odd item out
                        if (rowItems.size < 2) Spacer(Modifier.weight(1f))
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                "Scam alerts & government updates",
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
            Spacer(Modifier.height(10.dp))
        }

        // --- News items ---
        when (val s = state) {
            is NewsUiState.Loading -> item {
                Text("Loading news…", color = TextMuted, fontSize = 12.sp)
            }
            is NewsUiState.Error -> item {
                Text("Couldn't load news right now", color = TextMuted, fontSize = 12.sp)
            }
            is NewsUiState.Success -> {
                if (s.items.isEmpty()) {
                    item {
                        Text("No scam alerts right now", color = TextMuted, fontSize = 12.sp)
                    }
                } else {
                    items(s.items) { newsItem: NewsItem ->
                        NewsCard(newsItem, modifier = Modifier.padding(bottom = 8.dp))
                    }
                }
            }
        }

        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun NewsCard(item: NewsItem, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(CardBg, RoundedCornerShape(14.dp))
            .clickable {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.link)))
            }
            .padding(12.dp)
    ) {
        Text(item.title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(4.dp))
        Text(item.source, color = Gold, fontSize = 11.sp)
    }
}