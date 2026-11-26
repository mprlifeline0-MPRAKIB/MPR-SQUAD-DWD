package com.mprsquad.dwd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Navy = Color(0xFF071426)
private val Panel = Color(0xFF10243B)
private val Cyan = Color(0xFF32D7F5)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { DwdApp() }
    }
}

private data class Module(val title: String, val icon: ImageVector, val subtitle: String)

@Composable
private fun DwdApp() {
    val modules = listOf(
        Module("Duty", Icons.Default.Work, "ডিউটি ও ছুটির হিসাব"),
        Module("Bill", Icons.Default.ReceiptLong, "বিল ও পেমেন্ট"),
        Module("Expense", Icons.Default.Payments, "দৈনিক খরচ"),
        Module("Person Accounts", Icons.Default.People, "ব্যক্তিভিত্তিক হিসাব"),
        Module("Location Accounts", Icons.Default.Place, "লোকেশনভিত্তিক হিসাব"),
        Module("Dashboard", Icons.Default.Dashboard, "সম্মিলিত রিপোর্ট")
    )
    var selected by remember { mutableStateOf<String?>(null) }
    var menuOpen by remember { mutableStateOf(false) }
    var showExit by remember { mutableStateOf(false) }

    BackHandler(enabled = selected != null || menuOpen) {
        if (menuOpen) menuOpen = false else selected = null
    }
    BackHandler(enabled = selected == null && !menuOpen) { showExit = true }

    MaterialTheme(colorScheme = darkColorScheme(
        background = Navy, surface = Panel, primary = Cyan,
        onBackground = Color.White, onSurface = Color.White
    )) {
        Box(Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFF0B2038), Navy, Color(0xFF030A13)))
        )) {
            Column(Modifier.fillMaxSize().padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { menuOpen = true }) {
                        Icon(Icons.Default.Menu, "Menu", tint = Cyan)
                    }
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = { /* Search implementation is planned */ }) {
                        Icon(Icons.Default.Search, "Search", tint = Cyan)
                    }
                }
                Text("MPR SQUAD™", color = Cyan, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("DWD", color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.ExtraBold)
                Text("DAILY WORK DETAILS", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("unmatched in every spot", color = Cyan, fontSize = 12.sp)
                Text("আজকের তারিখ ও সময় • ডিভাইসের স্থানীয় সময়", color = Color.LightGray, fontSize = 12.sp)
                Spacer(Modifier.height(22.dp))
                if (selected == null) {
                    Text("MAIN MODULES", color = Cyan, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    LazyVerticalGrid(columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(modules) { m ->
                            Card(
                                modifier = Modifier.fillMaxWidth().height(142.dp).clickable { selected = m.title },
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Panel)
                            ) {
                                Column(Modifier.fillMaxSize().padding(15.dp), verticalArrangement = Arrangement.SpaceBetween) {
                                    Icon(m.icon, null, tint = Cyan, modifier = Modifier.size(30.dp))
                                    Column {
                                        Text(m.title, color = Color.White, fontWeight = FontWeight.Bold)
                                        Text(m.subtitle, color = Color.LightGray, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Text(selected!!, color = Cyan, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                    Text("এই মডিউলের ফর্ম, ডাটাবেস, PDF ও হিসাবের ফিচার পরবর্তী বাস্তবায়ন ধাপে যুক্ত হবে।",
                        color = Color.White)
                    Spacer(Modifier.height(16.dp))
                    OutlinedButton(onClick = { selected = null }) { Text("ফিরে যান") }
                }
            }
            if (menuOpen) {
                AlertDialog(onDismissRequest = { menuOpen = false },
                    title = { Text("অতিরিক্ত মেনু") },
                    text = { Text("Office Contract\nStock Inventory\nSettings\nBackup & Restore\nApp Info / Developer") },
                    confirmButton = { TextButton(onClick = { menuOpen = false }) { Text("বন্ধ করুন") } })
            }
            if (showExit) {
                AlertDialog(onDismissRequest = { showExit = false },
                    title = { Text("অ্যাপ বন্ধ করবেন?") },
                    text = { Text("MPR SQUAD DWD থেকে বের হতে চান?") },
                    confirmButton = { TextButton(onClick = { showExit = false }) { Text("না") } },
                    dismissButton = { TextButton(onClick = { showExit = false }) { Text("হ্যাঁ") } })
            }
        }
    }
}
