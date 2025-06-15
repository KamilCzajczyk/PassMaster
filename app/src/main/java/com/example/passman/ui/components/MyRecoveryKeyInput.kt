package com.example.passman.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.passman.R

@Composable
fun MyRecoveryKeyInput(onRecoveryKeyChanged: (String) -> Unit) {
    var recoveryKey by remember { mutableStateOf(TextFieldValue("")) }

    fun formatRecoveryKey(input: String): String {
        val raw = input.replace("-", "").uppercase().take(12)
        return raw.chunked(4).joinToString("-")
    }

    fun filterInput(old: TextFieldValue, new: TextFieldValue): TextFieldValue {
        // Remove all invalid dashes
        val filtered = new.text.filterIndexed { index, c ->
            if (c == '-') {
                // Only allow dash at positions 4 and 9 in formatted string
                index == 4 || index == 9
            } else {
                true
            }
        }
        val formatted = formatRecoveryKey(filtered)
        // Limit to 14 chars (12 + 2 dashes)
        val limited = if (formatted.length > 14) formatted.take(14) else formatted
        return TextFieldValue(
            limited,
            selection = androidx.compose.ui.text.TextRange(limited.length)
        )
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .width(290.dp)
    ) {
        OutlinedTextField(
            value = recoveryKey,
            onValueChange = {
                val filtered = filterInput(recoveryKey, it)
                recoveryKey = filtered
                onRecoveryKeyChanged(filtered.text.replace("-", ""))
            },
            label = { Text(stringResource(R.string.recovery_key)) },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                cursorColor = Color.Black,
                focusedLabelColor = Color.Black,
                unfocusedTextColor = Color.Black,
                unfocusedLabelColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                focusedBorderColor = Color.Black,
                focusedPlaceholderColor = Color.Black

            )
//            colors = TextFieldDefaults.colors(
//                unfocusedTextColor = Color.Black,
//                focusedTextColor = Color.Black,
//                unfocusedContainerColor = Color.White,
//                focusedContainerColor = Color.White,
//                focusedLabelColor = Color.Black,
//                unfocusedLabelColor = Color.Black,
//                unfocusedIndicatorColor = Color.Transparent,
//                focusedIndicatorColor = Color.Transparent,
//                cursorColor = Color.Black,
//            )
        )
    }
}