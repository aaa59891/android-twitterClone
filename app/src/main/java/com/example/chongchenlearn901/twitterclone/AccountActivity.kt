package com.example.chongchenlearn901.twitterclone

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import com.example.chongchenlearn901.twitterclone.adapters.FeedItemAdapter
import com.example.chongchenlearn901.twitterclone.adapters.INTENT_ACCOUNT
import com.example.chongchenlearn901.twitterclone.adapters.getDataAndNotifyDataChange
import com.example.chongchenlearn901.twitterclone.consts.COLLECTION_FOLLOW
import com.example.chongchenlearn901.twitterclone.consts.COLLECTION_POST
import com.example.chongchenlearn901.twitterclone.consts.FIELD_ACCOUNT
import com.example.chongchenlearn901.twitterclone.consts.FIELD_FOLLOW
import com.example.chongchenlearn901.twitterclone.models.FeedItem
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import kotlinx.android.synthetic.main.activity_account.*

class AccountActivity : AppCompatActivity() {

    val username by lazy {
        ParseUser.getCurrentUser().username
    }
    val TAG = "AccountActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        tvAccount.text = intent.getStringExtra(INTENT_ACCOUNT)
        val account = intent.getStringExtra(INTENT_ACCOUNT)

        if(account == username){
            cbFollow.visibility = View.INVISIBLE
            cbFollow.isClickable = false
        }else{
            cbFollow.setOnClickListener(cbFollowClick)
            ParseQuery<ParseObject>(COLLECTION_FOLLOW).whereEqualTo(FIELD_ACCOUNT, username).whereEqualTo(FIELD_FOLLOW, account).getFirstInBackground{
                data, e ->
                e?.let {
                    cbFollow.isChecked = false
                }?: run{
                    cbFollow.isChecked = true
                }
            }
        }
        val feedData = mutableListOf<FeedItem>()
        val adapter = FeedItemAdapter(this, R.layout.feed_content, feedData)
        lvFeed.adapter = adapter

        val query = ParseQuery<ParseObject>(COLLECTION_POST).whereEqualTo(FIELD_ACCOUNT, account)
        getDataAndNotifyDataChange(this, query, adapter, feedData)

    }

    private val cbFollowClick = View.OnClickListener{
        val cb = it as CheckBox
        if(cb.isChecked){
            getParseObj().saveInBackground{
                it?.apply {
                    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                }
            }
        }else{
            ParseQuery<ParseObject>(COLLECTION_FOLLOW)
                    .whereEqualTo(FIELD_ACCOUNT, username)
                    .whereEqualTo(FIELD_FOLLOW, tvAccount.text.toString())
                    .findInBackground{
                        data, e ->
                            e?.apply { Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show() }
                            ?:run{
                                data.forEach { it.deleteInBackground { it?.apply { Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show() } } }
                            }

                    }
        }
    }

    private fun getParseObj(): ParseObject{
        val obj = ParseObject(COLLECTION_FOLLOW)
        obj.put(FIELD_ACCOUNT, username)
        obj.put(FIELD_FOLLOW, tvAccount.text.toString())
        return obj
    }
}
