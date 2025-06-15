import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.passman.R
import com.example.passman.navigation.Screen
import com.example.passman.ui.components.MyHeader1Text
import com.example.passman.ui.components.MyHeader3Text
import com.example.passman.ui.components.MyHeader5Text
import com.example.passman.ui.components.MyNextButton
import com.example.passman.ui.theme.CandyAppleRed


//@Preview(showBackground = true)
//@Composable
//fun RecoverKeyScreenPreview(){
//    RecoveryKeyScreen()
//}


//add navController: NavController

@Composable
fun RecoveryKeyScreen(navController: NavController, recoveryKey: String){

    val context = LocalContext.current
    val reckey = recoveryKey.chunked(4).joinToString("-")
    val checkedState = remember { mutableStateOf(false) }
    var shwerror by remember { mutableStateOf(false) }

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxSize()
    ){
        MyHeader1Text(stringResource(R.string.master_password_set))
        MyHeader3Text(stringResource(R.string.recovery_key_generated))
        //MyHeader3Text("This is your recovery key")
        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.LightGray)
        ) {
            Row {
                Text(text = reckey, fontSize = 40.sp)
                IconButton(onClick = {
                    val clipboard =
                        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("reckey", recoveryKey)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_content_copy_24),
                        contentDescription = stringResource(R.string.copy),
                        tint = Color.Black
                    )
                }
            }
        }


        MyHeader3Text(stringResource(R.string.pleas_note_this_key_it_isn_t_stored_anywhere))
        MyHeader5Text(stringResource(R.string.if_you_forget_the_key_you_won_t_be_able_to_recover_your_data))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = checkedState.value, onCheckedChange = {checkedState.value = it})
            Text(stringResource(R.string.i_saved_the_key))
        }
        if(shwerror){
            Text(stringResource(R.string.please_save_the_key), color = CandyAppleRed, fontSize = 18.sp)
        } else {
            Text(stringResource(R.string.please_save_the_key), modifier = Modifier.alpha(0f))
        }

        MyNextButton(
            oncClicked = {
                if(checkedState.value){
                    navController.navigate(Screen.ConfirmRecoveryKey.route + "/$recoveryKey")
                } else {
                    shwerror = true
                }
                //navController.navigate(Screen.ConfirmRecoveryKey.route + "/$recoveryKey")
            }
        )

    }
}

