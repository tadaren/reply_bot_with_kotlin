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
import java.sql.SQLException
import javax.sql.DataSource


@LineMessageHandler
//@RequestMapping("/line_bot")
class WebHookController{
    
    @Value("\${spring.datasource.url:}")
    private val dbUrl: String? = null
    
    @Autowired
    lateinit var dataSource: DataSource
    
    @Bean
    @Throws(SQLException::class)
    fun dataSource(): DataSource{
        return if(dbUrl?.isEmpty() != false){
            HikariDataSource()
        }else{
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
                println(e.message)
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
                println(e.message)
                null
            }
        }
    }

    @EventMapping
    @Throws(Exception::class)
    fun handleTextMessageEvent(event: MessageEvent<TextMessageContent>): ArrayList<Message> {
        println(event)
        val text = event.message.text

        val splitText = text.split(Regex("""(?:　(?:＝?＝|=[=>]?|->)　| (?:＝?＝|=[=>]?|->)|＝?＝|=[=>]?|->)"""))

        if(Regex("""https?://[\w/:%#\\${'$'}&\?\(\)~\.=\+\-]+""").containsMatchIn(splitText[0])){
            return arrayListOf(TextMessage(""))
        }

        return arrayListOf(TextMessage(when(splitText.size){
            1 -> selectFromMapping(splitText[0])?:"None"
            2 -> if(upsertMapping(splitText[0], splitText[1])) "success" else "failure"
            else -> "other"
        }))
    }

    @EventMapping
    fun defaultMessageEvent(event: Event){
        println(event)
    }

}

