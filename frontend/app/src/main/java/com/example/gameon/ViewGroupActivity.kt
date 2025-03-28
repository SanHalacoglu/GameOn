package com.example.gameon

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.foundation.layout.size
import com.example.gameon.classes.Group
import com.example.gameon.ui.theme.*
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.gameon.api.methods.fetchGroupUrl
import com.example.gameon.api.methods.getGroupMembers
import com.example.gameon.classes.User
import com.example.gameon.composables.Header
import com.example.gameon.composables.ReportButton
import kotlinx.coroutines.launch
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import com.example.gameon.api.methods.updateGroup
import com.example.gameon.composables.TextInput

class ViewGroupActivity : ComponentActivity() {
    val groupMembersState = mutableStateOf<List<User>>(emptyList())
    val showDialog = mutableStateOf(false)
    val dialogMessage = mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val group: Group? = intent.getParcelableExtra("selected_group")
        Log.d("ViewGroupActivity", "Group: $group")
        val groupId = group?.group_id ?: 0
        val groupMembersState = mutableStateOf<List<User>>(emptyList())

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
                Header(this@ViewGroupActivity, lifecycleScope)
                Column(modifier = Modifier.weight(1f)) {
                    MainContent(groupMembersState, group)
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
                        .padding(bottom = 310.dp)
                ) {
                    setResult(RESULT_OK)
                    finish()
                }
            }
        }
    }
}

@Composable
fun GroupMembers(groupMembers: MutableState<List<User>>) {
    val title = FontFamily(Font(R.font.barlowcondensed_bold))
    val regularText = FontFamily(Font(R.font.lato_regular))

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
                fontFamily = title,
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
                        fontFamily = regularText,
                        fontSize = 16.sp
                    )

                    errorMessage != null -> Text(
                        text = errorMessage,
                        color = Purple,
                        fontFamily = regularText,
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
                                    .testTag(username),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = username,
                                    color = White,
                                    fontFamily = regularText,
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainContent(groupMembers: MutableState<List<User>>, group: Group?) {
    val groupId = group?.group_id ?: 0
    val groupName = group?.group_name ?: "Unknown Group"

    val openModal = remember { mutableStateOf(false) }
    val groupDisplayName = remember { mutableStateOf(groupName)}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = groupDisplayName.value,
                color = Blue,
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.barlowcondensed_bold)),
                    fontSize = 30.sp,
                ),
                modifier = Modifier.testTag(groupName)
            )

            Spacer(modifier = Modifier.width(8.dp))

            androidx.compose.material3.Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Group Name",
                tint = Blue,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { openModal.value = true }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        GroupMembers(groupMembers)

        Spacer(modifier = Modifier.height(20.dp))

        GoToDiscord(groupId, LocalContext.current)
    }
    if (openModal.value) GroupRenameModal(group, groupDisplayName, openModal)
}

@Composable
fun GroupRenameModal(
    group: Group?,
    groupDisplayName: MutableState<String>,
    openModal: MutableState<Boolean>,
) {
    val fontFamily = FontFamily(Font(R.font.lato_bold))
    val groupRename = remember { mutableStateOf(groupDisplayName.value) }

    val onDismissRequest = { openModal.value = false; groupRename.value = groupDisplayName.value }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Dialog(onDismissRequest) {
        Card(
            modifier = Modifier.fillMaxWidth().height(250.dp).padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor=BlueDark, contentColor=White)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Rename group?",
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                )
                TextInput(
                    "Name", groupRename, Modifier.height(60.dp).padding(horizontal = 16.dp), 14.sp,
                    singleLine = true, containerColor = Color(0xccffffff), textColor = BlueDarker
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ReportButton(
                        "Cancel", outlined=true, containerColor=BlueDark,
                        modifier = Modifier.width(100.dp),
                        onClick = onDismissRequest
                    )
                    ReportButton(
                        "Confirm", textColor = BlueDarker, containerColor = Blue,
                        modifier = Modifier.width(100.dp)
                    ) {
                        coroutineScope.launch {
                            val success = group?.let { updateGroup(context, it.group_id!!, Group(
                                group_id = it.group_id, group_name = groupRename.value,
                                max_players = it.max_players, game = it.game, game_id = it.game_id
                            )) } ?: false
                            if (success) {
                                groupDisplayName.value = groupRename.value
                                openModal.value = false
                            }
                            else Toast.makeText(context, "Error renaming group!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}