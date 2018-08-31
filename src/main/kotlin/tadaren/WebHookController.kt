package tadaren

import com.linecorp.bot.model.event.Event
import com.linecorp.bot.model.event.MessageEvent
import com.linecorp.bot.model.event.message.TextMessageContent
import com.linecorp.bot.model.message.Message
import com.linecorp.bot.model.message.TextMessage
import com.linecorp.bot.spring.boot.annotation.EventMapping
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Controller
import java.sql.SQLException
import javax.sql.DataSource


@Controller
@LineMessageHandler
class WebHookController{
//
//    @Value("\${spring.datasource.url:}")
//    private val dbUrl: String? = null
//
//    @Autowired
//    lateinit var dataSource: DataSource
//
//    @Bean
//    @Throws(SQLException::class)
//    fun dataSource(): DataSource{
//        return if(dbUrl?.isEmpty() != false){
//            HikariDataSource()
//        }else{
//            val config = HikariConfig()
//            config.jdbcUrl = dbUrl
//            HikariDataSource(config)
//        }
//    }
//
//    fun upsertMapping(key: String, value: String): Boolean{
//        dataSource.connection.use {
//            return try {
//                val statement = it.createStatement()
//                statement.executeUpdate("CREATE TABLE IF NOT EXISTS reply_map(key TEXT NOT NULL UNIQUE, value TEXT NOT NULL)")
//                val preStatement = it.prepareStatement("INSERT INTO reply_map VALUES (?, ?) ON CONFLICT ON CONSTRAINT reply_map_key_key DO UPDATE SET value=?")
//                preStatement.setString(1, key)
//                preStatement.setString(2, value)
//                preStatement.setString(3, value)
//                preStatement.execute()
//                true
//            }catch(e: SQLException) {
//                println(e.message)
//                false
//            }
//        }
//    }
//
//    fun selectFromMapping(key: String): String?{
//        dataSource.connection.use {
//            return try {
//                val preStatement = it.prepareStatement("SELECT value FROM reply_map WHERE key=? ")
//                preStatement.setString(1, key)
//                val s = preStatement.executeQuery()
//                println("Result: $s")
//                if(s.next())
//                    s.getString(1)
//                else
//                    null
//            }catch(e: SQLException){
//                println("SQLException: ${e.message}")
//                null
//            }
//        }
//    }
//
//    fun deleteMapping(key: String): Boolean{
//        dataSource.connection.use{
//            return try{
//                val preStatement = it.prepareStatement("DELETE FROM reply_map WHERE key=?")
//                preStatement.setString(1, key)
//                preStatement.execute()
//            }catch(e: SQLException){
//                println(e)
//                false
//            }
//        }
//    }

//    @EventMapping
//    @Throws(Exception::class)
//    fun handleTextMessageEvent(event: MessageEvent<TextMessageContent>): Message? {
//        println(event)
//        val text = event.message.text
//
//        val splitText = text.split(Regex("(?:　(?:＝?＝|=[=>]?|->)　| (?:＝?＝|=[=>]?|->)|＝?＝|=[=>]?|->)"))
//
//        if(Regex("""https?://[\w/:%#\\${'$'}&\?\(\)~\.=\+\-]+""").containsMatchIn(splitText[0])){
//            return null
//        }
//
//        return when(splitText.size){
//            1 -> selectFromMapping(splitText[0])?.let { TextMessage(it) }
//            2 -> TextMessage(if(upsertMapping(splitText[0], splitText[1])) "success" else "failure")
//            else -> null
//        }
//    }

    @EventMapping
    fun defaultMessageEvent(event: Event){
        println(event)
    }

}

