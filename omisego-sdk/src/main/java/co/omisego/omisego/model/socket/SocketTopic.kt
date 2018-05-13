package co.omisego.omisego.model.socket

import co.omisego.omisego.websocket.channel.interval.SocketHeartbeat

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

data class SocketTopic(val name: String)

/**
 * Run the lambda when meets the following condition
 *  - The topic is coming from the user (exclude heartbeat, etc.)
 */
inline fun SocketTopic.runIfNotInternalTopic(lambda: SocketTopic.() -> Unit) {
    // Note for myself: need to check == true because socketChannel cannot cast from Boolean? to Boolean (it's var)
    if (this.name != SocketHeartbeat.EVENT_NAME) {
        lambda()
    }
}