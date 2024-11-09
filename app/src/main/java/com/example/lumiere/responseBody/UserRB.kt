package com.example.lumiere.responseBody

import com.example.lumiere.Models.User

data class UserRB (
    var message: String ?= null,
    var status: String ?= null,
    var user: User?= null
)