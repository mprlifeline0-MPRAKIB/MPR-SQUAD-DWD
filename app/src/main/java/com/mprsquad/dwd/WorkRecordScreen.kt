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
    val isBill = type == "BILL"
    var date by remember { mutableStateOf(LocalDate.now().toString()) }
    var place by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("0") }
    var details by remember { mutableStateOf("") }
    var outboundMode by remember { mutableStateOf("") }
    var outboundFare by remember { mutableStateOf("0") }
    var returnMode by remember { mutableStateOf("") }
    var returnFare by remember { mutableStateOf("0") }
    var mealDay by remember { mutableStateOf("0") }
    var mealNight by remember { mutableStateOf("0") }
    var peopleCount by remember { mutableStateOf("0") }
    var peopleNames by remember { mutableStateOf("") }
    var payer by remember { mutableStateOf("") }
    var paid by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<Long?>(null) }
    var error by remember { mutableStateOf("") }

    fun clearForm() {
        date = LocalDate.now().toString(); place = ""; name = ""; amount = "0"; details = ""
        outboundMode = ""; outboundFare = "0"; returnMode = ""; returnFare = "0"
        mealDay = "0"; mealNight = "0"; peopleCount = "0"; peopleNames = ""; payer = ""
        paid = false; editing = null; error = ""
    }
    fun money(value: String): Double = value.toDoubleOrNull()?.also { require(it >= 0) } ?: throw IllegalArgumentException("Invalid amount")

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "ফিরে যান") }
            Text(title, color = Color.White, fontSize = 21.sp)
        }
        Text("মোট এন্ট্রি: ${rows.size}", color = Color.Cyan)
        LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(bottom = 12.dp)) {
            item {
                OutlinedTextField(date, { date = it }, label = { Text("তারিখ YYYY-MM-DD") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(place, { place = it }, label = { Text("কাজের স্থান") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(name, { name = it }, label = { Text(if (isBill) "বিলের শিরোনাম / কাজ" else "খরচের খাত") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(amount, { amount = it }, label = { Text(if (isBill) "অন্যান্য / মূল বিলের টাকা" else "টাকার পরিমাণ") }, modifier = Modifier.fillMaxWidth())
                if (isBill) {
                    Text("যাতায়াত", color = Color.Cyan, fontSize = 17.sp)
                    OutlinedTextField(outboundMode, { outboundMode = it }, label = { Text("যাওয়ার মাধ্যম (বাস/ট্রেন/রিকশা ইত্যাদি)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(outboundFare, { outboundFare = it }, label = { Text("যাওয়ার ভাড়া") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(returnMode, { returnMode = it }, label = { Text("ফেরার মাধ্যম") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(returnFare, { returnFare = it }, label = { Text("ফেরার ভাড়া") }, modifier = Modifier.fillMaxWidth())
                    Text("খাবার ও লোকজন", color = Color.Cyan, fontSize = 17.sp)
                    OutlinedTextField(mealDay, { mealDay = it }, label = { Text("দিনের খাবার বিল") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(mealNight, { mealNight = it }, label = { Text("রাতের খাবার বিল") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(peopleCount, { peopleCount = it }, label = { Text("লোকসংখ্যা") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(peopleNames, { peopleNames = it }, label = { Text("লোকজনের নাম") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(payer, { payer = it }, label = { Text("কে টাকা দিয়েছেন") }, modifier = Modifier.fillMaxWidth())
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = paid, onCheckedChange = { paid = it })
                        Text("বিল পরিশোধ হয়েছে", color = Color.White)
                    }
                }
                OutlinedTextField(details, { details = it }, label = { Text("সম্পূর্ণ বিবরণ") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                if (error.isNotBlank()) Text(error, color = Color.Red)
                Row {
                    Button(onClick = {
                        try {
                            val millis = LocalDate.parse(date).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                            require(place.isNotBlank() && name.isNotBlank())
                            val row = WorkRecordEntity(
                                id = editing ?: 0L, recordDate = millis, location = place.trim(),
                                recordType = type, title = name.trim(), description = details,
                                amount = money(amount), outboundMode = outboundMode,
                                outboundFare = if (isBill) money(outboundFare) else 0.0,
                                returnMode = returnMode, returnFare = if (isBill) money(returnFare) else 0.0,
                                mealDay = if (isBill) money(mealDay) else 0.0,
                                mealNight = if (isBill) money(mealNight) else 0.0,
                                peopleCount = if (isBill) (peopleCount.toIntOrNull() ?: throw IllegalArgumentException("Invalid people count")) else 0,
                                peopleNames = peopleNames, payer = payer, paid = paid
                            )
                            scope.launch {
                                if (editing == null) dao.saveWorkRecord(row) else dao.updateWorkRecord(row)
                                clearForm()
                            }
                        } catch (e: Exception) { error = "তারিখ, স্থান, শিরোনাম ও সব টাকার ঘর সঠিকভাবে পূরণ করুন।" }
                    }) { Text(if (editing == null) "সংরক্ষণ" else "আপডেট") }
                    Spacer(Modifier.width(8.dp))
                    OutlinedButton(onClick = { clearForm() }) { Text("নতুন") }
                }
                HorizontalDivider(Modifier.padding(vertical = 10.dp))
            }
            items(rows, key = { it.id }) { row ->
                Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(Modifier.padding(10.dp)) {
                        Text("${LocalDate.ofInstant(Instant.ofEpochMilli(row.recordDate), ZoneId.systemDefault())} • ${row.location}")
                        Text("${row.title} — ৳${row.amount}", color = Color.White)
                        if (isBill) {
                            Text("যাওয়া: ${row.outboundMode} ৳${row.outboundFare} | ফেরা: ${row.returnMode} ৳${row.returnFare}")
                            Text("দিনের খাবার ৳${row.mealDay} • রাতের খাবার ৳${row.mealNight}")
                            Text("লোক: ${row.peopleCount} ${row.peopleNames} • দিয়েছেন: ${row.payer}")
                            Text(if (row.paid) "পরিশোধিত" else "বাকি", color = if (row.paid) Color.Green else Color.Yellow)
                        }
                        if (row.description.isNotBlank()) Text(row.description)
                        Row {
                            TextButton(onClick = {
                                editing = row.id
                                date = LocalDate.ofInstant(Instant.ofEpochMilli(row.recordDate), ZoneId.systemDefault()).toString()
                                place = row.location; name = row.title; amount = row.amount.toString(); details = row.description
                                outboundMode = row.outboundMode; outboundFare = row.outboundFare.toString()
                                returnMode = row.returnMode; returnFare = row.returnFare.toString()
                                mealDay = row.mealDay.toString(); mealNight = row.mealNight.toString()
                                peopleCount = row.peopleCount.toString(); peopleNames = row.peopleNames
                                payer = row.payer; paid = row.paid; error = ""
                            }) { Text("সম্পাদনা") }
                            TextButton(onClick = { scope.launch { dao.deleteWorkRecord(row.id) } }) { Text("মুছুন") }
                        }
                    }
                }
            }
        }
    }
}
