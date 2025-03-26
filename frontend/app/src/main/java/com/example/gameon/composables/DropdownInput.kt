package com.example.gameon.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gameon.R
import com.example.gameon.ui.theme.BlueDark
import com.example.gameon.ui.theme.BlueDarker
import com.example.gameon.ui.theme.BlueLight
import com.example.gameon.ui.theme.Red
import com.example.gameon.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DropdownInput(
    label: String,
    options: List<T>,
    selectedOption: MutableState<T?>,
    modifier: Modifier = Modifier,
    outlined: Boolean = false,
    supportingText: String = "",
    displayText: (T) -> String,
    leadingIcon: ((T) -> @Composable (() -> Unit)?)? = null,
    onSelect: () -> Unit = { }
) {
    var expanded by remember { mutableStateOf(false) }
    val fontFamily = FontFamily(Font(R.font.lato_regular))

    Column {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = modifier
        ) {
            DropdownTextField(label, selectedOption, expanded, outlined, displayText, leadingIcon,
                Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable))

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = modifier
                    .background(BlueDark)
                    .padding(horizontal = 2.dp)
                    .heightIn(max = 250.dp)
            ) {
                options.forEachIndexed { index, option ->
                    val textColor = if (option == selectedOption.value) BlueLight else White
                    val bgColor = if (option == selectedOption.value) BlueDarker else BlueDark

                    DropdownMenuItem(
                        text = { Text(displayText(option)) },
                        onClick = {
                            selectedOption.value = option
                            onSelect()
                            expanded = false
                        },
                        colors = dropdownMenuItemColors(textColor),
                        leadingIcon = leadingIcon?.invoke(option),
                        modifier = Modifier.background(bgColor).testTag("${label}_option_$index")
                    )
                }
            }
        }
        if (supportingText.isNotBlank())
            Text(
                supportingText,
                fontFamily=fontFamily,
                fontSize = 12.sp,
                color = Color(0xBBFFFFFF),
                modifier = Modifier.padding(horizontal=16.dp)
            )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DropdownTextField(
    label: String,
    selectedOption: MutableState<T?>,
    expanded: Boolean,
    outlined: Boolean,
    displayText: (T) -> String,
    leadingIcon: ((T) -> @Composable (() -> Unit)?)? = null,
    modifier: Modifier // pass menuAnchor modifier
) {
    val fontFamily = FontFamily(Font(R.font.lato_regular))

    if (outlined) OutlinedTextField(
        readOnly = true,
        value = selectedOption.value?.let { displayText(it) } ?: "",
        onValueChange = { },
        label = { Text(label.uppercase(), fontFamily = fontFamily) },
        textStyle = TextStyle(fontFamily = fontFamily, fontSize = 16.sp),
        shape = RoundedCornerShape(10.dp),
        colors = outlinedDropdownTextFieldColors(),
        leadingIcon = selectedOption.value?.let { leadingIcon?.invoke(it) },
        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        modifier = modifier.fillMaxWidth().testTag("${label}TextField")
    ) else TextField(
        readOnly = true,
        value = selectedOption.value?.let { displayText(it) } ?: "",
        onValueChange = { },
        label = { Text(label, fontFamily = fontFamily) },
        textStyle = TextStyle(fontFamily = fontFamily),
        colors = dropdownTextFieldColors(),
        leadingIcon = selectedOption.value?.let { leadingIcon?.invoke(it) },
        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        modifier = modifier.fillMaxWidth().testTag("${label}TextField")
    )
}

@Composable
fun dropdownTextFieldColors(): TextFieldColors {
    return TextFieldDefaults.colors(
        unfocusedTextColor = White,
        unfocusedLabelColor = White,
        unfocusedTrailingIconColor = White,
        unfocusedContainerColor = BlueDark,
        unfocusedIndicatorColor = BlueDark,
        unfocusedPlaceholderColor = Color(0xAAFFFFFF),
        focusedTextColor = White,
        focusedLabelColor = White,
        focusedTrailingIconColor = White,
        focusedContainerColor = BlueDark,
        focusedIndicatorColor = White,
        focusedPlaceholderColor = Color(0xAAFFFFFF),
    )
}

@Composable
fun outlinedDropdownTextFieldColors(): TextFieldColors {
    return OutlinedTextFieldDefaults.colors(
        unfocusedBorderColor = White,
        unfocusedLabelColor = White,
        unfocusedTextColor = White,
        unfocusedTrailingIconColor = White,
        focusedBorderColor = BlueLight,
        focusedLabelColor = BlueLight,
        focusedTextColor = BlueLight,
        focusedTrailingIconColor = BlueLight,
        errorBorderColor = Red,
        errorLabelColor = Red,
        errorTextColor = Red,
        errorTrailingIconColor = Red,
    )
}

fun dropdownMenuItemColors(textColor: Color): MenuItemColors {
    return MenuItemColors(
        textColor = textColor,
        leadingIconColor = Color.Transparent,
        trailingIconColor = Color.Transparent,
        disabledTextColor = Color.Transparent,
        disabledLeadingIconColor = Color.Transparent,
        disabledTrailingIconColor = Color.Transparent
    )
}