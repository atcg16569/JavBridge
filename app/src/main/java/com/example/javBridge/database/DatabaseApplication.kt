package com.example.javBridge.database

import android.app.Application

class DatabaseApplication : Application() {
    private val bridgeDatabase by lazy { BridgeRoom.getDatabase(this) }
    val bridgeRepository by lazy { BridgeRepository(bridgeDatabase.bridgeDao()) }
    val remoteRepository by lazy { RemoteRepository(bridgeDatabase.bridgeDao()) }
}