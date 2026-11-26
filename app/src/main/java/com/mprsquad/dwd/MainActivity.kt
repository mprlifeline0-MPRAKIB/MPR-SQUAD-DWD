package com.mprsquad.dwd

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Navy = Color(0xFF061426)
private val Panel = Color(0xFF102A43)
private val Cyan = Color(0xFF36DDF4)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { DwdApp(onExit = { finish() }) }
    }
}

private data class HomeModule(val title: String, val icon: ImageVector, val color: Color)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DwdApp(onExit: () -> Unit) {
    val modules = listOf(
        HomeModule("ডিউটি ও ছুটির হিসাব", Icons.Default.CalendarMonth, Color(0xFF087BA8)),
        HomeModule("বিলের হিসাব", Icons.Default.ReceiptLong, Color(0xFF087D68)),
        HomeModule("খরচের হিসাব", Icons.Default.Payments, Color(0xFF9A520B)),
        HomeModule("ব্যক্তিভিত্তিক হিসাব", Icons.Default.People, Color(0xFF51409A)),
        HomeModule("স্থানভিত্তিক হিসাব", Icons.Default.Place, Color(0xFF176A9A)),
        HomeModule("হিসাবের সারসংক্ষেপ", Icons.Default.QueryStats, Color(0xFF176A9A))
    )
    var page by remember { mutableStateOf<String?>(null) }
    var drawer by remember { mutableStateOf(false) }
    var exitDialog by remember { mutableStateOf(false) }
    var officeExpanded by remember { mutableStateOf(false) }
    var settingsExpanded by remember { mutableStateOf(false) }
    val dateText = remember { SimpleDateFormat("dd MMMM yyyy, EEEE", Locale("bn", "BD")).format(Date()) }
    val timeText = remember { SimpleDateFormat("hh:mm a", Locale("bn", "BD")).format(Date()) }

    BackHandler(enabled = page != null || drawer) {
        if (drawer) drawer = false else page = null
    }
    BackHandler(enabled = page == null && !drawer) { exitDialog = true }

    MaterialTheme(colorScheme = darkColorScheme(
        primary = Cyan, background = Navy, surface = Panel,
        onBackground = Color.White, onSurface = Color.White
    )) {
        Box(Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFF0A2942), Navy, Color(0xFF020810)))
        )) {
            if (page == null) {
                Column(Modifier.fillMaxSize()) {
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text("MPR SQUAD™", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)
                            Text("DAILY WORK DETAILS", color = Color(0xFFD3E9F4), fontSize = 13.sp)
                        }
                        Button(onClick = { drawer = true }, shape = RoundedCornerShape(16.dp)) {
                            Icon(Icons.Default.Menu, null)
                            Spacer(Modifier.width(6.dp))
                            Text("মেনু")
                        }
                        Spacer(Modifier.width(8.dp))
                        IconButton(onClick = { page = "সার্চ" }) {
                            Icon(Icons.Default.Search, "সার্চ", tint = Cyan)
                        }
                    }
                    HorizontalDivider(color = Cyan.copy(alpha = .6f))
                    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                        Spacer(Modifier.height(14.dp))
                        // Brand panel reserved for the supplied portrait and finished DWD artwork.
                        Card(
                            modifier = Modifier.fillMaxWidth().height(190.dp),
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF101D32)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Cyan.copy(alpha = .8f))
                        ) {
                            Box(Modifier.fillMaxSize().background(
                                Brush.horizontalGradient(listOf(Color(0xFF071426), Color(0xFF123F59), Color(0xFF24143C)))
                            ).padding(16.dp), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("MPR SQUAD™", color = Color(0xFFFFD98A), fontSize = 17.sp, fontWeight = FontWeight.Bold)
                                    Text("DWD", color = Cyan, fontSize = 58.sp, fontWeight = FontWeight.Black)
                                    Text("Daily Work Details", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                    Text("unmatched in every spot", color = Color(0xFFFFD98A), fontSize = 12.sp)
                                    Spacer(Modifier.height(8.dp))
                                    Text("MP RAKIB  •  Founder & Lead Developer", color = Color.White, fontSize = 11.sp)
                                }
                            }
                        }
                        Spacer(Modifier.height(14.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF124A68)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD36B))
                        ) {
                            Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text("সময়", color = Color.LightGray, fontSize = 12.sp)
                                    Text(timeText, color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                                }
                                VerticalDivider(Modifier.height(42.dp), color = Color.LightGray)
                                Spacer(Modifier.width(14.dp))
                                Column(Modifier.weight(1.6f)) {
                                    Text("তারিখ", color = Color.LightGray, fontSize = 12.sp)
                                    Text(dateText, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(bottom = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(modules) { item ->
                                Card(
                                    modifier = Modifier.fillMaxWidth().height(124.dp).clickable { page = item.title },
                                    shape = RoundedCornerShape(22.dp),
                                    colors = CardDefaults.cardColors(containerColor = item.color),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = .25f))
                                ) {
                                    Column(
                                        Modifier.fillMaxSize().padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(item.icon, null, tint = Color.White, modifier = Modifier.size(34.dp))
                                        Spacer(Modifier.height(9.dp))
                                        Text(item.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (page == "অ্যাপ তথ্য / ডেভেলপার") {
                AboutInfo(onBack = { page = null })
            } else {
                Column(Modifier.fillMaxSize().padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { page = null }) { Icon(Icons.Default.ArrowBack, "ফিরে যান", tint = Cyan) }
                        Text(page!!, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }
                    Text("এই মডিউলের পূর্ণ ফর্ম, হিসাব, সম্পাদনা, মুছে ফেলা ও PDF এখনো বাস্তবায়নাধীন।",
                        color = Color.LightGray)
                }
            }

            if (drawer) {
                ModalNavigationDrawer(
                    drawerState = rememberDrawerState(DrawerValue.Open),
                    drawerContent = {
                        ModalDrawerSheet(
                            modifier = Modifier.fillMaxHeight().width(310.dp),
                            drawerContainerColor = Color(0xFFF0F5FA)
                        ) {
                            Spacer(Modifier.height(26.dp))
                            Text("MPR SQUAD™", modifier = Modifier.padding(20.dp), color = Color(0xFF12324C), fontWeight = FontWeight.Black, fontSize = 21.sp)
                            NavigationDrawerItem(label = { Text("🏠  হোম") }, selected = false, onClick = { page = null; drawer = false })
                            NavigationDrawerItem(
                                label = { Text("🗓️  অফিস কন্ট্রাক্ট") },
                                selected = false,
                                onClick = { officeExpanded = !officeExpanded }
                            )
                            if (officeExpanded) {
                                NavigationDrawerItem(label = { Text("মাসিক বেতন ও মোবাইল বিল") }, selected = false, onClick = { page = "অফিস কন্ট্রাক্ট"; drawer = false })
                                NavigationDrawerItem(label = { Text("বোনাস") }, selected = false, onClick = { page = "বোনাস"; drawer = false })
                            }
                            NavigationDrawerItem(
                                label = { Text("⚙️  সেটিংস ও অন্যান্য") },
                                selected = false,
                                onClick = { settingsExpanded = !settingsExpanded }
                            )
                            if (settingsExpanded) {
                                listOf("স্টক ইনভেন্টরি", "রিমাইন্ডার ও থিম", "ব্যাকআপ ও রিস্টোর", "অ্যাপ তথ্য / ডেভেলপার").forEach { label ->
                                    NavigationDrawerItem(label = { Text(label) }, selected = false, onClick = { page = label; drawer = false })
                                }
                            }
                            NavigationDrawerItem(
                                label = { Text("📲  সম্পূর্ণ অ্যাপ Share / অন্যকে পাঠান") },
                                selected = false,
                                onClick = { page = "অ্যাপ Share"; drawer = false }
                            )
                            Spacer(Modifier.height(10.dp))
                            NavigationDrawerItem(label = { Text("✕  বন্ধ করুন") }, selected = false, onClick = { drawer = false })
                        }
                    },
                    content = { }
                )
            }
            if (exitDialog) {
                AlertDialog(
                    onDismissRequest = { exitDialog = false },
                    title = { Text("অ্যাপ বন্ধ করবেন?") },
                    text = { Text("MPR SQUAD DWD থেকে বের হতে চান?") },
                    confirmButton = { TextButton(onClick = { exitDialog = false }) { Text("না") } },
                    dismissButton = { TextButton(onClick = onExit) { Text("হ্যাঁ") } }
                )
            }
        }
    }
}


@Composable
private fun AboutInfo(onBack: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "ফিরে যান", tint = Cyan) }
            Text("অ্যাপ তথ্য / ডেভেলপার", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(22.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF102C48)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Cyan)
        ) {
            Column(
                Modifier.fillMaxWidth().padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    Modifier.size(104.dp).background(
                        Brush.linearGradient(listOf(Color(0xFF147DA7), Color(0xFF17264B))),
                        RoundedCornerShape(52.dp)
                    ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("DWD", color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Black)
                }
                Spacer(Modifier.height(16.dp))
                Text("MP RAKIB", color = Color.White, fontSize = 27.sp, fontWeight = FontWeight.Black)
                Text("Founder & Lead Developer", color = Color(0xFFBBD2E2), fontSize = 14.sp)
                Spacer(Modifier.height(22.dp))
                Text("DWD", color = Cyan, fontSize = 58.sp, fontWeight = FontWeight.Black)
                Text("Daily Work Details", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                Text("MPR SQUAD™", color = Color(0xFFFFD98A), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("unmatched in every spot", color = Color(0xFFBBD2E2), fontSize = 12.sp)
                Spacer(Modifier.height(24.dp))
                HorizontalDivider(color = Cyan.copy(alpha = .35f))
                Spacer(Modifier.height(16.dp))
                Text("Developer Contact", color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text("Contact : 019090-62744", color = Color(0xFFE5F1F8), fontSize = 15.sp)
                Text("Email : mpr.lifeline0@gmail.com", color = Color(0xFFE5F1F8), fontSize = 14.sp)
                Spacer(Modifier.height(20.dp))
                HorizontalDivider(color = Cyan.copy(alpha = .35f))
                Spacer(Modifier.height(12.dp))
                Text("Version : 0.1.0", color = Color.LightGray, fontSize = 11.sp)
                Text("Release Date : প্রকাশের সময় নির্ধারিত হবে", color = Color.LightGray, fontSize = 11.sp)
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(
            "ডেভেলপার পরিচিতি ও যোগাযোগের তথ্য",
            color = Color(0xFF9BB6C8),
            fontSize = 11.sp
        )
    }
}
