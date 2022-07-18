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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.quickbill.firebaseManager.FirebaseManager
import com.example.quickbill.ui.theme.QuickBillTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            QuickBillTheme {
                SignUpContent()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = FirebaseManager.getAuth().currentUser
        if(currentUser != null){
            reload()

        }
    }
    private fun createAccount(email: String, password: String) {
        // [START create_user_with_email]
        FirebaseManager.getAuth().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(SignUpActivity.TAG, "createUserWithEmail:success")
                    val user = FirebaseManager.getAuth().currentUser
                    updateUI(user)

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(SignUpActivity.TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
        // [END create_user_with_email]
    }
    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val intent= Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun reload() {
        finish()
        startActivity(getIntent())
    }

    companion object {
        private const val TAG = "EmailPassword"
    }

    private fun signIn() {
       val intent= Intent(this,SignInActivity::class.java)
        startActivity(intent)
        finish()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Preview
    @Composable
    fun SignUpContent() {
        val (email, onEmailChange) = remember {
            mutableStateOf("")
        }
        val (password, onPasswordChange) = remember {
            mutableStateOf("")
        }
        val focusManager = LocalFocusManager.current
        Column(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
        ) {
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
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.primaryContainer,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primaryContainer),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 21.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus()}),
                    shape = MaterialTheme.shapes.medium,
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = { Text(text = "Password", color = MaterialTheme.colorScheme.onPrimaryContainer) },
                    singleLine = true,
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
                )
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    onClick = { createAccount(email, password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                ) {
                    Text(text = "Register", color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 42.dp, start = 14.dp, end = 14.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(
                        onClick = {
                            signIn()
                        },
                        modifier = Modifier
                            .padding(top=14.dp)
                    ) {
                        Text(text = "Already have an Account? Login", color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.labelLarge)
                    }
                }

            }


        }


    }

}
