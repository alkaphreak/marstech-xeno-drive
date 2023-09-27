package fr.marstech.xenodrive.conf

import mu.KLogging
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.EnableAsync

@EnableAsync
@Configuration
class ApplicationEventConfImpl : ApplicationEventConf {

    @EventListener
    @Async
    override fun postStart(event: ApplicationReadyEvent) {
        logger.info("Hello")
    }

    companion object : KLogging()
}