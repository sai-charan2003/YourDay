package com.charan.yourday.permission

interface PermissionManager {

    fun hasCalenderPermission() : Boolean
    fun requestCalenderPermission()
}