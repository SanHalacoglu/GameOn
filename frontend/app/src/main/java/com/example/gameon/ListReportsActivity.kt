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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
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
import com.example.gameon.api.methods.getReports
import com.example.gameon.api.methods.logout
import com.example.gameon.classes.Group
import com.example.gameon.classes.Report
import com.example.gameon.classes.User
import com.example.gameon.composables.Icon
import com.example.gameon.composables.Logo
import com.example.gameon.composables.ReportButton
import com.example.gameon.composables.ReportTitle
import com.example.gameon.ui.theme.*
import kotlinx.coroutines.launch

class ListReportsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val width = 300.dp

        val reportListState = mutableStateOf<List<Report>>(emptyList())
        val user = SessionDetails(this).getUser()
        val discordUsername = user?.username ?: "Unknown"

        lifecycleScope.launch{

            val reportList = getReports(
                unresolved = true,
                context = this@ListReportsActivity
            )
            if (reportList.isNotEmpty()) {
                reportListState.value = reportList
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
                        // TODO: open user settings
                    },
                    {
                        lifecycleScope.launch {
                            logout(this@ListReportsActivity)
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
                    ) {
                        ReportTitle(
                            "List Reports",
                            Modifier.width(width)
                        )
                        ReportList(
                            reportListState.value,
                            Modifier
                                .width(width)
                                .height(550.dp)
                        ) {
                                reportId ->
                            startActivity(Intent(
                                this@ListReportsActivity,
                                ViewReportsActivity::class.java
                            ).apply { putExtra("ReportId", reportId) })
                            finish()
                        }
                        ReportButton(
                            "Back",
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
fun ListReportHeader(username: String, onSettings: () -> Unit, onLogout: () -> Unit) {
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
fun ReportList(
    reportList: List<Report>,
    modifier: Modifier,
    onClick: (reportId: Int) -> Unit
) {
    Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        reportList.forEach { report ->
            ReportListItem(
                report.report_id!!,
                report.reported_user!!.username,
            ) {
                onClick(report.report_id)
            }
        }
        if (reportList.isEmpty())
            Text(
                "There are no reports to review.",
                color = White
            )
    }
}

@Composable
fun ReportListItem(
    reportId: Int,
    username: String,
    onClick: () -> Unit = {}
) {
    Button(
        contentPadding = PaddingValues(0.dp),
        shape = RectangleShape,
        onClick = onClick
    ) {
        ListItem(
            headlineContent = { Text("Report $reportId") },
            leadingContent = { Icon() },
            trailingContent = { Text(username) },
            colors = ListItemDefaults.colors(
                containerColor = BlueDark,
                headlineColor = White,
                trailingIconColor = White
            )
        )
    }

}

@Preview(showBackground = true)
@Composable
fun ListReportsPreview() {

    val exampleReport = Report(
        report_id = 1,
        reporter = User(
            discord_id = "1",
            username = "sanhal23",
            email = "sanhal23@discord.com",
            banned = false
        ),
        reported_user = User(
            discord_id = "2",
            username = "rubination",
            email = "rubination@discord.com",
            banned = false
        ),
        group = Group(
            group_id = 1,
            group_name = "Sims Swappers",
            max_players = 3,
            game_id = 1
        ),
        reason = "This person keeps saying that I stole his diamonds but consistently " +
                "steals other people's netherite."
    )

    val exampleReportList = List(5) {exampleReport}

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
        ) {
            ReportTitle(
                "List Reports",
                Modifier.width(width)
            )
            ReportList(
                exampleReportList,
                Modifier
                    .width(width)
                    .height(550.dp)
            ) { }
            ReportButton(
                "Back",
                outlined = true,
                modifier = Modifier.width(width)
            )
        }
    }
}