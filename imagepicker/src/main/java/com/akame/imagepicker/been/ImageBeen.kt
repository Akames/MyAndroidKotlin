package com.akame.imagepicker.been

import java.io.Serializable

data class ImageBeen(
    var path: String,
    var displayName: String,
    var position: Int
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (other == null || this.javaClass != other.javaClass) {
            return false
        }
        if (other is ImageBeen) {
            return path == other.path
        }
        return this === other
    }
}
