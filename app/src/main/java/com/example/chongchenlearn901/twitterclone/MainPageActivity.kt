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
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.chongchenlearn901.twitterclone.adapters.FeedItemAdapter
import com.example.chongchenlearn901.twitterclone.adapters.INTENT_ACCOUNT
import com.example.chongchenlearn901.twitterclone.adapters.getDataAndNotifyDataChange
import com.example.chongchenlearn901.twitterclone.consts.COLLECTION_FOLLOW
import com.example.chongchenlearn901.twitterclone.consts.COLLECTION_POST
import com.example.chongchenlearn901.twitterclone.consts.FIELD_ACCOUNT
import com.example.chongchenlearn901.twitterclone.consts.FIELD_FOLLOW
import com.example.chongchenlearn901.twitterclone.dialogs.PostDialog
import com.example.chongchenlearn901.twitterclone.models.FeedItem
import com.example.chongchenlearn901.twitterclone.models.getFeedItemByParse
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import kotlinx.android.synthetic.main.activity_main_page.*

class MainPageActivity : AppCompatActivity() {

    val mainFeedItems = mutableListOf<FeedItem>()
    lateinit var adapter: FeedItemAdapter

    val username by lazy {
        ParseUser.getCurrentUser().username
    }
    val TAG = "MainPageActivity"
    val dialog by lazy {
        PostDialog(this@MainPageActivity)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)
        adapter = FeedItemAdapter(this, R.layout.feed_content, mainFeedItems)

        ParseQuery<ParseObject>(COLLECTION_FOLLOW).whereEqualTo(FIELD_ACCOUNT, username).findInBackground{
            follows, e ->
                e?.apply { Toast.makeText(this@MainPageActivity, message, Toast.LENGTH_SHORT).show() }
                ?:run{
                    val list = follows.mapTo(mutableListOf(username)){it.getString(FIELD_FOLLOW)}
                    val query = ParseQuery<ParseObject>(COLLECTION_POST).whereContainsAll(FIELD_ACCOUNT, list)
                    getDataAndNotifyDataChange(this, query, adapter, mainFeedItems)
                }
        }

        lvFeeds.adapter = adapter
        lvFeeds.setOnItemClickListener { parent, view, position, id ->
            val tvAccount = view.findViewById<TextView>(R.id.tvAccount)
            val intent = Intent(this, AccountActivity::class.java)
            intent.putExtra(INTENT_ACCOUNT, tvAccount.text.toString())
            startActivity(intent)
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

    private fun showPostDialog(){
        val window = dialog.window
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        dialog.setClick(postClick, cancelClick)
        window.setLayout((resources.displayMetrics.widthPixels * 0.9f).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
    }

    val postClick = View.OnClickListener {
        val obj = ParseObject("post")
        val edPost = dialog.findViewById<EditText>(R.id.edPost)
        obj.put("account", ParseUser.getCurrentUser().username)
        obj.put("post", edPost.text.toString())
        obj.saveInBackground{
            it?.apply {
                Toast.makeText(this@MainPageActivity, message, Toast.LENGTH_SHORT).show()
            }?:run{
                Toast.makeText(this@MainPageActivity, "Post successfully", Toast.LENGTH_SHORT).show()
            }
            mainFeedItems.add(0, getFeedItemByParse(obj))
            this.adapter.notifyDataSetChanged()
            dialog.dismiss()
            edPost.setText("")
        }
    }

    val cancelClick = View.OnClickListener{
        dialog.dismiss()
    }
}
