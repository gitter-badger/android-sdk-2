package co.omisego.omisego.websocket.interval

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketSend
import co.omisego.omisego.websocket.SocketClientContract
import co.omisego.omisego.websocket.channel.SocketChannelContract
import co.omisego.omisego.websocket.channel.SocketMessageRef
import co.omisego.omisego.websocket.enum.SocketEventSend
import java.util.Date
import java.util.Timer
import kotlin.concurrent.schedule

class SocketHeartbeat(
    /**
     * [SocketMessageRef] is responsible for creating a unique ref value to be sent with the [SocketSend].
     */
    override val socketMessageRef: SocketChannelContract.MessageRef
) : SocketClientContract.SocketInterval {

    /**
     * The timer for scheduling the [SocketSend] periodically to be sent to the server.
     */
    override var timer: Timer? = null

    /**
     * An interval of milliseconds between the end of the previous task and the start of the next one.
     */
    override var period: Long = 5000

    /**
     * Start to schedule the [SocketSend] to be sent to the server periodically.
     *
     * @param task A lambda with a [SocketSend] parameter. This will be executed periodically that starts immediately.
     */
    @Suppress("OVERRIDE_BY_INLINE")
    @Synchronized
    override inline fun startInterval(crossinline task: (SocketSend) -> Unit) {
        synchronized(this) {
            timer?.cancel()
            timer = Timer()
            timer?.schedule(Date(), period, {
                task(SocketSend(TOPIC, SocketEventSend.HEARTBEAT, socketMessageRef.value, mapOf()))
            })
        }
    }

    /**
     * Stop to schedule the task to be sent to the server.
     */
    @Synchronized
    override fun stopInterval() {
        synchronized(this) {
            timer?.cancel()
            timer = null
        }
    }

    companion object {
        const val TOPIC = "phoenix"
    }
}
