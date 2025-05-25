package com.example.unq_dapps_2025_01_grupo_c.security

import com.example.unq_dapps_2025_01_grupo_c.exceptions.UserNotFoundException
import com.example.unq_dapps_2025_01_grupo_c.model.query_history.QueryHistory
import com.example.unq_dapps_2025_01_grupo_c.repository.QueryHistoryRepository
import com.example.unq_dapps_2025_01_grupo_c.repository.UserRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.transaction.support.TransactionTemplate
import org.springframework.web.filter.OncePerRequestFilter

@Component
class QueryLoggingFilter(
    private val userRepository: UserRepository,
    private val queryHistoryRepository: QueryHistoryRepository,
    private val transactionTemplate: TransactionTemplate
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val auth = SecurityContextHolder.getContext().authentication
        if (auth != null && auth.isAuthenticated && auth.name != "anonymousUser") {
            transactionTemplate.executeWithoutResult {
                val user = userRepository.findByUsername(auth.name)
                    .orElseThrow { UserNotFoundException(auth.name) }

                val queryParams = request.queryString?.let { "?$it" } ?: ""
                val queryHistory = QueryHistory(
                    term = "${request.method} ${request.requestURI}$queryParams",
                    user = user
                )
                queryHistoryRepository.save(queryHistory)
            }
        }

        filterChain.doFilter(request, response)
    }
}