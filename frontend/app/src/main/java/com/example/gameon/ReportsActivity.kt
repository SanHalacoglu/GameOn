package com.example.gameon

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.gameon.api.methods.SessionDetails
import com.example.gameon.api.methods.getGroupMembers
import com.example.gameon.api.methods.getUserGroups
import com.example.gameon.api.methods.logout
import com.example.gameon.api.methods.submitReport
import com.example.gameon.classes.Group
import com.example.gameon.classes.Report
import com.example.gameon.classes.User
import com.example.gameon.composables.DropdownInput
import com.example.gameon.composables.Icon
import com.example.gameon.composables.Logo
import com.example.gameon.composables.ReportButton
import com.example.gameon.composables.ReportTitle
import com.example.gameon.composables.TextInput
import com.example.gameon.ui.theme.*
import kotlinx.coroutines.launch

class ReportsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val groupListState = mutableStateOf<List<Group>>(emptyList())
        val userListState = mutableStateOf<List<User>>(emptyList())

        val selectedGroupName = mutableStateOf("")
        val selectedUserName = mutableStateOf("")
        val reason = mutableStateOf("")

        val user = SessionDetails(this).getUser()
        val discordUsername = user?.username ?: "Unknown"

        val canSubmit = mutableStateOf(false)

        val width = 300.dp

        lifecycleScope.launch{
            val groupList = getUserGroups(
                context = this@ReportsActivity
            )
            if (groupList.isNotEmpty()) {
                groupListState.value = groupList
            } else {
                selectedGroupName.value = "You are not in any groups."
            }
        }

        setContent {
            Column(
                modifier = Modifier
                .fillMaxSize()
                .background(color = BlueDarker),
                verticalArrangement = Arrangement.Top, // Keep everything stacked from the top
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                ReportHeader(
                    discordUsername,
                    {
                        val intent = Intent(
                            this@ReportsActivity,
                            UserSettingsActivity::class.java
                        )
                        startActivity(intent)
                    },
                    {
                        lifecycleScope.launch {
                            logout(this@ReportsActivity)
                        }
                    }
                )
                Box (
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = BlueDarker)
                ) {
                    Box(
                        modifier = Modifier
                            .align(alignment = Alignment.TopStart)
                            .offset(15.dp, 30.dp)
                    )
                    Column (
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        ReportTitle(
                            "Report User",
                            Modifier.width(width)
                        )
                        Reports(
                            groupListState.value,
                            selectedGroupName,
                            userListState.value,
                            selectedUserName,
                            reason,
                            canSubmit,
                            Modifier.width(width)
                        ) {
                            lifecycleScope.launch {
                                val selectedGroupObject = groupListState.value
                                    .find { it.group_name == selectedGroupName.value }
                                // Default to 0 if not found
                                val groupId = selectedGroupObject?.group_id ?: 0

                                val groupMemberList =
                                    getGroupMembers(groupId, this@ReportsActivity)

                                val userList = groupMemberList.map { it.user!! }

                                userListState.value = userList
                            }
                        }
                        ReportButton(
                            "Submit Report",
                            containerColor = Red,
                            enabled = canSubmit.value,
                            modifier = Modifier.width(width)
                        ) {
                            lifecycleScope.launch {
                                val selectedGroupObject = groupListState.value
                                    .find { it.group_name == selectedGroupName.value }
                                // Default to 0 if not found
                                val groupId = selectedGroupObject?.group_id ?: 0

                                val selectedUserObject = userListState.value
                                    .find { it.username == selectedUserName.value }
                                // Default to "0" if not found
                                val discordId = selectedUserObject?.discord_id ?: "0"

                                submitReport(
                                    Report(
                                        group_id = groupId,
                                        reported_discord_id = discordId,
                                        reason = reason.value,
                                    ),
                                    context = this@ReportsActivity,
                                )
                            }
                        }
                        ReportButton(
                            "Cancel",
                            outlined = true,
                            modifier = Modifier.width(width)
                        ) {
                            finish()
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportHeader(username: String, onSettings: () -> Unit, onLogout: () -> Unit) {
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
                androidx.compose.material3.Icon(
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
fun Reports(
    groupList: List<Group>,
    selectedGroupName: MutableState<String>,
    userList: List<User>,
    selectedUserName: MutableState<String>,
    reason: MutableState<String>,
    canSubmit: MutableState<Boolean>,
    modifier: Modifier,
    onSelectedGroup: () -> Unit = { },
) {
    canSubmit.value = selectedGroupName.value.isNotBlank() &&
            selectedUserName.value.isNotBlank() &&
            reason.value.isNotBlank()

    Column (
        verticalArrangement = Arrangement.spacedBy(25.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        DropdownInput(
            "Group",
            groupList.map { it.group_name },
            selectedGroupName,
            modifier = modifier,
            onSelect = onSelectedGroup
        )
        if (groupList.isNotEmpty() && selectedGroupName.value.isNotEmpty())
            DropdownInput(
                "User",
                userList.map { it.username },
                selectedUserName,
                { Icon() },
                modifier = modifier
            )
        TextInput(
            reason,
            modifier = modifier.height(350.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReportsPreview() {
    val groupList = emptyList<Group>()
    val userList = emptyList<User>()

    val selectedGroupName = remember { mutableStateOf("Sims Swappers") }
    val selectedUserName = remember { mutableStateOf("caboose4020") }
    val reason = remember { mutableStateOf(
        "This person called me many bad words while playing The Sims."
    ) }

    val width = 300.dp

    Box (
        modifier = Modifier
            .fillMaxSize()
            .background(color = BlueDarker)
    ) {
        Box(
            modifier = Modifier
                .align(alignment = Alignment.TopStart)
                .offset(15.dp, 30.dp)
        ) { Logo() }
        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            ReportTitle(
                "Report User",
                Modifier.width(width)
            )
            Reports(
                groupList,
                selectedGroupName,
                userList,
                selectedUserName,
                reason,
                remember { mutableStateOf(true) },
                Modifier.width(width)
            )
            ReportButton(
                "Report User",
                containerColor = Red,
                modifier = Modifier.width(width)
            )
            ReportButton(
                "Cancel",
                outlined = true,
                modifier = Modifier.width(width)
            )
        }
    }

}