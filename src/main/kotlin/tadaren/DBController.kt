package tadaren

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.stereotype.Controller
import java.sql.SQLException
import javax.sql.DataSource

@Controller
object DBController{

    @Value("\${spring.datasource.url}")
    private val dbUrl: String? = null

    @Autowired
    lateinit var dataSource: DataSource

    @Bean
    @Throws(SQLException::class)
    fun dataSource(): DataSource {
        return if (dbUrl?.isEmpty() != false) {
            HikariDataSource()
        } else {
            val config = HikariConfig()
            config.jdbcUrl = dbUrl
            HikariDataSource(config)
        }
    }

}


fun upsertMapping(key: String, value: String): Boolean{
    DBController.dataSource.connection.use {
        return try {
            val statement = it.createStatement()
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS reply_map(key TEXT NOT NULL UNIQUE, value TEXT NOT NULL)")
            val preStatement = it.prepareStatement("INSERT INTO reply_map VALUES (?, ?) ON CONFLICT ON CONSTRAINT reply_map_key_key DO UPDATE SET value=?")
            preStatement.setString(1, key)
            preStatement.setString(2, value)
            preStatement.setString(3, value)
            preStatement.execute()
        } catch(e: SQLException) {
            false
        }
    }
}

fun selectFromMapping(key: String): String?{
    DBController.dataSource.connection.use {
        return try {
            val preStatement = it.prepareStatement("SELECT * FROM reply_map WHERE key=? ")
            preStatement.setString(1, key)
            val s = preStatement.executeQuery()
            s.getString(1)
        }catch(e: SQLException){
            null
        }
    }
}