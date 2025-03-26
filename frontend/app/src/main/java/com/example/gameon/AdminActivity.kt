package com.example.gameon

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.example.gameon.api.methods.createAdmin
import com.example.gameon.api.methods.createGame
import com.example.gameon.api.methods.getAdmins
import com.example.gameon.api.methods.getUsers
import com.example.gameon.classes.User
import com.example.gameon.composables.Avatar
import com.example.gameon.composables.ReportButton
import com.example.gameon.composables.SimpleHeader
import com.example.gameon.composables.TextInput
import com.example.gameon.ui.theme.*
import kotlinx.coroutines.launch

class AdminActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val gameName = mutableStateOf("")
        val gameDescription = mutableStateOf("")

        val userList = mutableStateOf(emptyList<User>())

        lifecycleScope.launch {
            val users = getUsers(this@AdminActivity)
            val admins = getAdmins(this@AdminActivity)

            userList.value = users.filter {  user ->
                admins.find { admin ->
                    admin.discord_id == user.discord_id ||
                            admin.user?.discord_id == user.discord_id
                } == null
            }
            Log.d("AdminActivity", "UserList = ${userList.value}")
        }

        setContent {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = BlueDarker),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SimpleHeader("Admin Panel")
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 30.dp)
                        .weight(1.0F),
                    verticalArrangement = Arrangement.spacedBy(30.dp)
                ){
                    PromoteAdminSection(this@AdminActivity, lifecycleScope, userList)
                    AddGameSection( gameName, gameDescription )
                    { lifecycleScope.launch {
                        createGame(this@AdminActivity, gameName.value, gameDescription.value)
                        gameName.value = ""; gameDescription.value = ""
                    } }
                }
                ReportButton(
                    "Back", outlined=true,
                    modifier=Modifier
                        .fillMaxWidth(0.9F)
                        .padding(top = 0.dp, bottom = 50.dp),
                ) { finish () }
            }
        }
    }
}

@Composable
fun PromoteAdminSection(
    context: Context,
    scope: LifecycleCoroutineScope? = null,
    users: MutableState<List<User>>
) {
    val fontFamily = FontFamily(Font(R.font.barlowcondensed_bold))

    val usernameInput = remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    val enabled = users.value.isNotEmpty() && usernameInput.value.isNotEmpty()
    var chosenUser: User? by remember { mutableStateOf(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .clip(RoundedCornerShape(20.dp))
            .border(2.dp, Blue, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Promote User to Admin",
            color = White,
            fontFamily = fontFamily,
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        TextInput(
            "Username", usernameInput,
            modifier=Modifier.height(60.dp),
            fontSize = 16.sp, singleLine=true,
            errorText = "This user is either already an admin or doesn't exist.",
            isError = isError,
            containerColor = BlueDark
        )
        ReportButton(
            "Search for User",
            containerColor=Blue,
            textColor=BlueDarker,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth()
        ) {
            chosenUser = users.value.find { user -> user.username == usernameInput.value }
            isError = chosenUser == null
        }
    }
    if(chosenUser != null)
        AdminDialog(
            chosenUser!!,
            { scope?.launch {
                val adminCreated = createAdmin(context, chosenUser!!.discord_id)
                if (adminCreated) {
                    Toast.makeText(context, "${chosenUser!!.username} is now an admin!", Toast.LENGTH_SHORT).show()
                    users.value = users.value.filter { user -> user.discord_id != chosenUser!!.discord_id }
                    usernameInput.value = ""; chosenUser = null
                } else Toast.makeText(context, "Error occurred promoting admin.", Toast.LENGTH_SHORT).show()
            } },
            { usernameInput.value = ""; chosenUser = null }
        )
}

@Composable
fun AdminDialog(
    user: User,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit
) {
    val fontFamily = FontFamily(Font(R.font.lato_bold))
    Dialog(onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor=BlueDark, contentColor=White)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Avatar(user.discord_id, user.avatar, size=75.dp,
                    modifier = Modifier.border(4.dp, Blue, CircleShape))
                Text(
                    buildAnnotatedString {
                        append("Would you like to promote ")
                        withStyle(style = SpanStyle(color=Blue)) {append(user.username)}
                        append(" to admin?")
                    },
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ReportButton(
                        "Cancel", outlined=true, containerColor=BlueDark,
                        modifier = Modifier.width(100.dp),
                        onClick = onDismissRequest
                    )
                    ReportButton(
                        "Confirm", textColor = BlueDarker, containerColor = Blue,
                        modifier = Modifier.width(100.dp),
                        onClick = onConfirm
                    )
                }
            }
        }
    }
}

@Composable
fun AddGameSection(
    nameInput: MutableState<String>,
    descriptionInput: MutableState<String>,
    onSubmit: () -> Unit
) {
    val fontFamily = FontFamily(Font(R.font.barlowcondensed_bold))
    val enabled = nameInput.value.isNotBlank() && descriptionInput.value.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxWidth(0.9F)
            .clip(RoundedCornerShape(20.dp))
            .border(2.dp, Purple, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Add a Game",
            color = White,
            fontFamily = fontFamily,
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        TextInput("Name", nameInput, modifier=Modifier.height(60.dp), fontSize = 16.sp, singleLine=true)
        TextInput("Description", descriptionInput, modifier=Modifier.height(200.dp), fontSize = 16.sp)
        ReportButton(
            "Add Game",
            containerColor=Purple,
            textColor=BlueDarker,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            onClick = onSubmit
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AdminPreview() {
    val scrollState = rememberScrollState()
    val gameName = remember {mutableStateOf("")}
    val gameDesc = remember {mutableStateOf("")}
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = BlueDarker),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SimpleHeader("Admin Panel")
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(vertical = 30.dp)
                .weight(1.0F),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ){
            PromoteAdminSection(LocalContext.current, users = remember { mutableStateOf(emptyList()) })
            AddGameSection(gameName, gameDesc){}
        }
        ReportButton(
            "Back", outlined=true,
            modifier=Modifier
                .fillMaxWidth(0.9F)
                .padding(top = 10.dp, bottom = 50.dp)
        )
    }
}