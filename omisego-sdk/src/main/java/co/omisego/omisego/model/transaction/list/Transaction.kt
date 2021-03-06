package co.omisego.omisego.model.transaction.list

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 14/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.Token
import co.omisego.omisego.model.pagination.Paginable
import java.math.BigDecimal
import java.util.Date

data class TransactionExchange(val rate: Double)

data class TransactionSource(
    val address: String,
    val amount: BigDecimal,
    val token: Token
)

data class Transaction(
    val id: String,
    val status: TransactionStatus,
    val from: TransactionSource,
    val to: TransactionSource,
    val exchange: TransactionExchange,
    val metadata: Map<String, Any>,
    val encryptedMetadata: Map<String, Any>,
    val createdAt: Date
) : Paginable.Transaction()
