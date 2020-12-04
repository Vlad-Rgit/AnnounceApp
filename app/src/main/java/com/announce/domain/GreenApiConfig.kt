package com.announce.domain

data class GreenApiConfig(
    var apiTokenInstance: String,
    var idInstance: String
) {
    constructor(): this("", "")
}