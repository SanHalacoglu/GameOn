package com.example.gameon

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.gameon.api.methods.SessionDetails
import com.example.gameon.api.methods.getPreferencesByUserId
import com.example.gameon.api.methods.getUserGroups
import com.example.gameon.classes.Group
import com.example.gameon.composables.Header
import com.example.gameon.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.testTag

class MainActivity : ComponentActivity() {
    private val groupListState = mutableStateOf<List<Group>>(emptyList())
    fun getGroupListState(): MutableState<List<Group>> = groupListState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel: MainViewModel by viewModels()

        val sessionDetails = SessionDetails(this)
        val user = sessionDetails.getUser()
        val adminId = sessionDetails.getAdminId()
        Log.d("UserSettings", "User: $user, Admin ID: $adminId")
        val preferenceIDState = mutableStateOf(-1)
        lateinit var viewGroupLauncher: ActivityResultLauncher<Intent>
        lateinit var preferencesLauncher: ActivityResultLauncher<Intent>

        val discordUsername = user?.username ?: "Unknown"
        val discordId = user?.discord_id ?: "Unknown"

        viewGroupLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                lifecycleScope.launch {
                    val updatedGroups = getUserGroups(discordId, this@MainActivity)
                    groupListState.value = updatedGroups
                }
            }
        }

        preferencesLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                Log.d("MainActivity", "result from matchmaking preferences activity: $result")
                lifecycleScope.launch {
                    val updatedPreferences = getPreferencesByUserId(this@MainActivity, discordId)
                    if (updatedPreferences != null) {
                        val updatedPrefId = updatedPreferences.preference_id ?: -1
                        preferenceIDState.value = updatedPrefId
                        viewModel.startMatchmaking(this@MainActivity, updatedPrefId, discordId)
                    }
                }
            }
        }

        lifecycleScope.launch {
            val initialGroupList = getUserGroups(discordId, this@MainActivity)
            groupListState.value = initialGroupList
            while (true) {
                delay(10_000)
                val updatedGroupList = getUserGroups(discordId, this@MainActivity)
                groupListState.value = updatedGroupList
            }
        }

        setContent {
            Column(
                modifier = Modifier.fillMaxSize().background(color = BlueDarker),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Header(this@MainActivity, lifecycleScope)
                MainContent(
                    groupListState,
                    discordUsername,
                    adminId,
                    viewModel.isMatchmakingActive,
                    viewModel.showDialog,
                    viewModel.dialogMessage,
                    viewGroupLauncher,
                    preferencesLauncher
                )
            }
        }
    }
}

@Composable
fun FindGroup(
    context: Context,
    isMatchmakingActive: MutableState<Boolean>,
    showDialog: MutableState<Boolean>,
    dialogMessage: MutableState<String>,
    preferencesLauncher: ActivityResultLauncher<Intent>
) {
    val fontFamily = FontFamily(Font(R.font.barlowcondensed_bold))
    val coroutineScope = rememberCoroutineScope()

    fun openPreferences() {
        val intent = Intent(context, SetMatchmakingPreferences::class.java)
        preferencesLauncher.launch(intent)
    }

    LaunchedEffect(showDialog.value) {
        Log.d("FindGroup", "Dialog state changed: ${showDialog.value}")
    }

    val buttonColor = if (isMatchmakingActive.value) Purple.copy(alpha = 0.5f) else Purple
    val buttonText = if (isMatchmakingActive.value) "Finding..." else "Find Group"

    Log.d("FindGroup", "Recomposition triggered: Button Text = $buttonText")

    Box(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(70.dp)
            .clip(RoundedCornerShape(50.dp))
            .background(buttonColor)
            .clickable(enabled = !isMatchmakingActive.value) { openPreferences() }
            .testTag("FindGroupButton"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = buttonText,
            color = if (isMatchmakingActive.value) BlueDarker.copy(alpha = 0.5f) else BlueDarker,
            style = TextStyle(fontFamily = fontFamily, fontSize = 25.sp)
        )
    }

    if (showDialog.value) {
        Log.d("FindGroup", "Showing dialog: ${dialogMessage.value}")
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Matchmaking Status") },
            text = { Text(dialogMessage.value) },
            confirmButton = {
                TextButton(
                    onClick = { showDialog.value = false }
                ) {
                    Text("OK")
                }
            },
            modifier = Modifier.testTag("MatchmakingPopup")
        )
    }
}

@Composable
fun ViewExistingGroups(context: Context, groupListState: MutableState<List<Group>>, discordUsername: String, viewGroupLauncher: ActivityResultLauncher<Intent>) {
    val title = FontFamily(Font(R.font.barlowcondensed_bold))
    val regularText = FontFamily(Font(R.font.lato_regular))
    val groups = groupListState.value

    val showErrorDialog = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf("") }

    val isLoading = groups == null
    val hasGroups = groups.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(200.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(2.dp, Purple, RoundedCornerShape(20.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "My Groups",
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

                hasGroups -> LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(groups) { group ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clip(RoundedCornerShape(50.dp))
                                .background(Purple.copy(alpha = 0.2f))
                                .clickable {
                                    val intent = Intent(context, ViewGroupActivity::class.java).apply {
                                        putExtra("selected_group", group)
                                        putExtra("discord_username", discordUsername)
                                    }
                                    viewGroupLauncher.launch(intent)
                                }
                                .testTag("${group.group_name}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = group.group_name,
                                color = White,
                                fontFamily = regularText,
                                fontSize = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                else -> Text(
                    text = "No groups found",
                    color = Purple,
                    fontFamily = regularText,
                    fontSize = 16.sp
                )
            }
        }
    }
    if (showErrorDialog.value) {
        AlertDialog(
            onDismissRequest = { showErrorDialog.value = false },
            title = { Text("Group Not Found") },
            text = { Text(errorMessage.value) },
            confirmButton = {
                TextButton(
                    onClick = { showErrorDialog.value = false }
                ) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun ReportsSection(context: Context, adminId: Int) {
    val title = FontFamily(Font(R.font.barlowcondensed_bold))
    val regularText = FontFamily(Font(R.font.lato_regular))

    Column(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .clip(RoundedCornerShape(20.dp))
            .border(2.dp, Purple, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Reports",
            color = White,
            fontFamily = title,
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Box(
            modifier = Modifier.fillMaxWidth().height(50.dp)
                .clip(RoundedCornerShape(50.dp)).background(Purple)
                .clickable {
                    context.startActivity(Intent(
                        context,
                        ReportsActivity::class.java
                    ))
                }.testTag("ReportButton"),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Submit a Report",
                color = BlueDarker,
                fontFamily = regularText,
                fontSize = 18.sp
            )
        }
        if (adminId != -1) {
            Box(
                modifier = Modifier.fillMaxWidth().height(50.dp)
                    .clip(RoundedCornerShape(50.dp)).background(Purple)
                    .clickable {
                        context.startActivity(
                            Intent(
                                context,
                                ListReportsActivity::class.java
                            )
                        )
                    }.testTag("ViewReportsButton"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "View Reports",
                    color = BlueDarker,
                    fontFamily = title,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun MainContent(
    groupListState: MutableState<List<Group>>,
    discordUsername : String,
    adminId: Int,
    isMatchmakingActive: MutableState<Boolean>,
    showDialog: MutableState<Boolean>,
    dialogMessage: MutableState<String>,
    viewGroupLauncher: ActivityResultLauncher<Intent>,
    preferencesLauncher: ActivityResultLauncher<Intent>
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(2.dp))
        FindGroup(context, isMatchmakingActive, showDialog, dialogMessage, preferencesLauncher)
        ViewExistingGroups(context, groupListState, discordUsername, viewGroupLauncher)
        ReportsSection(context, adminId)
    }
}
