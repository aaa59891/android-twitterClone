package com.example.chongchenlearn901.twitterclone.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.example.chongchenlearn901.twitterclone.R
import com.example.chongchenlearn901.twitterclone.models.FeedItem
import com.example.chongchenlearn901.twitterclone.models.getFeedItemByParse
import com.parse.ParseObject
import com.parse.ParseQuery

/**
 * Created by chongchen on 2018-01-18.
 */
val INTENT_ACCOUNT = "account"
class FeedItemAdapter : ArrayAdapter<FeedItem> {
    val TAG = "FeedItemAdapter"
    val cont: Context
    val resource: Int
    var objects: List<FeedItem>

    constructor(context: Context, resource: Int, objects: List<FeedItem>) : super(context, resource, objects) {
        this.cont = context
        this.resource = resource
        this.objects = objects
    }

    override fun getCount(): Int {
        return this.objects.size
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var viewHolder: ViewHolder? = null
        var view: View? = convertView
        convertView?.let{
            viewHolder = convertView.tag as ViewHolder
        }?: run{
            view = LayoutInflater.from(this.cont).inflate(this.resource, parent, false)
            view?.apply {
                viewHolder = ViewHolder(findViewById(R.id.tvAccount), findViewById(R.id.tvPost), findViewById(R.id.tvTime))
                tag = viewHolder
            }
        }
        val feedItem = this.objects[position]

        viewHolder?.apply {
            tvAccount.text = feedItem.account
            tvPost.text = feedItem.post
            tvTime.text = feedItem.time
        }

        return view!!
    }

    private class ViewHolder(val tvAccount: TextView, val tvPost: TextView, val tvTime: TextView)

}

fun getDataAndNotifyDataChange(context: Context, query: ParseQuery<ParseObject>, adapter: FeedItemAdapter, data: MutableList<FeedItem>){
    query.orderByDescending("createdAt").findInBackground{
        items, e ->
        e?.apply { Toast.makeText(context, message, Toast.LENGTH_SHORT).show() }
        ?: run{
            items.mapTo(data){ getFeedItemByParse(it) }
            adapter.notifyDataSetChanged()
        }
    }
}