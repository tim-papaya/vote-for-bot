import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import java.nio.file.Files
import kotlin.io.path.Path

// 1 - voting
// 2 - calculate votes
// 3 - next question

fun main(args: Array<String>) {
    val token = extractTokenFromFile(args[0])
    val bot = bot {
        this.token = token
        dispatch {
            text {
                bot.sendMessage(ChatId.fromId(message.chat.id), text = text)
            }
        }
    }.startPolling()
}

fun extractTokenFromFile(filePath: String): String =
    Files.readString(Path(filePath))
