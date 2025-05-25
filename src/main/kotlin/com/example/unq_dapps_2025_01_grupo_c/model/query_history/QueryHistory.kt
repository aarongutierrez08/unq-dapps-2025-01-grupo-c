package com.example.unq_dapps_2025_01_grupo_c.model.query_history

import com.example.unq_dapps_2025_01_grupo_c.dto.query_history.QueryHistoryResponse
import com.example.unq_dapps_2025_01_grupo_c.model.user.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "query_history")
data class QueryHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    var term: String = "",

    var date: LocalDateTime = LocalDateTime.now(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User
) {
    fun toResponse(): QueryHistoryResponse {
        return QueryHistoryResponse(
            term = this.term,
            date = this.date,
            userName = this.user.username
        )
    }
}

fun List<QueryHistory>.toResponseList(): List<QueryHistoryResponse> {
    return this.map { it.toResponse() }
}