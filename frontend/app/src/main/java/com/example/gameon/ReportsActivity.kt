package com.example.gameon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.gameon.api.methods.submitReport
import com.example.gameon.classes.Group
import com.example.gameon.classes.Report
import com.example.gameon.classes.User
import com.example.gameon.composables.DropdownInput
import com.example.gameon.composables.Logo
import com.example.gameon.composables.ReportTitle
import com.example.gameon.composables.TextInput
import com.example.gameon.ui.theme.*
import kotlinx.coroutines.launch

class ReportsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //TODO: Pull groups and users live from backend

        val groupListState = mutableStateOf<List<Group>>(emptyList())
        val userListState = mutableStateOf<List<User>>(emptyList())

        val selectedGroupName = mutableStateOf("")
        val selectedUserName = mutableStateOf("")
        val reason = mutableStateOf("")

        val width = 300.dp

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
                        Modifier.width(width)
                    ) {
                        // Set group members to user
                    }
                    ReportButton(
                        "Submit Report",
                        containerColor = Error,
                    ) {
                        lifecycleScope.launch {
                            val selectedGroupObject = groupListState.value.find { it.group_name == selectedGroupName.value }
                            val groupId = selectedGroupObject?.group_id ?: 0 // Default to 0 if not found

                            val selectedUserObject = userListState.value.find { it.username == selectedUserName.value }
                            val discordId = selectedUserObject?.discord_id ?: "0" // Default to "0" if not found

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
                        outlined = true
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
    groupListState: List<Group>,
    selectedGroupName: MutableState<String>,
    userListState: List<User>,
    selectedUserName: MutableState<String>,
    reason: MutableState<String>,
    modifier: Modifier,
    onSelectedGroup: () -> Unit = { },
) {

    Column (
        verticalArrangement = Arrangement.spacedBy(25.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        DropdownInput(
            "Group",
            groupListState.map { it.group_name },
            selectedGroupName,
            modifier = modifier,
            onSelect = onSelectedGroup
        )
        if (selectedGroupName.value != "")
            DropdownInput(
                "User",
                userListState.map { it.username },
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

@Composable
fun Icon() {
    Box (
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .width(40.dp)
            .height(40.dp)
            .background(
                color = DiscordBlurple,
                shape = CircleShape
            )
    ) {
        Image(
            painterResource(R.drawable.discord_icon),
            "Discord Icon",
            modifier = Modifier
                .width(26.dp)
                .height(20.dp)
        )
    }
}

@Composable
fun ReportButton(
    text: String,
    textColor: Color = White,
    containerColor: Color = BlueDarker,
    outlined: Boolean = false,
    onClick : () -> Unit = { }
) {
    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor
        ),
        border = ButtonDefaults.outlinedButtonBorder(
            enabled = outlined
        ),
        modifier = Modifier
            .width(300.dp)
    ) {
        Text(
            text,
            fontFamily = FontFamily(Font(R.font.lato_bold)),
            color = textColor
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
                Modifier.width(width)
            )
            ReportButton(
                "Report User",
                containerColor = Error,
            )
            ReportButton(
                "Cancel",
                outlined = true
            )
        }
    }

}