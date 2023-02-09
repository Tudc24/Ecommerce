package com.example.ecommerceapp.data;

data class User(
    val firstName: String,
    val lastName: String,
    val email: String,
    var image: String = ""
) {
    constructor() : this("", "", "", "")
}
