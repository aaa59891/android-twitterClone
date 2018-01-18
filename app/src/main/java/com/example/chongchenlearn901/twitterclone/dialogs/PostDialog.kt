package com.example.chongchenlearn901.twitterclone.dialogs

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.Window
import com.example.chongchenlearn901.twitterclone.R
import kotlinx.android.synthetic.main.post_dialog.*

/**
 * Created by chongchen on 2018-01-18.
 */

class PostDialog: Dialog{
    val TAG = "PostDialog"
    val activity:Activity

    constructor(activity: Activity): super(activity){
        this.activity = activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.post_dialog)
    }

    fun setClick(post: View.OnClickListener, cancel: View.OnClickListener): PostDialog{
        btnPost.setOnClickListener(post)
        btnCancel.setOnClickListener(cancel)
        return this
    }
}