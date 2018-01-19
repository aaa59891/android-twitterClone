package com.example.chongchenlearn901.twitterclone

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.chongchenlearn901.twitterclone.adapters.INTENT_ACCOUNT
import com.example.chongchenlearn901.twitterclone.consts.INTENT_SEARCH_USERNAME
import com.parse.ParseUser
import kotlinx.android.synthetic.main.activity_user.*

class UserActivity : AppCompatActivity() {
    val TAG = "UserActivity"
    val usernames = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        val userAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, usernames)
        lvUser.adapter = userAdapter
        lvUser.onItemClickListener = lvUserOnItemClick

        val queryUsername = intent.getStringExtra(INTENT_SEARCH_USERNAME)
        ParseUser.getQuery().whereContains("username", queryUsername).findInBackground{
            users, e ->
            e?.apply { Toast.makeText(this@UserActivity, message, Toast.LENGTH_SHORT).show() }
            ?:run{
                users.mapTo(usernames){it.username}
                userAdapter.notifyDataSetChanged()
            }
        }
    }

    val lvUserOnItemClick = AdapterView.OnItemClickListener{
        _, _, position, _ ->
        val intent = Intent(applicationContext, AccountActivity::class.java)
        intent.putExtra(INTENT_ACCOUNT, usernames[position])
        startActivity(intent)
    }
}
