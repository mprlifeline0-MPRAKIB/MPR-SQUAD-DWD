package com.mprsquad.dwd

import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mprsquad.dwd.data.DwdDatabase
import java.time.LocalDate
import java.time.ZoneId
import java.time.Instant

@Composable
internal fun WorkOverviewScreen(title: String, locationMode: Boolean, onBack: () -> Unit) {
    val ctx = LocalContext.current
    val dao = remember { DwdDatabase.get(ctx).dwdDao() }
    var fromText by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1).toString()) }
    var toText by remember { mutableStateOf(LocalDate.now().toString()) }
    var selectedLocation by remember { mutableStateOf("") }
    val from = remember(fromText) { runCatching { LocalDate.parse(fromText).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() }.getOrDefault(0L) }
    val to = remember(toText) { runCatching { LocalDate.parse(toText).plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()-1 }.getOrDefault(Long.MAX_VALUE) }
    val rows by (if (locationMode && selectedLocation.isNotBlank()) dao.observeLocation(selectedLocation) else dao.observeDashboard(from,to)).collectAsState(initial = emptyList())
    val filtered = rows.filter { it.recordDate in from..to }
    val places = rows.map { it.location }.distinct().sorted()
    val total = filtered.sumOf { it.amount + it.outboundFare + it.returnFare + it.mealDay + it.mealNight }
    val pdfLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
        if (uri != null) runCatching {
            val pdf = PdfDocument()
            val page = pdf.startPage(PdfDocument.PageInfo.Builder(595, 842, 1).create())
            val paint = Paint().apply { textSize = 12f; isAntiAlias = true }
            var y = 42f
            page.canvas.drawText("MPR SQUAD DWD - " + title, 36f, y, paint); y += 24f
            page.canvas.drawText("Period: " + fromText + " to " + toText + " | Records: " + filtered.size + " | Total BDT: " + total, 36f, y, paint); y += 28f
            filtered.take(45).forEach { row ->
                val line = row.location + " | " + row.title + " | BDT " + (row.amount + row.outboundFare + row.returnFare + row.mealDay + row.mealNight)
                page.canvas.drawText(line.take(95), 36f, y, paint); y += 16f
            }
            pdf.finishPage(page)
            context.contentResolver.openOutputStream(uri)?.use { pdf.writeTo(it) } ?: error("PDF destination unavailable")
            pdf.close()
        }
    }
    Column(Modifier.fillMaxSize().padding(14.dp)) {
        Row(verticalAlignment=Alignment.CenterVertically) {
            IconButton(onClick=onBack) { Icon(Icons.Default.ArrowBack,"ফিরে যান",tint=Color.Cyan) }
            Text(title,color=Color.White,fontSize=20.sp)
        }
        OutlinedTextField(fromText,{fromText=it},label={Text("শুরুর তারিখ YYYY-MM-DD")},modifier=Modifier.fillMaxWidth(),singleLine=true)
        OutlinedTextField(toText,{toText=it},label={Text("শেষের তারিখ YYYY-MM-DD")},modifier=Modifier.fillMaxWidth(),singleLine=true)
        if(locationMode) {
            Text("লোকেশন নির্বাচন করুন",color=Color.Cyan)
            Row(horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                TextButton(onClick={selectedLocation=""}) { Text("সব লোকেশন") }
                places.take(4).forEach { p -> TextButton(onClick={selectedLocation=p}) { Text(p) } }
            }
            if(selectedLocation.isNotBlank()) Text("নির্বাচিত: $selectedLocation",color=Color.Cyan)
        }
        Button(onClick={pdfLauncher.launch("MPR-DWD-report.pdf")}, modifier=Modifier.fillMaxWidth()) { Text("PDF রিপোর্ট তৈরি করুন") }
        Card(Modifier.fillMaxWidth(),colors=CardDefaults.cardColors(containerColor=Color(0xFF124A68))) {
            Column(Modifier.padding(14.dp)) {
                Text("কাজের হিসাবের মোট",color=Color.White)
                Text("৳${"%.2f".format(total)}",color=Color.Cyan,fontSize=25.sp)
                Text("মোট রেকর্ড: ${filtered.size}  •  ব্যক্তিগত লেনদেন এখানে অন্তর্ভুক্ত নয়",color=Color.White)
            }
        }
        LazyColumn(Modifier.weight(1f)) {
            items(filtered,key={it.id}) { r ->
                Card(Modifier.fillMaxWidth().padding(vertical=4.dp)) {
                    Column(Modifier.padding(10.dp)) {
                        Text("${LocalDate.ofInstant(Instant.ofEpochMilli(r.recordDate),ZoneId.systemDefault())} • ${r.location}",color=Color.Cyan)
                        Text("${r.recordType}: ${r.title}",color=Color.White)
                        Text("৳${r.amount+r.outboundFare+r.returnFare+r.mealDay+r.mealNight}",color=Color.White)
                        if(r.description.isNotBlank()) Text(r.description,color=Color.LightGray)
                    }
                }
            }
        }
    }
}
