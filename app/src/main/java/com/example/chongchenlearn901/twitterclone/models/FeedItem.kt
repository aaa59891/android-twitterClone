package com.example.chongchenlearn901.twitterclone.models

import com.example.chongchenlearn901.twitterclone.consts.FIELD_ACCOUNT
import com.example.chongchenlearn901.twitterclone.consts.FIELD_POST
import com.example.chongchenlearn901.twitterclone.consts.SDF
import com.parse.ParseObject

/**
 * Created by chongchen on 2018-01-18.
 */
data class FeedItem(val account: String, val post:String, val time:String) {
}

fun getFeedItemByParse(parse: ParseObject): FeedItem{
    return FeedItem(parse.getString(FIELD_ACCOUNT), parse.getString(FIELD_POST), SDF.format(parse.createdAt))
}