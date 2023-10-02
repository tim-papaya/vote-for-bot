import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ReplyMarkup

fun TextHandlerEnvironment.sendMessage(chatId: Long, text: String) =
    bot.sendMessage(ChatId.fromId(chatId), text)

fun TextHandlerEnvironment.sendMessage(chatId: Long, text: String, replyMarkup: ReplyMarkup) =
    bot.sendMessage(ChatId.fromId(chatId), text, replyMarkup = replyMarkup)