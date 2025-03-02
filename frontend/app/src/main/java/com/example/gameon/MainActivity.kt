package com.example.gameon

import android.content.Context
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.gameon.api.methods.SessionDetails
import com.example.gameon.api.methods.checkMatchmakingStatus
import com.example.gameon.api.methods.getUserGroups
import com.example.gameon.api.methods.initiateMatchmaking
import com.example.gameon.api.methods.logout
import com.example.gameon.classes.Group
import com.example.gameon.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private val isMatchmakingActive = mutableStateOf(false)
    private val matchmakingStatus = mutableStateOf<String?>(null)
    private val groupListState = mutableStateOf<List<Group>>(emptyList())
    private val showDialog = mutableStateOf(false)
    private val dialogMessage = mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

//        val groupListState = mutableStateOf<List<Group>>(emptyList())
        val user = SessionDetails(this).getUser()

        val discordUsername = user?.username ?: "Unknown"
        val discordId = user?.discord_id ?: "Unknown"
        val preferenceID = user?.preference_id ?: user?.preferences?.preference_id?.toIntOrNull() ?: -1

        lifecycleScope.launch {
            while (true) {
                delay(10_000)

                if (isMatchmakingActive.value) {
                    Log.d("MainActivity", "Polling started: isMatchmakingActive = TRUE")
                    val status = checkMatchmakingStatus(this@MainActivity, discordId)
                    Log.d("MainActivity", "Matchmaking Status: $status")

                    matchmakingStatus.value = status

                    when (status) {
                        "in_progress" -> Log.d("MainActivity", "Matchmaking is still in progress.")
                        "timed_out" -> {
                            isMatchmakingActive.value = false
                            Log.d("MainActivity", "Matchmaking timed out.")
                            dialogMessage.value = "Matchmaking timed out. Please try again."
                            showDialog.value = true
                        }
                        "group_found" -> {
                            isMatchmakingActive.value = false
                            Log.d("MainActivity", "Group found! Navigating to group page.")
                            dialogMessage.value = "You have been matched with a group!"
                            showDialog.value = true
                        }
                        "not_in_progress" -> {
                            isMatchmakingActive.value = false
                            Log.d("MainActivity", "Matchmaking no longer in progress.")
                        }
                        "error" -> Log.e("MainActivity", "Error checking matchmaking status.")
                    }
                } else {
                    Log.d("MainActivity", "Polling skipped: isMatchmakingActive = FALSE")
                }
            }
        }

        lifecycleScope.launch {
            val initialGroupList = getUserGroups(discordId, this@MainActivity)
            Log.d("MainActivity", "Initial Group List: $initialGroupList")

            groupListState.value = initialGroupList

            while (true) {
                delay(10_000) // Polling every 10 seconds
                val updatedGroupList = getUserGroups(discordId, this@MainActivity)
                Log.d("MainActivity", "Updated Group List: $updatedGroupList")

                groupListState.value = updatedGroupList
            }
        }

        setContent {
            Column(
                modifier = Modifier.fillMaxSize().background(color = BlueDarker),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Header(
                    discordUsername,
                    {
                        val intent = android.content.Intent(
                            this@MainActivity,
                            UserSettingsActivity::class.java
                        )
                        startActivity(intent)
                    },
                    { lifecycleScope.launch { logout(this@MainActivity) } }
                )
                MainContent(preferenceID, groupListState, discordUsername, discordId, isMatchmakingActive, matchmakingStatus, showDialog, dialogMessage)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Header(username: String, onSettings: () -> Unit, onLogout: () -> Unit) {
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
                    imageVector = Icons.Default.AccountCircle, // Default profile icon
                    contentDescription = "Profile Icon",
                    tint = Purple, // Adjust color as needed
                    modifier = Modifier.size(90.dp)
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)// Set icon size
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

            // Username with Glow Effect
            Text(
                text = username,
                color = Purple, // Pinkish-white glow
                style = TextStyle(
                    fontFamily = fontFamilyBarlow,
                    fontSize = 16.sp, // Adjust size as needed
                    shadow = Shadow(
                        color = PurpleLight, // Glow color
                        blurRadius = 10f // Strong blur for glow effect
                    )
                )
            )
        }
    }
}

@Composable
fun FindGroup(
    preferenceID: Int,
    context: Context,
    discordId: String,
    isMatchmakingActive: MutableState<Boolean>,
    matchmakingStatus: MutableState<String?>,
    showDialog: MutableState<Boolean>,
    dialogMessage: MutableState<String>
) {
    val fontFamily = FontFamily(Font(R.font.barlowcondensed_bold))
    val coroutineScope = rememberCoroutineScope()

    fun startMatchmaking() {
        coroutineScope.launch {
            isMatchmakingActive.value = true
            Log.d("FindGroup", "isMatchmakingActive set to TRUE")
            val success = initiateMatchmaking(context, preferenceID)

            if (success) {
                Log.d("FindGroup", "Matchmaking started.")
                matchmakingStatus.value = "in_progress"
            } else {
                Log.e("FindGroup", "Failed to initiate matchmaking")
                isMatchmakingActive.value = false
                Log.d("FindGroup", "isMatchmakingActive set to FALSE due to failure")
            }
        }
    }

    LaunchedEffect(showDialog.value) {
        Log.d("FindGroup", "Dialog state changed: ${showDialog.value}")
    }


    val buttonColor = if (isMatchmakingActive.value) Purple.copy(alpha = 0.5f) else Purple
    val buttonText = if (isMatchmakingActive.value) "Finding..." else "Find Group"

    Box(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(70.dp)
            .clip(RoundedCornerShape(50.dp))
            .background(buttonColor)
            .clickable( enabled = !isMatchmakingActive.value) { startMatchmaking() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = buttonText,
            color = if (isMatchmakingActive.value) BlueDarker.copy(alpha = 0.5f) else BlueDarker,
            style = TextStyle(fontFamily = fontFamily, fontSize = 25.sp)
        )
    }

    if (showDialog.value) {
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
            }
        )
    }
}

@Composable
fun ViewExistingGroups(context: Context, groupListState: MutableState<List<Group>>, discordUsername: String) {
    val fontFamily = FontFamily(Font(R.font.barlowcondensed_bold))
    val groups = groupListState.value

    val isLoading = groups == null // If `null`, still fetching
    val hasGroups = groups.isNotEmpty()

    Box(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(200.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(2.dp, Purple, RoundedCornerShape(20.dp))
            .padding(16.dp) // Add padding to separate text from the edges
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header at the top
            Text(
                text = "My Existing Groups",
                color = White,
                fontFamily = fontFamily,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(8.dp)) // Add spacing

            // ✅ Properly handle the states
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
                                        // Create Intent and pass Group object
                                        val intent = android.content.Intent(
                                            context,
                                            ViewGroupActivity::class.java
                                        )
                                        intent.putExtra("selected_group", group)
                                        intent.putExtra("discord_username", discordUsername)
                                        context.startActivity(intent)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = group.group_name,
                                    color = White,
                                    fontFamily = fontFamily,
                                    fontSize = 16.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    else -> Text(
                        text = "No groups found",
                        color = Purple,
                        fontFamily = fontFamily,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ReportsSection(context: Context) {
    val fontFamily = FontFamily(Font(R.font.barlowcondensed_bold))

    Box(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .clip(RoundedCornerShape(20.dp))
            .border(2.dp, Purple, RoundedCornerShape(20.dp))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Reports Section Title
            Text(
                text = "Reports",
                color = White,
                fontFamily = fontFamily,
                fontSize = 20.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // Submit a Report Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(Purple)
                    .clickable {
                        val intent = android.content.Intent(
                            context,
                            ReportsActivity::class.java
                        )
                        context.startActivity(intent)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Submit a Report",
                    color = BlueDarker,
                    fontFamily = fontFamily,
                    fontSize = 18.sp
                )
            }

            // View Reports Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(Purple)
                    .clickable {
                        val intent = android.content.Intent(
                            context,
                            ListReportsActivity::class.java
                        )
                        context.startActivity(intent)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "View Reports",
                    color = BlueDarker,
                    fontFamily = fontFamily,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun MainContent(preferenceID: Int, groupListState: MutableState<List<Group>>, discordUsername : String, discordId: String, isMatchmakingActive: MutableState<Boolean>,
                matchmakingStatus: MutableState<String?>, showDialog: MutableState<Boolean>, dialogMessage: MutableState<String>) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceEvenly, // Ensures even spacing
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FindGroup(preferenceID, context, discordId, isMatchmakingActive, matchmakingStatus, showDialog, dialogMessage) // Large Button

        ViewExistingGroups(context, groupListState, discordUsername) // Expands to fit content

        ReportsSection(context) // Expands to fit content
    }
}

//@Preview(showBackground = true)
//@Composable
//fun MainPreview() {
//    val groupListState = remember {mutableStateOf<List<Group>>(emptyList())}
//    val discord = "maddy_paulson"
//    val discordId = "751124124151185438"
//    Column (
//        modifier = Modifier
//            .fillMaxSize()
//            .background(color = BlueDarker),
//        verticalArrangement = Arrangement.Top,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ){
//        Header(discord, {}, {})
//        MainContent(preferenceID = -1, groupListState, discord) // Fix: Provide a valid preferenceID
//    }
//}