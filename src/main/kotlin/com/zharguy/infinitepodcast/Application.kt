package com.zharguy.infinitepodcast

import io.micronaut.context.ApplicationContext
import io.micronaut.runtime.Micronaut.run
import org.flywaydb.core.Flyway

fun main(args: Array<String>) {

	val context = ApplicationContext.run()
	val flyway = context.getBean(Flyway::class.java)
	// Run migrations
	flyway.migrate()

	run(*args)
}

