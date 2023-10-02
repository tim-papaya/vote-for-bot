import Mood.*
import State.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.KeyboardReplyMarkup
import com.github.kotlintelegrambot.entities.keyboard.KeyboardButton
import java.io.File
import java.nio.file.Files
import java.util.Collections.synchronizedMap
import kotlin.io.path.Path

val guests: MutableMap<Long, GuestInfo> = synchronizedMap(mutableMapOf())


fun main(args: Array<String>) {
    val mapper = jacksonObjectMapper()
    val questions: Map<String, Question> = mapper.readValue(File(args[1]))

    val token = extractTokenFromFile(args[0])
    bot {
        this.token = token
        dispatch {
            text {
                checkGuestCommand()
                checkAdminCommand(questions)
            }
        }
    }.startPolling()
}

private fun TextHandlerEnvironment.checkGuestCommand() {
    val chatId = message.chat.id

    when (text) {
        "/stop" -> guests.remove(chatId)
    }

    when (guests[chatId]?.state) {
        null -> checkWelcomeCommands(chatId)
        PICKING_CHARACTER -> checkPickingCharacter(chatId, text)
        CHECK_MOOD -> checkMood(chatId, text)
        AT_SWAMP -> TODO()
    }

}

fun TextHandlerEnvironment.checkMood(chatId: Long, text: String) {
    when (val mood = Mood.entries.firstOrNull { it.fullName == text }) {
        null -> sendMessage(
            chatId,
            "Хороший настрой;)\nНа что он больше всего похож из списка?",
            replyMarkup = createMarkupWithMoods()
        )

        EXITING, DREAMY, CHILL -> {
            guests[chatId]?.apply {
                this.mood = mood
                state = AT_SWAMP
            }
            sendMessage(chatId, "Спасибо! Проходите на Болото")
        }
    }
}

fun TextHandlerEnvironment.checkPickingCharacter(chatId: Long, text: String) {
    Character.entries.firstOrNull { it.fullName == text.trim() }?.also {
        guests[chatId]?.apply {
            state = CHECK_MOOD
            character = it
        }
    }?.also {
        sendMessage(
            chatId, listOf(
                "Спасибо за ответ! Скажите какое у вас настроение сегодня?",
                "1) Возбужденное (я хочу беситься/веселиться),",
                "2) Мечтательное (я хочу покреативить),",
                "3) Рассудительное (я хочу почиллить)"
            ).joinToString("\n"),
            replyMarkup = createMarkupWithMoods()
        )
    } ?: sendMessage(
        chatId,
        "Ой, кажется вы выдаете себя за другого, попробуйте еще раз.",
        replyMarkup = createMarkupWithCharacters()
    )
}

private fun createMarkupWithMoods(): KeyboardReplyMarkup =
    Mood.entries.map { listOf(KeyboardButton(it.fullName)) }
        .toList()
        .let { KeyboardReplyMarkup(it) }

private fun TextHandlerEnvironment.checkWelcomeCommands(chatId: Long) {
    when (text) {
        "/start" -> {
            sendMessage(chatId, "Какой вы персонаж?", replyMarkup = createMarkupWithCharacters())
            guests[chatId] = GuestInfo(chatId, PICKING_CHARACTER)
        }
    }
}

private fun createMarkupWithCharacters(): KeyboardReplyMarkup {
    val characters = Character.entries.map { it.fullName }
    return KeyboardReplyMarkup(
        listOf(
            createCharactersLine(characters[0], characters[1], characters[2]),
            createCharactersLine(characters[3], characters[4]),
        )
    )
}

private fun createCharactersLine(vararg chars: String) =
    chars.map { KeyboardButton(it) }.toList()

private fun TextHandlerEnvironment.checkAdminCommand(questions: Map<String, Question>) {
    try {
        val tokens = text.split(" ")
        when (tokens[0]) {
            "#question" -> {
                val question = questions[tokens[1]] ?: throw IllegalArgumentException("Question not found")
                val text = question.text
                guests.forEach {
                    bot.sendMessage(ChatId.fromId(it.key), text = question.text)
                }
            }
        }
    } catch (e: Exception) {
        bot.sendMessage(ChatId.fromId(message.chat.id), e.message ?: "")
    }
}

fun extractTokenFromFile(filePath: String): String =
    Files.readString(Path(filePath))
