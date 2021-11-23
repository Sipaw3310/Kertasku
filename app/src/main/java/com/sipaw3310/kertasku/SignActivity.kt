package com.sipaw3310.kertasku

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign)

        val auth: FirebaseAuth = Firebase.auth

        val progressBar: ProgressBar = findViewById(R.id.signIn_progressBar)
        val errortext: TextView = findViewById(R.id.signIn_errorText)

        val emailfield: TextInputEditText = findViewById(R.id.signIn_emailInput)
        val passwordfield: TextInputEditText = findViewById(R.id.signIn_passwordInput)
        val signInButton: Button = findViewById(R.id.signInButton)
        val signUpLink: TextView = findViewById(R.id.signUpLink)

        signInButton.setOnClickListener {
            if(emailfield.text.toString() != "" && passwordfield.text.toString() != "") {
                signInButton.isEnabled = false
                progressBar.visibility = View.VISIBLE

                auth.signInWithEmailAndPassword(emailfield.text.toString(), passwordfield.text.toString())
                    .addOnCompleteListener(this) {
                        if(it.isSuccessful) {
                            val intent: Intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            signInButton.isEnabled = true
                            progressBar.visibility = View.GONE
                            errortext.visibility = View.VISIBLE
                            if(it.exception is FirebaseAuthInvalidUserException) {
                                errortext.text = getString(R.string.sign_in_invalid_user)
                            } else if(it.exception is FirebaseAuthInvalidCredentialsException) {
                                errortext.text = getString(R.string.sign_in_invalid_credential)
                            } else if(it.exception is FirebaseNetworkException) {
                                errortext.text = getString(R.string.sign_network_error)
                            } else {
                                errortext.text = getString(R.string.sign_in_failed)
                            }
                        }
                    }
            } else {
                if(emailfield.text.toString() == "") emailfield.error = getString(R.string.email_empty_error)
                if(passwordfield.text.toString() == "") passwordfield.error = getString(R.string.password_empty_error)
            }
        }

        signUpLink.setOnClickListener {
            val intent: Intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

    }
}