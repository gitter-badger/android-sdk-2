package co.omisego.omisego.model

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/6/2017 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

/**
 * Represents the global settings of the provider
 *
 * @param tokens A list of tokens available for the provider
 */
data class Setting(val tokens: List<Token>)
