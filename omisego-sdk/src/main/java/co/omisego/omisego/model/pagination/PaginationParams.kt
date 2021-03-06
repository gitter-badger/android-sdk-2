package co.omisego.omisego.model.pagination

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 14/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.enums.OMGEnum

enum class SortDirection constructor(override val value: String) : OMGEnum {
    ASCENDING("asc"),
    DESCENDING("desc");
}

sealed class Paginable {
    open class Transaction : Paginable() {

        /**
         * Represents transaction's searchable fields
         */
        enum class SearchableFields constructor(override val value: String) : OMGEnum {
            ID("id"),
            STATUS("status"),
            FROM("from"),
            TO("to"),
            CREATED_AT("created_at"),
            UPDATED_AT("updated_at");

            override fun toString(): String = value
        }

        /**
         * Represents transaction's sortable fields.
         */
        enum class SortableFields constructor(override val value: String) : OMGEnum {
            ID("id"),
            STATUS("status"),
            FROM("from"),
            TO("to"),
            CREATED_AT("created_at"),
            UPDATED_AT("updated_at");

            override fun toString(): String = value
        }

        /**
         * Represents transaction statuses.
         */
        enum class TransactionStatus constructor(override val value: String) : OMGEnum {
            PENDING("pending"),
            CONFIRMED("confirmed"),
            FAILED("failed"),
            UNKNOWN("unknown");

            override fun toString(): String = value
        }
    }
}
