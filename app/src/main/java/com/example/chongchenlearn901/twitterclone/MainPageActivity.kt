package com.example.chongchenlearn901.twitterclone

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.example.chongchenlearn901.twitterclone.adapters.FeedItemAdapter
import com.example.chongchenlearn901.twitterclone.adapters.INTENT_ACCOUNT
import com.example.chongchenlearn901.twitterclone.adapters.getDataAndNotifyDataChange
import com.example.chongchenlearn901.twitterclone.consts.*
import com.example.chongchenlearn901.twitterclone.dialogs.PostDialog
import com.example.chongchenlearn901.twitterclone.models.FeedItem
import com.example.chongchenlearn901.twitterclone.models.getFeedItemByParse
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import kotlinx.android.synthetic.main.activity_main_page.*

class MainPageActivity : AppCompatActivity() {

    val mainFeedItems = mutableListOf<FeedItem>()
    lateinit var feedItemAdapter: FeedItemAdapter

    val username by lazy {
        ParseUser.getCurrentUser().username
    }
    val TAG = "MainPageActivity"
    val dialog by lazy {
        PostDialog(this@MainPageActivity)
    }
    val accounts = mutableListOf<String>()
    private lateinit var autoCompleteAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)
        feedItemAdapter = FeedItemAdapter(this, R.layout.feed_content, mainFeedItems)
        autoCompleteAdapter = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, accounts)
        edAccountSearch.setAdapter(autoCompleteAdapter)
        edAccountSearch.threshold = 1
        lvFeeds.adapter = feedItemAdapter
        lvFeeds.onItemClickListener = lvOnItemClick
        btnSearch.setOnClickListener(searchClick)

        setAutocompleteData()
    }

    private fun setAutocompleteData() {
        ParseUser.getQuery().findInBackground{
            users, e ->
            e?.apply { Toast.makeText(this@MainPageActivity, message, Toast.LENGTH_SHORT).show() }
                    ?:run{
                users.mapTo( accounts){it.username}
                autoCompleteAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setFeedItems()
    }

    private fun setFeedItems() {
        ParseQuery<ParseObject>(COLLECTION_FOLLOW).whereEqualTo(FIELD_ACCOUNT, username).findInBackground{
            follows, e ->
            e?.apply { Toast.makeText(this@MainPageActivity, message, Toast.LENGTH_SHORT).show() }
                    ?:run{
                mainFeedItems.clear()
                val list = follows.mapTo(mutableListOf(username)){it.getString(FIELD_FOLLOW)}
                val query = ParseQuery<ParseObject>(COLLECTION_POST).whereContainedIn(FIELD_ACCOUNT, list)
                getDataAndNotifyDataChange(this, query, feedItemAdapter, mainFeedItems)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_page_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.apply {
            when(itemId){
                R.id.menuLogout -> gotoLoginPage()
                R.id.menuPost -> showPostDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun gotoLoginPage(){
        ParseUser.logOut()
        startActivity(Intent(applicationContext, MainActivity::class.java))
        finish()
    }

    private val lvOnItemClick = AdapterView.OnItemClickListener{
        _, view, _, _ ->
        val tvAccount = view.findViewById<TextView>(R.id.tvAccount)
        val intent = Intent(this, AccountActivity::class.java)
        intent.putExtra(INTENT_ACCOUNT, tvAccount.text.toString())
        startActivity(intent)
    }

    private fun showPostDialog(){
        val window = dialog.window
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        dialog.setClick(postClick, cancelClick)
        window.setLayout((resources.displayMetrics.widthPixels * 0.9f).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
    }

    private val postClick = View.OnClickListener {
        val obj = ParseObject(COLLECTION_POST)
        val edPost = dialog.findViewById<EditText>(R.id.edPost)
        obj.put(FIELD_ACCOUNT, ParseUser.getCurrentUser().username)
        obj.put(FIELD_POST, edPost.text.toString())
        obj.saveInBackground{
            it?.apply {
                Toast.makeText(this@MainPageActivity, message, Toast.LENGTH_SHORT).show()
            }?:run{
                Toast.makeText(this@MainPageActivity, "Post successfully", Toast.LENGTH_SHORT).show()
            }
            mainFeedItems.add(0, getFeedItemByParse(obj))
            this.feedItemAdapter.notifyDataSetChanged()
            dialog.dismiss()
            edPost.setText("")
        }
    }

    private val cancelClick = View.OnClickListener{
        dialog.dismiss()
    }

    private val searchClick = View.OnClickListener{
        val intent = Intent(this, UserActivity::class.java)
        intent.putExtra(INTENT_SEARCH_USERNAME, edAccountSearch.text.toString())
        startActivity(intent)
    }
}
