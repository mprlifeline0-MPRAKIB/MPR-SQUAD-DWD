package com.mprsquad.dwd

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun AppShareScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    Column(Modifier.fillMaxSize().padding(20.dp), horizontalAlignment=Alignment.CenterHorizontally) {
        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
            IconButton(onClick=onBack) { Icon(Icons.Default.ArrowBack,"ফিরে যান",tint=Color.Cyan) }
            Text("অ্যাপ শেয়ার",color=Color.White,fontSize=21.sp)
        }
        Spacer(Modifier.height(20.dp))
        Text("অ্যাপের লিংক শেয়ার করুন",color=Color.Cyan,fontSize=18.sp)
        Text("শুধু অ্যাপের লিংক শেয়ার হবে। এই ফোনের Room ডেটাবেস বা ব্যক্তিগত তথ্য শেয়ার হবে না। অন্য ফোনে ইনস্টল করলে আলাদা খালি ডেটাবেস তৈরি হবে।",color=Color.White)
        Spacer(Modifier.height(20.dp))
        Button(onClick={
            val send=Intent(Intent.ACTION_SEND).apply {
                type="text/plain"
                putExtra(Intent.EXTRA_SUBJECT,"MPR SQUAD DWD — Daily Work Details")
                putExtra(Intent.EXTRA_TEXT,"MPR SQUAD DWD app project and APK builds: https://github.com/mprlifeline0-MPRAKIB/MPR-SQUAD-DWD")
            }
            context.startActivity(Intent.createChooser(send,"MPR SQUAD DWD শেয়ার করুন"))
        }) { Text("শেয়ার করুন") }
    }
}
