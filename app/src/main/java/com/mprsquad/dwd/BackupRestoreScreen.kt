package com.mprsquad.dwd

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mprsquad.dwd.data.DwdDatabase
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

@Composable
internal fun BackupRestoreScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var status by remember { mutableStateOf("ব্যাকআপে এই ফোনের সব হিসাব, ব্যক্তিগত লেনদেনসহ থাকবে। ফাইলটি নিরাপদে রাখুন।") }
    val export = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/octet-stream")) { uri ->
        if (uri != null) runCatching {
            val db = DwdDatabase.get(context)
            db.openHelper.writableDatabase.query("PRAGMA wal_checkpoint(FULL)").close()
            db.close()
            val source = context.getDatabasePath("mpr_squad_dwd.db")
            context.contentResolver.openOutputStream(uri)?.use { out -> FileInputStream(source).use { it.copyTo(out) } }
                ?: error("ফাইল তৈরি করা যায়নি")
            status = "ব্যাকআপ সফলভাবে তৈরি হয়েছে।"
        }.onFailure { status = "ব্যাকআপ ব্যর্থ: ${it.message}" }
    }
    val import = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) runCatching {
            val temp = File(context.cacheDir, "restore_candidate.db")
            context.contentResolver.openInputStream(uri)?.use { input -> FileOutputStream(temp).use { input.copyTo(it) } }
                ?: error("ফাইল পড়া যায়নি")
            val header = ByteArray(16)
            FileInputStream(temp).use { require(it.read(header) == 16 && String(header, Charsets.US_ASCII).startsWith("SQLite format 3")) { "সঠিক SQLite ব্যাকআপ নয়" } }
            DwdDatabase.closeForRestore()
            val target = context.getDatabasePath("mpr_squad_dwd.db")
            target.parentFile?.mkdirs()
            temp.copyTo(target, overwrite = true)
            File(context.getDatabasePath("mpr_squad_dwd.db-wal").path).delete()
            File(context.getDatabasePath("mpr_squad_dwd.db-shm").path).delete()
            status = "রিস্টোর করা হয়েছে। অ্যাপটি বন্ধ করে আবার খুলুন।"
        }.onFailure { status = "রিস্টোর ব্যর্থ: ${it.message}" }
    }
    Column(Modifier.fillMaxSize().padding(18.dp)) {
        Row {
            IconButton(onClick=onBack) { Icon(Icons.Default.ArrowBack,"ফিরে যান",tint=Color.Cyan) }
            Text("ব্যাকআপ ও রিস্টোর",color=Color.White,fontSize=21.sp)
        }
        Spacer(Modifier.height(16.dp))
        Text("লোকাল ব্যাকআপ",color=Color.Cyan)
        Text("ব্যাকআপ ফাইল আপনার নির্বাচিত জায়গায় সংরক্ষণ হবে। এতে ব্যক্তিগত হিসাবও থাকবে; কাউকে ফাইলটি দেবেন না।",color=Color.White)
        Spacer(Modifier.height(12.dp))
        Button(onClick={export.launch("MPR-SQUAD-DWD-backup.db")}) { Text("ব্যাকআপ তৈরি করুন") }
        HorizontalDivider(Modifier.padding(vertical=18.dp))
        Text("রিস্টোর",color=Color.Cyan)
        Text("রিস্টোর করলে বর্তমান ডেটাবেস প্রতিস্থাপিত হবে। আগে ব্যাকআপ নিন।",color=Color.White)
        Spacer(Modifier.height(10.dp))
        Button(onClick={import.launch(arrayOf("*/*"))}) { Text("ব্যাকআপ ফাইল নির্বাচন করুন") }
        Spacer(Modifier.height(18.dp))
        Text(status,color=Color.Yellow)
    }
}
