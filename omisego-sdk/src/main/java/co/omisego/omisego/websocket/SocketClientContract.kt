package co.omisego.omisego.websocket

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketSend
import co.omisego.omisego.websocket.channel.SocketChannelContract
import com.google.gson.Gson
import okhttp3.WebSocketListener

interface SocketClientContract {
    interface Builder {
        var authenticationToken: String
        var baseURL: String
        var debug: Boolean
        fun build(): SocketClient
    }

    interface Core {
        fun joinChannel(topic: String)
        fun leaveChannel(topic: String)
        fun hasSentAllMessages(): Boolean
        fun cancel()
    }

    interface MessageRef {
        var value: String
    }

    interface Channel {
        fun addChannel(topic: String): Map<String, SocketChannelContract.Channel>
        fun removeChannel(topic: String): Map<String, SocketChannelContract.Channel>
        fun retrieveChannels(): Map<String, SocketChannelContract.Channel>
        fun retrieveWebSocketCallback(): WebSocketListener
    }

    interface PayloadSendParser {
        val gson: Gson
        fun parse(payload: SocketSend): String
    }

    interface Interval {
        fun startInterval()
        fun stopInterval()
    }
}
