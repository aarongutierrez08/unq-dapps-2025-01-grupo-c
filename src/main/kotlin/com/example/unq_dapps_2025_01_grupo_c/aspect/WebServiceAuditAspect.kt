package com.example.unq_dapps_2025_01_grupo_c.aspect

import org.aspectj.lang.annotation.*
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Aspect
@Component
class WebServiceAuditAspect {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    fun logWebServiceAudit(joinPoint: org.aspectj.lang.ProceedingJoinPoint): Any? {
        val timestamp = System.currentTimeMillis()
        val user = SecurityContextHolder.getContext().authentication?.name ?: "anonymous"
        val method = (joinPoint.signature as MethodSignature).method
        val operation = method.name
        val params = joinPoint.args.joinToString(", ") { it?.toString() ?: "null" }

        val start = System.nanoTime()
        try {
            val result = joinPoint.proceed()
            val elapsedMs = (System.nanoTime() - start) / 1_000_000
            logger.info(
                "[AUDIT] timestamp={}, user={}, operation={}, params={}, execTimeMs={}",
                timestamp, user, operation, params, elapsedMs
            )
            return result
        } catch (ex: Throwable) {
            val elapsedMs = (System.nanoTime() - start) / 1_000_000
            logger.error(
                "[AUDIT] ERROR timestamp={}, user={}, operation={}, params={}, execTimeMs={}, error={}",
                timestamp, user, operation, params, elapsedMs, ex.toString()
            )
            throw ex
        }
    }
}
