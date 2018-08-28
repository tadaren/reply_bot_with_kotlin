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

}

