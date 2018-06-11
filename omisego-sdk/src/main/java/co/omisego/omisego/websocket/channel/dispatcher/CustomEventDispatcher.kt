package co.omisego.omisego.websocket.channel.dispatcher

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 12/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.model.transaction.consumption.TransactionConsumption
import co.omisego.omisego.websocket.SocketChannelCallback
import co.omisego.omisego.websocket.SocketCustomEventCallback
import co.omisego.omisego.websocket.SocketCustomEventCallback.AnyEventCallback
import co.omisego.omisego.websocket.SocketCustomEventCallback.TransactionConsumptionCallback
import co.omisego.omisego.websocket.SocketCustomEventCallback.TransactionRequestCallback
import co.omisego.omisego.websocket.enum.SocketCustomEvent
import co.omisego.omisego.websocket.enum.SocketCustomEvent.OTHER
import co.omisego.omisego.websocket.enum.SocketCustomEvent.TRANSACTION_CONSUMPTION_FINALIZED
import co.omisego.omisego.websocket.enum.SocketCustomEvent.TRANSACTION_CONSUMPTION_REQUEST

/**
 * A callback for dispatch the [SocketCustomEventCallback] events.
 */
class CustomEventDispatcher : SocketDispatcherContract.CustomEventDispatcher {
    /**
     * For dispatching the [SocketCustomEventCallback] event.
     */
    override var socketCustomEventCallback: SocketCustomEventCallback? = null

    /**
     * The web socket replied object from eWallet API.
     */
    override var socketReceive: SocketReceive? = null

    /**
     * For dispatching the [SocketChannelCallback] event.
     */
    override var socketChannelCallback: SocketChannelCallback? = null

    /**
     * Handles the [SocketCustomEvent] and dispatch the [SocketCustomEventCallback] to the client.
     *
     * @param customEvent To indicate the actual type of generic [SocketCustomEvent]
     */
    override fun handleEvent(customEvent: SocketCustomEvent) {
        val response = socketReceive ?: return
        val callback = socketCustomEventCallback ?: return

        when (callback) {
            is TransactionRequestCallback ->
                callback.handleTransactionRequestEvent(response, customEvent)
            is TransactionConsumptionCallback ->
                callback.handleTransactionConsumptionEvent(response, customEvent)
            is AnyEventCallback ->
                callback.handleAnyEvent(response)
        }
    }

    /**
     * Handles the [SocketCustomEvent] event and dispatch the [SocketCustomEventCallback.TransactionRequestCallback].
     * This method will be invoked by the [handleEvent] method.
     * Any event excepts [TRANSACTION_CONSUMPTION_REQUEST] or [TRANSACTION_CONSUMPTION_FINALIZED] is [OTHER], managed by the [EitherEnumDeserializer].
     *
     * @param socketReceive The web socket replied object from eWallet API.
     * @param customEvent The custom event used for decide the event to be dispatched
     */
    override fun SocketCustomEventCallback.TransactionRequestCallback.handleTransactionRequestEvent(
        socketReceive: SocketReceive,
        customEvent: SocketCustomEvent
    ) {
        when (customEvent) {
            TRANSACTION_CONSUMPTION_REQUEST -> {
                if (socketReceive.data is TransactionConsumption) {
                    onTransactionConsumptionRequest(socketReceive.data)
                }
            }
            TRANSACTION_CONSUMPTION_FINALIZED -> {
                val judge = socketReceive.judgeCustomEventCallback(
                    this::onTransactionConsumptionFinalizedFail,
                    this::onTransactionConsumptionFinalizedSuccess
                )

                if (!judge && socketReceive.error != null) {
                    socketChannelCallback?.onError(socketReceive.error)
                }
            }
            OTHER -> {
                if (socketReceive.error != null) {
                    socketChannelCallback?.onError(socketReceive.error)
                }
            }
        }
    }

    /**
     * Handles the [SocketCustomEvent] event and dispatch the [SocketCustomEventCallback.TransactionConsumptionCallback].
     * This method will be invoked by the [handleEvent] method.
     *
     * @param socketReceive The web socket replied object from eWallet API.
     * @param customEvent The custom event used for decide the event to be dispatched
     */
    override fun SocketCustomEventCallback.TransactionConsumptionCallback.handleTransactionConsumptionEvent(
        socketReceive: SocketReceive,
        customEvent: SocketCustomEvent
    ) {
        if (customEvent == TRANSACTION_CONSUMPTION_FINALIZED) {
            val judge = socketReceive.judgeCustomEventCallback(
                this::onTransactionConsumptionFinalizedFail,
                this::onTransactionConsumptionFinalizedSuccess
            )

            if (!judge && socketReceive.error != null) {
                socketChannelCallback?.onError(socketReceive.error)
            }
        } else if (customEvent == OTHER && socketReceive.error != null) {
            socketChannelCallback?.onError(socketReceive.error)
        }
    }

    /**
     * Handles the [SocketCustomEvent] event and dispatch the [SocketCustomEventCallback.AnyEventCallback].
     * This method will be invoked by the [handleEvent] method.
     *
     * @param socketReceive The web socket replied object from eWallet API.
     */
    override fun SocketCustomEventCallback.AnyEventCallback.handleAnyEvent(
        socketReceive: SocketReceive
    ) = onEventReceived(socketReceive)

    internal inline fun <reified T : SocketReceive.SocketData> SocketReceive.judgeCustomEventCallback(
        matchedTypeWithErrorLambda: (T, APIError) -> Unit,
        matchedTypeLambda: (T) -> Unit
    ): Boolean {
        when {
            this.data is T && this.error != null -> matchedTypeWithErrorLambda(this.data, this.error)
            this.data is T -> matchedTypeLambda(this.data)
            else -> return false
        }
        return true
    }
}
