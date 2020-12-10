package com.announce.common.domain

data class GreenApiConfig(
    var apiTokenInstance: String,
    var idInstance: String
) {
    constructor(): this("", "")
}