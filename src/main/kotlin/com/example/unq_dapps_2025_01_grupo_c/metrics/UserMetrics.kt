package com.example.unq_dapps_2025_01_grupo_c.metrics

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.stereotype.Component

@Component
class UserMetrics(private val meterRegistry: MeterRegistry) {
    val usersCreatedCounter: Counter = Counter.builder("app_users_created_total")
        .description("Total number of users created")
        .register(meterRegistry)

    fun incrementUsersCreated() {
        usersCreatedCounter.increment()
    }
}