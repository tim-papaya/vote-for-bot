import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import com.github.kotlintelegrambot.entities.ChatId
import java.time.LocalTime
import kotlin.concurrent.timer

fun TextHandlerEnvironment.launchTimer(
    chatId: Long,
    timerMessageId: Long,
    timeLimit: Int,
    startTime: Int,
    text: String
) {
    timer(period = 1000) {
        val remainingTime = prepareTimerTime(timeLimit, startTime)
        if (remainingTime <= 0) {
            bot.deleteMessage(ChatId.fromId(chatId), timerMessageId)
            this.cancel()
        } else {
            bot.editMessageText(
                ChatId.fromId(chatId),
                timerMessageId,
                text = "$text $remainingTime"
            )
        }
    }
}

private fun prepareTimerTime(timeLimit: Int, startTime: Int) =
    (timeLimit - (LocalTime.now().toSecondOfDay() - startTime)).takeIf { it > 0 } ?: 0