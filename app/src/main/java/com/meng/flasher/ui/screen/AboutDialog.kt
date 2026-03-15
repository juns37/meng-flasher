package com.meng.flasher.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meng.flasher.R

@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current

    fun openUrl(url: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                // Icon
                Image(
                    painter = painterResource(R.drawable.icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                )
                Spacer(Modifier.height(8.dp))

                // Nama & versi
                Text(
                    stringResource(R.string.app_name),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    stringResource(R.string.home_version),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(Modifier.height(16.dp))
                Divider(color = MaterialTheme.colorScheme.outline)
                Spacer(Modifier.height(12.dp))

                // Customized by
                Text(
                    text = "Customized by",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Juni",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "github.com/juns37",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { openUrl("https://github.com/juns37") }
                )

                Spacer(Modifier.height(12.dp))
                Divider(color = MaterialTheme.colorScheme.outline)
                Spacer(Modifier.height(12.dp))

                // Author section
                InfoRow(label = "Author", value = "LibXZR")
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "github.com/libxzr/HorizonKernelFlasher",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { openUrl("https://github.com/libxzr/HorizonKernelFlasher") }
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "GNU General Public License v3.0",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontStyle = FontStyle.Italic
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.about_close))
            }
        },
        dismissButton = {
            TextButton(onClick = { openUrl("https://github.com/juns37/meng-flasher") }) {
                Text("Source")
            }
        }
    )
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "$label: ",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = value,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
