package com.example.gameon

import android.hardware.lights.Light
import android.os.Bundle
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gameon.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = BlueDarker),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Header()
                MainContent()
            }
        }
    }
}

@Composable
fun Header() {
    val fontFamily = FontFamily(Font(R.font.barlowcondensed_bold))
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
                    fontFamily = fontFamily,
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
            Icon(
                imageVector = Icons.Default.AccountCircle, // Default profile icon
                contentDescription = "Profile Icon",
                tint = Purple, // Adjust color as needed
                modifier = Modifier.size(90.dp) // Set icon size
            )

            // Username with Glow Effect
            Text(
                text = "Username",
                color = Purple, // Pinkish-white glow
                style = TextStyle(
                    fontFamily = fontFamily,
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
fun FindGroup(){
    val fontFamily = FontFamily(Font(R.font.barlowcondensed_bold))

    Box(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(70.dp) // Increased height
            .clip(RoundedCornerShape(50.dp))
            .background(Purple) // Solid background instead of border
            .clickable {
                // TODO: Trigger matchmaking algorithm
            },
        contentAlignment = Alignment.Center
    ){
        Text(
            text = "Find Group",
            color = BlueDarker,
            style = TextStyle(
                fontFamily = fontFamily,
                fontSize = 25.sp, // Adjust size as needed
            )
        )
    }
}

@Composable
fun ViewExistingGroups(){
    val fontFamily = FontFamily(Font(R.font.barlowcondensed_bold))

    Box(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(200.dp) // Increased height for better group display
            .clip(RoundedCornerShape(20.dp))
            .border(2.dp, Purple, RoundedCornerShape(20.dp))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "My Existing Groups",
                color = White,
                fontFamily = fontFamily,
                fontSize = 20.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // Placeholder List of Groups (Replace with actual user groups)
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 8.dp) // Fix: Use contentPadding instead
            ) {
                items(listOf("Group 1", "Group 2", "Group 3")) { groupName ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(Purple.copy(alpha = 0.2f)) // Slightly transparent
                            .clickable {
                                // TODO: Navigate to View Existing Group Page
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = groupName,
                            color = White,
                            fontFamily = fontFamily,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp)) // Add spacing between items
                }
            }

        }
    }
}

@Composable
fun ReportsSection() {
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
                    .clip(RoundedCornerShape(15.dp))
                    .background(Purple)
                    .clickable {
                        // TODO: Navigate to Submit Report Page
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
                    .clip(RoundedCornerShape(15.dp))
                    .background(Purple) // Slightly different opacity
                    .clickable {
                        // TODO: Navigate to View Reports Page
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
fun MainContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceEvenly, // Ensures even spacing
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FindGroup() // Large Button

        ViewExistingGroups() // Expands to fit content

        ReportsSection() // Expands to fit content
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(color = BlueDarker),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Header()
        MainContent()
    }
}