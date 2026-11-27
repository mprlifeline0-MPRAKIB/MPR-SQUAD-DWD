package com.mprsquad.dwd

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
import com.mprsquad.dwd.data.WorkRecordEntity
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.Instant

@Composable
internal fun WorkRecordScreen(title: String, type: String, onBack: () -> Unit) {
 val ctx = LocalContext.current
 val dao = remember { DwdDatabase.get(ctx).dwdDao() }
 val scope = rememberCoroutineScope()
 val rows by dao.observeWorkRecords(type).collectAsState(initial = emptyList())
 var date by remember { mutableStateOf(LocalDate.now().toString()) }
 var place by remember { mutableStateOf("") }
 var name by remember { mutableStateOf("") }
 var amount by remember { mutableStateOf("0") }
 var details by remember { mutableStateOf("") }
 var editing by remember { mutableStateOf<Long?>(null) }
 var error by remember { mutableStateOf("") }
 Column(Modifier.fillMaxSize().padding(16.dp)) {
  Row(verticalAlignment = Alignment.CenterVertically) {
   IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
   Text(title, color = Color.White, fontSize = 21.sp)
  }
  Text("মোট এন্ট্রি: ${rows.size}", color = Color.Cyan)
  OutlinedTextField(date, { date = it }, label = { Text("তারিখ YYYY-MM-DD") }, modifier = Modifier.fillMaxWidth())
  OutlinedTextField(place, { place = it }, label = { Text("কাজের স্থান") }, modifier = Modifier.fillMaxWidth())
  OutlinedTextField(name, { name = it }, label = { Text("খাত / শিরোনাম") }, modifier = Modifier.fillMaxWidth())
  OutlinedTextField(amount, { amount = it }, label = { Text("টাকার পরিমাণ") }, modifier = Modifier.fillMaxWidth())
  OutlinedTextField(details, { details = it }, label = { Text("বিস্তারিত") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
  if (error.isNotBlank()) Text(error, color = Color.Red)
  Row {
   Button(onClick = {
    try {
     val millis = LocalDate.parse(date).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
     val money = amount.toDouble()
     require(place.isNotBlank() && name.isNotBlank() && money >= 0)
     val row = WorkRecordEntity(id = editing ?: 0L, recordDate = millis, location = place, recordType = type, title = name, description = details, amount = money)
     scope.launch { if (editing == null) dao.saveWorkRecord(row) else dao.updateWorkRecord(row); editing = null; place = ""; name = ""; amount = "0"; details = ""; error = "" }
    } catch (e: Exception) { error = "তারিখ, স্থান, শিরোনাম ও টাকার পরিমাণ যাচাই করুন।" }
   }) { Text(if (editing == null) "সংরক্ষণ" else "আপডেট") }
   OutlinedButton(onClick = { editing = null; place = ""; name = ""; amount = "0"; details = ""; error = "" }) { Text("নতুন") }
  }
  HorizontalDivider(Modifier.padding(vertical = 8.dp))
  LazyColumn(Modifier.weight(1f)) {
   items(rows, key = { it.id }) { row ->
    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
     Column(Modifier.padding(10.dp)) {
      Text("${LocalDate.ofInstant(Instant.ofEpochMilli(row.recordDate), ZoneId.systemDefault())} • ${row.location}")
      Text("${row.title} — ৳${row.amount}")
      Text(row.description)
      Row {
       TextButton(onClick = { editing = row.id; date = LocalDate.ofInstant(Instant.ofEpochMilli(row.recordDate), ZoneId.systemDefault()).toString(); place = row.location; name = row.title; amount = row.amount.toString(); details = row.description }) { Text("সম্পাদনা") }
       TextButton(onClick = { scope.launch { dao.deleteWorkRecord(row.id) } }) { Text("মুছুন") }
      }
     }
    }
   }
  }
 }
}