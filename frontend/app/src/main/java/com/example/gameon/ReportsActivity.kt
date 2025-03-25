package com.example.gameon

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.gameon.api.methods.getGroupMembers
import com.example.gameon.api.methods.getUserGroups
import com.example.gameon.api.methods.logout
import com.example.gameon.api.methods.submitReport
import com.example.gameon.classes.Group
import com.example.gameon.classes.Report
import com.example.gameon.classes.User
import com.example.gameon.composables.Avatar
import com.example.gameon.composables.DropdownInput
import com.example.gameon.composables.Header
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

        val selectedGroup = mutableStateOf<Group?>(null)
        val selectedUser = mutableStateOf<User?>(null)
        val reason = mutableStateOf("")

        val reasonError = mutableStateOf(false)
        val canSubmit = mutableStateOf(false)
        val width = 300.dp

        lifecycleScope.launch{ groupListState.value = getUserGroups(context = this@ReportsActivity) }

        setContent {
            Column(
                Modifier.fillMaxSize().background(color = BlueDarker),
                Arrangement.Top, Alignment.CenterHorizontally
            ){
                Header(
                    this@ReportsActivity,
                    { startActivity(Intent(
                        this@ReportsActivity,
                        UserSettingsActivity::class.java
                    )) },
                    { lifecycleScope.launch { logout(this@ReportsActivity) } }
                )
                Column (
                    Modifier.fillMaxSize(), Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
                    Alignment.CenterHorizontally
                ){
                    ReportTitle("Report User", Modifier.width(width))
                    ReportInputs(
                        groupListState.value, selectedGroup, userListState.value, selectedUser,
                        reason, reasonError, canSubmit, Modifier.width(width)
                    ) { lifecycleScope.launch {
                        val groupId = selectedGroup.value?.group_id ?: -1
                        val groupMemberList = getGroupMembers(groupId, this@ReportsActivity)
                        userListState.value = groupMemberList.map { it.user!! }
                    } }
                    ReportButtonArray(
                        canSubmit, width,
                        { lifecycleScope.launch { submitReport(
                            Report(
                                group_id = selectedGroup.value?.group_id ?: -1,
                                reported_discord_id = selectedUser.value?.discord_id ?: "0",
                                reason = reason.value,
                            ),
                            context = this@ReportsActivity,
                        ) { reasonError.value = true } } },
                        { finish() }
                    )
                }
            }
        }
    }
}

@Composable
fun ReportInputs(
    groupList: List<Group>,
    selectedGroup: MutableState<Group?>,
    userList: List<User>,
    selectedUser: MutableState<User?>,
    reason: MutableState<String>,
    reasonError: MutableState<Boolean>,
    canSubmit: MutableState<Boolean>,
    modifier: Modifier,
    onSelectedGroup: () -> Unit = { },
) {
    canSubmit.value = selectedGroup.value != null &&
            selectedUser.value != null &&
            reason.value.isNotBlank()

    Column (
        verticalArrangement = Arrangement.spacedBy(25.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        DropdownInput(
            "Group",
            groupList,
            selectedGroup,
            modifier = modifier,
            displayText = { it.group_name },
            onSelect = onSelectedGroup,
        )
        if (groupList.isNotEmpty() && selectedGroup.value != null)
            DropdownInput(
                "User",
                userList,
                selectedUser,
                modifier = modifier,
                displayText = { it.username },
                leadingIcon = {{ Avatar(it.discord_id, it.avatar) }}
            )
        TextInput(
            reason,
            modifier = modifier.height(300.dp).testTag("ReasonInput"),
            "Error: Please limit your input to 500 characters",
            reasonError.value
        )
    }
}

@Composable
fun ReportButtonArray(
    canSubmit: MutableState<Boolean>,
    width: Dp,
    onSubmit: () -> Unit,
    onCancel: () -> Unit
) {
    ReportButton(
        "Submit Report",
        containerColor = Red,
        enabled = canSubmit.value,
        modifier = Modifier.width(width).testTag("SubmitReportButton"),
        onClick = onSubmit
    )
    ReportButton(
        "Cancel",
        outlined = true,
        modifier = Modifier.width(width).testTag("CancelReportButton"),
        onClick = onCancel
    )
}

@Preview(showBackground = true)
@Composable
fun ReportsPreview() {
    val groupList = listOf(
        Group(1, "Sims Swappers", game_id=1, max_players=3)
    )
    val userList = listOf(
        User("1", "caboose4020", email="email@dc.com")
    )

    val selectedGroup = remember { mutableStateOf<Group?>(groupList[0]) }
    val selectedUser = remember { mutableStateOf<User?>(userList[0]) }
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
            ReportInputs(
                groupList,
                selectedGroup,
                userList,
                selectedUser,
                reason,
                remember { mutableStateOf(false) },
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