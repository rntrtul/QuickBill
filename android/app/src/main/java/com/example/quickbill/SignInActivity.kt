package com.example.quickbill

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.quickbill.ui.theme.QuickBillTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SignInActivity: AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        setContent{
            QuickBillTheme {
                SignInContent()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null){
            reload()
        }
    }

    private fun sendEmailVerification() {
        // [START send_email_verification]
        val user = auth.currentUser!!
        user.sendEmailVerification()
            .addOnCompleteListener(this) { task ->
                // Email Verification sent
            }
        // [END send_email_verification]
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
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
        auth.signOut()
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
        val (email, onEmailChange) = remember {
            mutableStateOf("")
        }
        val (password, onPasswordChange) = remember {
            mutableStateOf("")
        }


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
                    color = MaterialTheme.colorScheme.primary,
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
                    label = { Text(text = "Email", style = MaterialTheme.typography.labelLarge) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 21.dp),
                    shape = MaterialTheme.shapes.medium
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = { Text(text = "Password",style = MaterialTheme.typography.labelLarge) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 21.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation(),
                    shape = MaterialTheme.shapes.medium
                )
                TextButton(

                    onClick = { sendEmailVerification() },
                ) {
                    Text(text = "Forgot Password",  style = MaterialTheme.typography.labelLarge)
                }
                Button(
                    onClick = { signIn(email, password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                ) {
                    Text(text = "Login",  style = MaterialTheme.typography.labelLarge)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 42.dp, start = 14.dp, end = 14.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "Don't have an Account?",
                        modifier = Modifier
                            .padding(top=29.dp) ,
                        style = MaterialTheme.typography.labelLarge
                    )
                    TextButton(
                        onClick = {
                            signUp()
                        },
                        modifier = Modifier
                            .padding(top=14.dp)
                    ) {
                        Text(text = "Sign Up",style = MaterialTheme.typography.labelLarge)
                    }
                }

            }

        }


    }

}



