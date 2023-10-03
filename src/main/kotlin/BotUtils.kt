import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ReplyMarkup
import com.github.kotlintelegrambot.entities.TelegramFile
import java.io.File

fun TextHandlerEnvironment.sendMessage(chatId: Long, text: String) =
    bot.sendMessage(ChatId.fromId(chatId), text)

fun TextHandlerEnvironment.sendMessage(chatId: Long, text: String, replyMarkup: ReplyMarkup) =
    bot.sendMessage(ChatId.fromId(chatId), text, replyMarkup = replyMarkup)
fun TextHandlerEnvironment.sendPhoto(chatId: Long, path: String): Pair<Any?, Exception?> =
    bot.sendPhoto(ChatId.fromId(chatId), TelegramFile.ByFile(File(path)))