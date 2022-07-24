package com.example.quickbill

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.quickbill.firebaseManager.FirebaseManager
import com.example.quickbill.ui.theme.QuickBillTheme
import com.google.firebase.auth.FirebaseUser
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue



class SignInActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            QuickBillTheme {
                SignInContent()
            }
        }
        FirebaseManager.initialize()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = FirebaseManager.getAuth().currentUser
        if(currentUser != null){
            val intent= Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun sendEmailVerification() {
        // [START send_email_verification]
        val user = FirebaseManager.getAuth().currentUser!!
        user.sendEmailVerification()
            .addOnCompleteListener(this) { task ->
                // Email Verification sent
            }
        // [END send_email_verification]
    }

    private fun signIn(email: String, password: String) {
        FirebaseManager.getAuth().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = FirebaseManager.getAuth().currentUser
                    updateUI(user)

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
    // [END on_start_check_user]

    private fun signUp() {
        val intent= Intent(this,SignUpActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user == null) {
            return
        }
        val intent= Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun reload() {
        FirebaseManager.getAuth().signOut()
        finish()
        startActivity(getIntent())
    }

    companion object {
        private const val TAG = "EmailPassword"
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Preview
    @Composable
    fun SignInContent() {
        val (email, onEmailChange) = rememberSaveable {
            mutableStateOf("")
        }
        val (password, onPasswordChange) = rememberSaveable {
            mutableStateOf("")
        }
        val focusManager = LocalFocusManager.current
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
        )
        {
            Row(
                horizontalArrangement = Arrangement.Start,
            ) {
                Text(
                    text = "QuickBill",
                    style = MaterialTheme.typography.displayMedium,
                    modifier = Modifier
                        .padding(28.dp),
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    label = { Text(text = "Email", color = MaterialTheme.colorScheme.onPrimaryContainer) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 21.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.primaryContainer,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primaryContainer),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus()}),
                    shape = MaterialTheme.shapes.medium,
                    singleLine = true
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = { Text(text = "Password",color = MaterialTheme.colorScheme.onPrimaryContainer) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 21.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.primaryContainer,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primaryContainer),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus()}),
                    visualTransformation = PasswordVisualTransformation(),
                    shape = MaterialTheme.shapes.medium,
                    singleLine = true
                )
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    onClick = { signIn(email, password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                ) {
                    Text(text = "Login", color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 42.dp, start = 14.dp, end = 14.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(
                        onClick = {
                            signUp()
                        },
                        modifier = Modifier
                            .padding(top=14.dp)
                    ) {
                        Text(text = "Don't have an Account? Sign Up",color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.labelLarge)
                    }
                }

            }

        }


    }

}



