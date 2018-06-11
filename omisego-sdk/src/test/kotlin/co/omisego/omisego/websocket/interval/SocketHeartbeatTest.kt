package co.omisego.omisego.websocket.interval

import co.omisego.omisego.model.socket.SocketSend
import co.omisego.omisego.websocket.channel.SocketChannelContract
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.timeout
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.amshove.kluent.any
import org.amshove.kluent.mock
import org.junit.Before
import org.junit.Test
import kotlin.concurrent.thread

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 27/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

class SocketHeartbeatTest {

    private val mockMessageRef: SocketChannelContract.MessageRef = mock()
    private lateinit var socketHeartbeat: SocketHeartbeat

    @Before
    fun setup() {
        socketHeartbeat = spy(SocketHeartbeat(mockMessageRef).apply { period = 5000L })
    }

    @Test
    fun `startInterval should be a thread-safe function`() {
        val allThreads = mutableListOf<Thread>()
        val task = mock<(SocketSend) -> Unit>()
        for (i in 1..100) {
            val t = thread {
                socketHeartbeat.startInterval(task)
            }
            allThreads.add(t)
        }

        // Wait all threads finish their worked.
        for (i in 0..99) {
            allThreads[i].join()
        }

        /**
         * Ensure the following situation will not happen.
         *
         * timer?.cancel() <-- Thread B (execute before thread A)
         * timer = Timer()
         * timer = timer?.schedule(whatever) <-- Thread A (thread A will throw IllegalStateException, so task() won't be invoked)
         *
         * Because of that, this expression will verify that all tasks should be invoked.
         */
        timeout(3000)
        verify(task, times(100)).invoke(any())
    }
}
