import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import java.io.File
import java.nio.file.Files
import java.util.Collections.synchronizedList
import kotlin.io.path.Path

// subscribe
// 1 - voting
// 2 - calculate votes
// 3 - next question

val chatIDs: MutableList<Long> = synchronizedList(mutableListOf())

fun main(args: Array<String>) {
    val mapper = jacksonObjectMapper()
    val questions: List<Question> = mapper.readValue(File(args[1]))

    val token = extractTokenFromFile(args[0])
    val bot = bot {
        this.token = token
        dispatch {
            text {
                when (text) {
                    "hello" -> chatIDs.add(message.chat.id)
                    "start" -> chatIDs.forEach { bot.sendMessage(ChatId.fromId(it), text = questions[0].name) }
                }

            }
        }
    }.startPolling()
}

fun extractTokenFromFile(filePath: String): String =
    Files.readString(Path(filePath))
