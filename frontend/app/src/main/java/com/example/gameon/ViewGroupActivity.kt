package com.example.gameon

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gameon.classes.Group
import com.example.gameon.ui.theme.*
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.gameon.api.methods.SessionDetails
import com.example.gameon.api.methods.fetchGroupUrl
import com.example.gameon.api.methods.getGroupMembers
import com.example.gameon.api.methods.initiateMatchmaking
import com.example.gameon.api.methods.logout
import com.example.gameon.classes.DateAdapter
import com.example.gameon.classes.User
import com.example.gameon.composables.ReportButton
import com.example.gameon.composables.ReportTitle
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.launch
import java.util.Date
import androidx.compose.material3.AlertDialog

class ViewGroupActivity : ComponentActivity() {
    val groupMembersState = mutableStateOf<List<User>>(emptyList())
    fun getGroupMemberState() = groupMembersState
    val showDialog = mutableStateOf(false)
    val dialogMessage = mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val group: Group? = intent.getParcelableExtra("selected_group")
        Log.d("ViewGroupActivity", "Group: $group")
        val groupId = group?.group_id ?: 0
        val groupName = group?.group_name ?: "Unknown Group"
        val user = SessionDetails(this).getUser()
        val discordUsername = user?.username ?: "Unknown"

        Log.d("ViewGroupActivity", "Group: $group")

        lifecycleScope.launch {
            val groupMemberList = getGroupMembers(groupId, this@ViewGroupActivity)
            val userList = groupMemberList.mapNotNull { it.user }

            if (userList.isEmpty()) {
                dialogMessage.value = "Failed to load group members. Please try again later."
                showDialog.value = true
            }
            groupMembersState.value = userList
        }

        Log.d("ViewGroupActivity", "Group Members: ${groupMembersState.value}")

        setContent {
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = BlueDarker),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                ViewGroupHeader(
                    discordUsername,
                    {
                        val intent = Intent(
                            this@ViewGroupActivity,
                            UserSettingsActivity::class.java
                        )
                        startActivity(intent)
                    },
                    {
                        lifecycleScope.launch {
                            logout(this@ViewGroupActivity)
                        }
                    }
                )
                Column(modifier = Modifier.weight(1f)) {
                    MainContent(groupMembersState, groupName, groupId)
                }

                if (showDialog.value) {
                    AlertDialog(
                        onDismissRequest = { showDialog.value = false },
                        title = { Text("Error") },
                        text = { Text(dialogMessage.value) },
                        confirmButton = {
                            TextButton(
                                onClick = { showDialog.value = false }
                            ) {
                                Text("OK")
                            }
                        },
                        modifier = Modifier.testTag("GroupMembersErrorPopup")
                    )
                }

                ReportButton(
                    "Back",
                    outlined = true,
                    modifier = Modifier
                        .width(300.dp)
                        .padding(bottom = 80.dp)
                ) {
                    finish()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewGroupHeader(username: String, onSettings: () -> Unit, onLogout: () -> Unit) {
    val fontFamilyBarlow = FontFamily(Font(R.font.barlowcondensed_bold))
    val fontFamilyLato = FontFamily(Font(R.font.lato_black))
    var expanded by remember { mutableStateOf(false) }

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .background(color = BlueDark)
            .height(160.dp)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box {
            Text(
                text = "GameOn",
                color = TestBlue,
                style = TextStyle(
                    fontFamily = fontFamilyBarlow,
                    fontSize = 55.sp,
                    shadow = Shadow(
                        color = TestBlueLight,
                        blurRadius = 20F
                    ),
                )
            )
            Image(
                painterResource(R.drawable.gameon_headphones),
                "GameOn Headphones",
                modifier = Modifier
                    .size(35.dp)
                    .offset(x = 107.dp, y = 4.dp),
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile Icon",
                    tint = Purple,
                    modifier = Modifier.size(90.dp)
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    containerColor = Purple,
                    modifier = Modifier
                        .width(120.dp)
                ) {
                    DropdownMenuItem(
                        { Text(
                            "Settings",
                            fontFamily = fontFamilyLato,
                            textAlign = TextAlign.Center,
                            color = BlueDarker,
                            modifier = Modifier.fillMaxWidth()
                        ) },
                        onClick = onSettings,
                    )
                    DropdownMenuItem(
                        { Text(
                            "Log Out",
                            fontFamily = fontFamilyLato,
                            textAlign = TextAlign.Center,
                            color = BlueDarker,
                            modifier = Modifier.fillMaxWidth()
                        ) },
                        onClick = onLogout,
                    )
                }
            }

            Text(
                text = username,
                color = Purple,
                style = TextStyle(
                    fontFamily = fontFamilyBarlow,
                    fontSize = 16.sp,
                    shadow = Shadow(
                        color = PurpleLight,
                        blurRadius = 10f
                    )
                )
            )
        }
    }
}

@Composable
fun GroupMembers(groupMembers: MutableState<List<User>>) {
    val fontFamily = FontFamily(Font(R.font.barlowcondensed_bold))

    val isLoading = groupMembers.value.isEmpty()
    val errorMessage = if (isLoading) "No members found" else null

    Box(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(200.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(2.dp, Purple, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Group Members",
                color = White,
                fontFamily = fontFamily,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isLoading -> Text(
                        text = "Loading...",
                        color = Purple,
                        fontFamily = fontFamily,
                        fontSize = 16.sp
                    )

                    errorMessage != null -> Text(
                        text = errorMessage,
                        color = Purple,
                        fontFamily = fontFamily,
                        fontSize = 16.sp
                    )

                    else -> LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(groupMembers.value) { member ->
                            val username = member.username

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .clip(RoundedCornerShape(15.dp))
                                    .background(Purple.copy(alpha = 0.2f))
                                    .testTag("$username"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = username,
                                    color = White,
                                    fontFamily = fontFamily,
                                    fontSize = 16.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GoToDiscord(groupId: Int, context: Context) {
    val fontFamily = FontFamily(Font(R.font.barlowcondensed_bold))
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(70.dp)
            .clip(RoundedCornerShape(50.dp))
            .background(Purple)
            .clickable {
                coroutineScope.launch {
                    isLoading = true
                    val groupUrl = fetchGroupUrl(groupId, context)
                    isLoading = false

                    if (groupUrl != null) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(groupUrl))
                        context.startActivity(intent)
                    }
                }
            }
            .testTag("DiscordButton"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isLoading) "Loading..." else "Go to Discord Group",
            color = BlueDarker,
            style = TextStyle(
                fontFamily = fontFamily,
                fontSize = 25.sp
            )
        )
    }
}

@Composable
fun MainContent(groupMembers: MutableState<List<User>>, groupName: String, groupId: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = groupName,
            color = Blue,
            style = TextStyle(
                fontFamily = FontFamily(Font(R.font.barlowcondensed_bold)),
                fontSize = 30.sp,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("$groupName"),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        GroupMembers(groupMembers)

        Spacer(modifier = Modifier.height(20.dp))

        GoToDiscord(groupId, LocalContext.current)
    }
}