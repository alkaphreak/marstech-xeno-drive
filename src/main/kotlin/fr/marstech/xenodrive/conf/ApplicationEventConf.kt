package fr.marstech.xenodrive.conf

import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async

interface ApplicationEventConf {

    @EventListener
    @Async
    fun postStart(event: ApplicationReadyEvent)
}