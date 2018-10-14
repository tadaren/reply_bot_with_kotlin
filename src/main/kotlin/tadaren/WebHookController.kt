package tadaren

import com.linecorp.bot.model.event.Event
import com.linecorp.bot.model.event.MessageEvent
import com.linecorp.bot.model.event.message.TextMessageContent
import com.linecorp.bot.model.message.Message
import com.linecorp.bot.model.message.TextMessage
import com.linecorp.bot.spring.boot.annotation.EventMapping
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component
@LineMessageHandler
class WebHookController{

    @Autowired
    lateinit var mappingRepository: MappingRepository


    @EventMapping
    @Throws(Exception::class)
    fun handleTextMessageEvent(event: MessageEvent<TextMessageContent>): Message? {
        println(event)
        val text = event.message.text

        // special pattern
        when(text){
            "list", "リスト" -> return TextMessage(mappingRepository.findAll().joinToString(separator = "\n") { it.key })
        }

        val splitText = text.split(Regex("(?:　(?:＝?＝|=[=>]?|->)　| (?:＝?＝|=[=>]?|->)|＝?＝|=[=>]?|->)"))

        // exclude URL pattern
        if(Regex("""https?://[\w/:%#\\${'$'}&?()~.=+\-]+""").containsMatchIn(splitText[0])){
            return null
        }

        // default mapping reply
        return when(splitText.size){
            1 -> mappingRepository.findByKey(splitText[0])?.value?.let { TextMessage(it) }
            2 -> {
                mappingRepository.save(Mapping(splitText[0], splitText[1]))
                TextMessage("success")
            }
            else -> null
        }
    }

    @EventMapping
    fun defaultMessageEvent(event: Event){
        println(event)
    }

}

