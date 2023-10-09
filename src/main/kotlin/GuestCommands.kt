import Mood.*
import State.*
import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import com.github.kotlintelegrambot.entities.ReplyKeyboardRemove
import java.nio.file.Files
import java.nio.file.Path

fun TextHandlerEnvironment.checkQuestion(botState: BotState, chatId: Long, text: String) {
    val currentQuestion = botState.getCurrentQuestion()
    if (currentQuestion == null) {
        sendMessage(chatId, "Вопроса еще нет...", ReplyKeyboardRemove())
        return
    }

    val guestInfo = botState.guests[chatId]!!

    if (currentQuestion.hasAnswers())
        checkQuestionWithAnswer(currentQuestion, text, guestInfo, chatId)
    else
        checkQuestionWithoutAnswer(currentQuestion, text, guestInfo, chatId)
}

private fun TextHandlerEnvironment.checkQuestionWithAnswer(
    currentQuestion: Question,
    text: String,
    guestInfo: GuestInfo,
    chatId: Long
) {
    if (currentQuestion.answers.contains(text)) {
        guestInfo.answers[currentQuestion.id] = Answer(currentQuestion.id, currentQuestion.isRightAnswer(text), text)
        sendMessage(chatId, "Ответ засчитан!", ReplyKeyboardRemove())
    } else {
        sendMessage(chatId, "Такого ответа нет")
        return
    }
}

private fun TextHandlerEnvironment.checkQuestionWithoutAnswer(
    currentQuestion: Question,
    text: String,
    guestInfo: GuestInfo,
    chatId: Long
) {
    guestInfo.answers[currentQuestion.id] = Answer(currentQuestion.id, false, text)
    sendMessage(chatId, "Ответ засчитан!", ReplyKeyboardRemove())
}

fun TextHandlerEnvironment.checkWelcomeCommands(botState: BotState, chatId: Long) {
    when (text) {
        "/start" -> {
            sendMessage(chatId, "Какой вы персонаж?", createMarkupWithCharacters())
            botState.guests[chatId] = GuestInfo(chatId, PICKING_CHARACTER)
        }
    }
}

fun TextHandlerEnvironment.checkPickingCharacter(botState: BotState, chatId: Long, text: String) {
    Character.entries.firstOrNull { it.fullName == text.trim() }
        .takeIf { notDuplicate(botState, text) }?.also {
            botState.guests[chatId]?.apply {
                state = AT_SWAMP
                character = it
            }
        }?.also {
            sendMessage(
                chatId, listOf(
                    "Скажите какое у вас настроение сегодня?",
                    "1) Возбужденное (я хочу беситься/веселиться),",
                    "2) Расслабленное (я хочу почиллить)"
                ).joinToString("\n"),
                replyMarkup = createMarkupWithMoods()
            )
            botState.guests[chatId]!!.state = CHECK_MOOD
        } ?: sendMessage(
        chatId,
        "Ой, кажется, вы выдаете себя за другого, попробуйте еще раз.",
        replyMarkup = createMarkupWithCharacters()
    )
}

fun TextHandlerEnvironment.checkMood(botState: BotState, chatId: Long, text: String) {
    when (val mood = Mood.entries.firstOrNull { it.fullName == text }) {
        null -> sendMessage(
            chatId,
            "Хороший настрой;)\nНа что он больше всего похож из списка?",
            replyMarkup = createMarkupWithMoods()
        )

        EXITING, CHILL -> {
            botState.guests[chatId]?.apply {
                this.mood = mood
                state = AT_SWAMP
            }
            sendPhoto(chatId, "${botState.photoPath}swamp.jpg")
            sendMessage(chatId, "Спасибо! Проходите на Болото", ReplyKeyboardRemove())
        }
    }
}

fun TextHandlerEnvironment.checkSwampCommands(botState: BotState, chatId: Long, text: String) {
    when (text) {
        "#погнали" -> {
            val character = botState.guests[chatId]!!.character!!

            val photoPath = createPhotoPath(botState, character)
            if (Files.exists(Path.of(photoPath))) {
                sendPhoto(chatId, photoPath)
            }

            sendMessage(chatId, "Поздравляю, вы ${character.fullName}!")
            sendMessage(chatId, "Спасибо, вы готовы к приключениям!", ReplyKeyboardRemove())
            botState.guests[chatId]!!.state = CHARACTER_RECEIVED
        }

    }
}

fun TextHandlerEnvironment.checkCharacterReceivedCommands(botState: BotState, chatId: Long, text: String) {
    when (text) {
        "#ямыдюлок" -> {
            sendPhoto(chatId, "${botState.photoPath}dulok.jpeg")
            sendMessage(
                chatId,
                listOf(
                    "Добро пожаловать в Дюлок!",
                    "\uD83E\uDE9EВы готовы пройти тест с помощью Волшебных зеркал",
                    "\uD83D\uDC51Правила очень просты: на большом Волшебном зеркале и вашем карманном вы увидите вопрос",
                    "\uD83D\uDD58Ваша задача в течение отведенного времени ответить на него",
                    "\uD83D\uDC40Вопросы подразумевают либо выбор варианта, либо свободный ответ"
                )
                    .joinToString("\n")
            )
            botState.guests[chatId]?.state = READY_FOR_FINAL_TEST
        }

    }
}

private fun createPhotoPath(botState: BotState, character: Character) =
    botState.photoPath + "${character.name.lowercase()}.png"

private fun notDuplicate(botState: BotState, text: String) =
    !botState.guests.map { (k, v) -> v.character?.fullName }.contains(text)
