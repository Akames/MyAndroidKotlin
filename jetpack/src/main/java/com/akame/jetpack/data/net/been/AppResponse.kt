package com.akame.jetpack.data.net.been

data class ServerResult<out T>(val status: Int, val data: T)