package com.example.lumiere.responseBody

import com.example.lumiere.Models.Post
import com.example.lumiere.Models.User

data class PostRB (
    var message: String ?= null,
    var status: String ?= null,
    var post: Post?= null
)