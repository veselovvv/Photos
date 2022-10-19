package com.veselovvv.photos

interface RequestHandler<in T> {
    fun handleRequest(target: T)
}