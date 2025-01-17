package org.baylist.db.repo

import org.baylist.db.entity.History
import org.springframework.data.jpa.repository.JpaRepository

interface HistoryRepository : JpaRepository<History, Long> {
}