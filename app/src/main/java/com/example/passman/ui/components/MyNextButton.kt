package com.example.passman.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.passman.R

@Composable
fun MyNextButton(oncClicked: () -> Unit){
    Button(
        onClick = oncClicked,
        modifier = Modifier.size(width = 160.dp, height = 50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
        elevation = ButtonDefaults.buttonElevation(10.dp)

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Next",
                fontSize = 22.sp,
                color = Color.White
            )
            Icon(
                painter = painterResource(R.drawable.outline_line_end_arrow_notch_24),
                contentDescription = "next",
                modifier = Modifier.size(30.dp),
                tint = Color.White
            )

        }
    }
}
