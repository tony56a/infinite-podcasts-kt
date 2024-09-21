package com.zharguy.infinitepodcast.repos

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

suspend fun <T> dbQuery(db: Database? = null, block: suspend () -> T): T =
    newSuspendedTransaction(Dispatchers.IO, db = db) { block() }