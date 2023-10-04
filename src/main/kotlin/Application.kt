import State.*
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.BotCommand
import java.nio.file.Files
import kotlin.io.path.Path


fun main(args: Array<String>) {
    val token = extractTokenFromFile(args[0])
    val botState = loadBotState(args[1], args[2], args[3], args[4])

    bot {
        this.token = token
        dispatch {
            text {
                if (!checkAdminCommand(botState))
                    checkGuestCommand(botState)
                botState.save()
            }
        }
    }.apply {
        setMyCommands(listOf(BotCommand("start", "Начать приключение!")))
        startPolling()
    }

}

fun TextHandlerEnvironment.checkAdminCommand(botState: BotState): Boolean {
    val adminId = message.chat.id
    val tokens = text.split(" ")
    val command = tokens[0]
    val firstArgument = if (tokens.size > 1) tokens[1] else ""

    if (!botState.admins.contains(adminId)) return false

    when (command) {
        "#q" -> {
            sendQuestionToGuests(botState, firstArgument)
            return true
        }

        "#end" -> {
            val question = botState.getCurrentQuestion()
            if (question == null) {
                sendMessage(adminId, "Вопрос не запущен")
                return true
            }
            if (question.hasRightAnswer()) {
                checkQuestionResultsWithRightAnswer(botState, adminId)
            } else {
                checkQuestionResultsWithoutRightAnswer(botState, adminId, question)
            }
            botState.guests.filter { it.value.state == READY_FOR_FINAL_TEST }
                .forEach { sendMessage(it.key, "Время вышло!") }
            botState.setCurrentQuestion(null)
            return true
        }

        "#stats" -> {
            getStatsForQuestionResultsWithRightAnswer(botState, adminId)
            return true
        }

        "#stats_open" -> {
            getStatsForQuestionResultsWithoutAnswers(botState, adminId)
            return true
        }

        "#moods" -> {
            getMoods(botState, adminId)
            return true
        }
    }
    return false
}


private fun TextHandlerEnvironment.checkGuestCommand(botState: BotState) {
    val chatId = message.chat.id

    when (text) {
        "/stop" -> botState.guests.remove(chatId)
        "I_want_secret_power_here" -> {
            botState.admins.add(chatId)
            sendMessage(chatId, "granted")
        }
    }

    when (botState.guests[chatId]?.state) {
        null -> checkWelcomeCommands(botState, chatId)
        PICKING_CHARACTER -> checkPickingCharacter(botState, chatId, text)
        AT_SWAMP -> checkSwampCommands(botState, chatId, text)
        CHECK_MOOD -> checkMood(botState, chatId, text)
        CHARACTER_RECEIVED -> checkCharacterReceivedCommands(botState, chatId, text)
        READY_FOR_FINAL_TEST -> checkQuestion(botState, chatId, text)
    }

}

private fun extractTokenFromFile(filePath: String): String =
    Files.readString(Path(filePath))
