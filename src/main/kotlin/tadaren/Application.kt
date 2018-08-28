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
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.bind.annotation.RequestMapping
import java.rmi.registry.Registry
import java.sql.SQLException
import javax.sql.DataSource

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

@LineMessageHandler
//@RequestMapping("/line_bot")
class WebHookController{

    @EventMapping
    @Throws(Exception::class)
    fun handleTextMessageEvent(event: MessageEvent<TextMessageContent>): Message{
        println(event)
        val text = event.message.text

        val splitText = text.split(Regex("""(?:　(?:＝?＝|=[=>]?|->)　| (?:＝?＝|=[=>]?|->)|＝?＝|=[=>]?|->)"""))

        if(Regex("""https?://[\w/:%#\\${'$'}&\?\(\)~\.=\+\-]+""").containsMatchIn(splitText[0])){
            return TextMessage("")
        }

        return TextMessage(when(splitText.size){
            1 -> selectFromMapping(splitText[0])?:""
            2 -> if(upsertMapping(splitText[0], splitText[1])) "success" else "failure"
            else -> ""
        })
    }

    @EventMapping
    fun defaultMessageEvent(event: Event){
        println(event)
    }


    @Value("\${spring.datesource.url}")
    private val dbUrl: String? = null

    @Autowired
    private lateinit var dataSource: DataSource

    @Bean
    @Throws(SQLException::class)
    fun dataSource(): DataSource{
        return if (dbUrl?.isEmpty() != false) {
            HikariDataSource()
        } else {
            val config = HikariConfig()
            config.jdbcUrl = dbUrl
            HikariDataSource(config)
        }
    }


    fun upsertMapping(key: String, value: String): Boolean{
        dataSource.connection.use {
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
        dataSource.connection.use {
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

}

