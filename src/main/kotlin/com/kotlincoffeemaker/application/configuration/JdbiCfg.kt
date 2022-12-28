/*
package com.kotlincoffeemaker.application.configuration

import com.gitlab.mvysny.jdbiorm.JdbiOrm
import org.jdbi.v3.core.statement.Slf4JSqlLogger
import org.springframework.stereotype.Service
import javax.annotation.PreDestroy
import javax.sql.DataSource

@Service
class JdbiCfg(val ds: DataSource) {
    init {
        JdbiOrm.setDataSource(ds)
        JdbiOrm.jdbi().installPlugins()
        JdbiOrm.jdbi().setSqlLogger(Slf4JSqlLogger())
    }

    @PreDestroy
    fun stop() {
        JdbiOrm.destroy()
    }
}
*/
