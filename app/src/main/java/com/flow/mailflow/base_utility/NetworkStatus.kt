package com.flow.mailflow.base_utility

sealed class NetworkStatus {
    object Available : NetworkStatus()
    object Unavailable : NetworkStatus()
}

sealed class MyState {
    object Fetched : MyState()
    object Error : MyState()
}
