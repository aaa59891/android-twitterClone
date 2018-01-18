package com.example.chongchenlearn901.twitterclone

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.parse.ParseUser
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ParseUser.getCurrentUser()?.let {
            gotoMainPage()
            return
        }
        btnSignUp.setOnClickListener(signUpClick)
        btnLogin.setOnClickListener(loginClick)
    }

    private val signUpClick = View.OnClickListener{
        val user = ParseUser()
        user.username = etAccount.text.toString()
        user.setPassword(etPassword.text.toString())
        user.signUpInBackground {
            it?.let {
                Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT).show();
            }?: run{
                Toast.makeText(applicationContext, "Sign Up successfully!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private val loginClick = View.OnClickListener {
        ParseUser.logInInBackground(etAccount.text.toString(), etPassword.text.toString()) { user, e ->
            e?.let {
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
            }?: run{
                gotoMainPage()
            }
        }
    }

    private fun gotoMainPage(){
        startActivity(Intent(applicationContext, MainPageActivity::class.java))
        finish()
    }
}
