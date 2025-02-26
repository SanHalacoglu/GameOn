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
import com.example.gameon.composables.DropdownInput
import com.example.gameon.composables.Logo
import com.example.gameon.composables.ReportTitle
import com.example.gameon.composables.TextInput
import com.example.gameon.ui.theme.*

class ReportsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val groupList = listOf("Sims Swappers", "Dota Base", "Crafty Kings")
        var userList = listOf("")

        val selectedGroup = mutableStateOf("")
        val selectedUser = mutableStateOf("")
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
                        groupList,
                        selectedGroup,
                        userList,
                        selectedUser,
                        reason,
                        Modifier.width(width)
                    ) {
                        userList = listOf("maddy_paulson", "rubination", "caboose4020")
                    }
                    ReportButton(
                        "Report User",
                        containerColor = Error,
                    )
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
    groupList: List<String>,
    selectedGroup: MutableState<String>,
    userList: List<String>,
    selectedUser: MutableState<String>,
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
            groupList,
            selectedGroup,
            modifier = modifier,
            onSelect = onSelectedGroup
        )
        if (selectedGroup.value != "")
            DropdownInput(
                "User",
                userList,
                selectedUser,
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
    val groupList = listOf("Sims Swappers", "Dota Base", "Crafty Kings")
    val userList = listOf("maddy_paulson", "rubination", "caboose4020")

    val selectedGroup = remember { mutableStateOf("Sims Swappers") }
    val selectedUser = remember { mutableStateOf("caboose4020") }
    val reason = remember { mutableStateOf(
        "This person called me many bad words while playing The Sims. Send her to the gulag."
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
                selectedGroup,
                userList,
                selectedUser,
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