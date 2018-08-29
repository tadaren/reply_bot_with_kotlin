package tadaren

import com.linecorp.bot.model.event.Event
import com.linecorp.bot.model.event.MessageEvent
import com.linecorp.bot.model.event.message.TextMessageContent
import com.linecorp.bot.model.message.Message
import com.linecorp.bot.model.message.TextMessage
import com.linecorp.bot.spring.boot.annotation.EventMapping
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler


@LineMessageHandler
//@RequestMapping("/line_bot")
class WebHookController{

    @EventMapping
    @Throws(Exception::class)
    fun handleTextMessageEvent(event: MessageEvent<TextMessageContent>): Message {
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

