package com.example.passman.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun MyHeader1Text(textToDisplay: String){
    Text(
        text = textToDisplay,
        fontSize = 30.sp,
        fontStyle = FontStyle.Italic,
        fontWeight = FontWeight.Bold

    )
}

@Composable
fun MyHeader3Text(textToDisplay: String) {
    Text(
        text = textToDisplay,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun MyHeader5Text(textToDisplay: String) {
    Text(
        text = textToDisplay,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold
    )
}


