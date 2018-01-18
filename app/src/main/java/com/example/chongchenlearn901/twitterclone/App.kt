package com.example.chongchenlearn901.twitterclone

import android.app.Application
import com.parse.Parse

/**
 * Created by chongchen on 2018-01-18.
 */
class App: Application(){
    override fun onCreate() {
        super.onCreate()
        Parse.initialize(this)
    }
}