package com.example.gameon

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.gameon.api.methods.getGroupMembers
import com.example.gameon.api.methods.getUserGroups
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

                            val userList = groupMemberList.map {
                                it.user!!
                            }

                            userListState.value = userList
                        }
                    }
                    ReportButton(
                        "Submit Report",
                        containerColor = Error,
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
        "This person called me many bad words while playing The Sims. Send him to the gulag."
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
                containerColor = Error,
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