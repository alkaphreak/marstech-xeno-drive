package fr.marstech.xenodrive

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class XenoDriveApplication

fun main(args: Array<String>) {
	runApplication<XenoDriveApplication>(*args)
}
