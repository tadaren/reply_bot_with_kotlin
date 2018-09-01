package tadaren

import com.linecorp.bot.client.LineMessagingClient
import com.linecorp.bot.model.ReplyMessage
import com.linecorp.bot.model.event.MessageEvent
import com.linecorp.bot.model.event.message.TextMessageContent
import com.linecorp.bot.model.event.source.GroupSource
import com.linecorp.bot.model.message.Message
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import java.time.Instant
import com.linecorp.bot.model.response.BotApiResponse
import java.util.concurrent.CompletableFuture
import com.linecorp.bot.model.message.TextMessage
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import java.util.*


@RunWith(SpringRunner::class)
@SpringBootTest
class ApplicationTests {
    @Mock
    private lateinit var lineMessagingClient: LineMessagingClient
    @InjectMocks
    lateinit var underTest: WebHookController

    @Test
    fun handledTextMessageEvent(){
        val request = MessageEvent(
                "replyToken",
                GroupSource("groupId", "userId"),
                TextMessageContent("id", "卍"),
                Instant.now()
        )

        `when`(lineMessagingClient.replyMessage(ReplyMessage(
                "replyToken", listOf<Message>(TextMessage("ふとし"))
        ))).thenReturn(CompletableFuture.completedFuture(
                BotApiResponse("ok", Collections.emptyList())
        ))
//        `when`(lineMessagingClient.leaveGroup("groupId"))
//                .thenReturn(CompletableFuture.completedFuture(
//                        BotApiResponse("ok", Collections.emptyList())
//                ))

//        underTest.handleTextMessageEvent(request)

        verify(lineMessagingClient).replyMessage(ReplyMessage("replyToken", listOf<Message>(TextMessage("ふとし"))))

//        verify(lineMessagingClient).leaveGroup("groupId")

    }

}
