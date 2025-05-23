package com.example.gameon.composables

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.gameon.AdminActivity
import com.example.gameon.R
import com.example.gameon.UserSettingsActivity
import com.example.gameon.api.methods.SessionDetails
import com.example.gameon.api.methods.logout
import com.example.gameon.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun Header(context: Context, scope: LifecycleCoroutineScope) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .background(color = BlueDark)
            .height(160.dp)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Logo()
        Spacer(modifier = Modifier.weight(1f))
        HeaderDropdown(context, scope)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderDropdown(context: Context, scope: LifecycleCoroutineScope) {
    val sessionDetails = SessionDetails(context)
    val user = sessionDetails.getUser()
    val adminId = sessionDetails.getAdminId()

    val expanded = remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded.value,
            onExpandedChange = { expanded.value = !expanded.value }
        ) {
            Avatar(
                discordId = user?.discord_id ?: "0",
                avatarId = user?.avatar,
                size = 90.dp,
                modifier = Modifier
                    .border(width = 2.dp, color = PurpleLight, shape = CircleShape)
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
            )
            ExposedDropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false },
                containerColor = Purple,
                modifier = Modifier.width(120.dp)
            ) {
                if (adminId != -1) HeaderDropdownItem(expanded, "Admin Panel")
                { context.startActivity(Intent(context, AdminActivity::class.java)) }
                HeaderDropdownItem(expanded, "Settings")
                { context.startActivity(Intent(context, UserSettingsActivity::class.java)) }
                HeaderDropdownItem(expanded, "Log Out")
                { scope.launch { logout(context) } }
            }
        }

        Text(
            text = user?.username ?: "Unknown",
            color = Purple,
            style = TextStyle(
                fontFamily = FontFamily(Font(R.font.barlowcondensed_bold)),
                fontSize = 16.sp,
                shadow = Shadow(
                    color = PurpleLight,
                    blurRadius = 10f
                )
            )
        )
    }
}

@Composable
fun HeaderDropdownItem(
    expanded: MutableState<Boolean>,
    text: String,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        { Text(
            text,
            fontFamily = FontFamily(Font(R.font.lato_black)),
            textAlign = TextAlign.Center,
            color = BlueDarker,
            modifier = Modifier.fillMaxWidth()
        ) },
        onClick = {
            expanded.value = false
            onClick()
        },
    )
}