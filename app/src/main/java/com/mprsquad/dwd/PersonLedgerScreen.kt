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
import com.mprsquad.dwd.data.PersonEntity
import com.mprsquad.dwd.data.PersonTransactionEntity
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

@Composable
internal fun PersonLedgerScreen(onBack: () -> Unit) {
 val ctx = LocalContext.current
 val dao = remember { DwdDatabase.get(ctx).dwdDao() }
 val scope = rememberCoroutineScope()
 val people by dao.observePeople().collectAsState(initial = emptyList())
 var selected by remember { mutableStateOf<Long?>(null) }
 val transactions by (selected?.let { dao.observePersonTransactions(it) } ?: remember { kotlinx.coroutines.flow.flowOf(emptyList()) }).collectAsState(initial = emptyList())
 var personName by remember { mutableStateOf("") }
 var code by remember { mutableStateOf("") }
 var amount by remember { mutableStateOf("") }
 var description by remember { mutableStateOf("") }
 var comments by remember { mutableStateOf("") }
 var received by remember { mutableStateOf(false) }
 var error by remember { mutableStateOf("") }
 Column(Modifier.fillMaxSize().padding(14.dp)) {
  Row(verticalAlignment = Alignment.CenterVertically) {
   IconButton(onClick = { if (selected != null) selected = null else onBack() }) { Icon(Icons.Default.ArrowBack, "ফিরে যান") }
   Text(if (selected == null) "ব্যক্তিভিত্তিক হিসাব" else "লেনদেনের হিসাব", color = Color.White, fontSize = 21.sp)
  }
  if (selected == null) {
   OutlinedTextField(personName, { personName = it }, label = { Text("ব্যক্তির নাম") }, modifier = Modifier.fillMaxWidth())
   OutlinedTextField(code, { code = it }, label = { Text("আইডি / পরিচিতি") }, modifier = Modifier.fillMaxWidth())
   Button(onClick = {
    if (personName.isBlank()) error = "নাম লিখুন" else scope.launch { dao.savePerson(PersonEntity(displayName = personName.trim(), personCode = code.trim())); personName = ""; code = ""; error = "" }
   }) { Text("ব্যক্তি যোগ করুন") }
   if (error.isNotBlank()) Text(error, color = Color.Red)
   LazyColumn(Modifier.weight(1f)) {
    items(people, key = { it.id }) { p ->
     Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
      Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
       Column(Modifier.weight(1f)) { Text(p.displayName, color = Color.White); Text(p.personCode, color = Color.LightGray) }
       TextButton(onClick = { selected = p.id }) { Text("হিসাব") }
       TextButton(onClick = { scope.launch { dao.deletePerson(p.id) } }) { Text("মুছুন") }
      }
     }
    }
   }
  } else {
   val person = people.firstOrNull { it.id == selected }
   Text(person?.displayName ?: "ব্যক্তি", color = Color.Cyan, fontSize = 19.sp)
   OutlinedTextField(amount, { amount = it }, label = { Text("টাকার পরিমাণ") }, modifier = Modifier.fillMaxWidth())
   OutlinedTextField(description, { description = it }, label = { Text("লেনদেনের বিবরণ") }, modifier = Modifier.fillMaxWidth())
   OutlinedTextField(comments, { comments = it }, label = { Text("মন্তব্য") }, modifier = Modifier.fillMaxWidth())
   Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(received, { received = it }); Text("টাকা গ্রহণ করা হয়েছে", color = Color.White) }
   Button(onClick = {
    val value = amount.toDoubleOrNull()
    if (value == null || value < 0) error = "সঠিক টাকার পরিমাণ দিন" else scope.launch {
     dao.savePersonTransaction(PersonTransactionEntity(personId = selected!!, transactionDate = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(), amount = value, description = description, received = received, comments = comments))
     amount = ""; description = ""; comments = ""; received = false; error = ""
    }
   }) { Text("লেনদেন সংরক্ষণ") }
   if (error.isNotBlank()) Text(error, color = Color.Red)
   Text("ব্যক্তিগত হিসাব — ড্যাশবোর্ডের কাজের মোটে অন্তর্ভুক্ত নয়", color = Color.LightGray)
   LazyColumn(Modifier.weight(1f)) {
    items(transactions, key = { it.id }) { t ->
     Card(Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
      Column(Modifier.padding(10.dp)) {
       Text("৳${t.amount} • ${if (t.received) "গ্রহণ করা হয়েছে" else "পাওয়া বাকি"}", color = Color.White)
       Text(t.description, color = Color.LightGray)
       if (t.comments.isNotBlank()) Text(t.comments, color = Color.Cyan)
       TextButton(onClick = { scope.launch { dao.deletePersonTransaction(t.id) } }) { Text("মুছুন") }
      }
     }
    }
   }
  }
 }
}
