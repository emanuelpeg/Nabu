package org.emanuelpeg.nabu

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class NabuApplication

fun main(args: Array<String>) {
	runApplication<NabuApplication>(*args)
}
