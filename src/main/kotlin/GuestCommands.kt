import Mood.*
import State.*
import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import com.github.kotlintelegrambot.entities.ReplyKeyboardRemove

fun TextHandlerEnvironment.checkQuestionWithAnswers(botState: BotState, chatId: Long, text: String) {
    val currentQuestion = botState.getCurrentQuestion()
    if (currentQuestion == null) {
        sendMessage(chatId, "Вопроса еще нет.")
        return
    }

    val guestInfo = botState.guests[chatId]!!

    val answer = guestInfo.answers[currentQuestion.id]

    if (answer != null) {
        sendMessage(chatId, "Спасибо! Вы уже ответили.")
        return
    }

    if (currentQuestion.answers.contains(text)) {
        guestInfo.answers[currentQuestion.id] = Answer(currentQuestion.id, currentQuestion.isRightAnswer(text), text)
        sendMessage(chatId, "Ответ засчитан!", ReplyKeyboardRemove())
    } else {
        sendMessage(chatId, "Такого ответа нет")
        return
    }
}

fun TextHandlerEnvironment.checkSwampCommands(botState: BotState, chatId: Long, text: String) {
    when (text) {
        "#ямыдюлок" -> {
            sendMessage(
                chatId,
                "«Добро пожаловать в Дюлок! Вы готовы пройти тест с помощью Волшебных зеркал. Правила очень просты: на большом Волшебном зеркале и вашем карманном Волшебном зеркале вы увидите вопрос. Ваша задача в течение 30 секунд ответить на него. Вопросы подразумевают либо выбор варианта, либо свободный ответ».\n" +
                        "Проходится тест, заполнение правильных ответов вручную. Подсчет голосов – выдача приза в конце вечера."
            )
            botState.guests[chatId]?.state = READY_FOR_FINAL_TEST
        }

    }
}

fun TextHandlerEnvironment.checkMood(botState: BotState, chatId: Long, text: String) {
    when (val mood = Mood.entries.firstOrNull { it.fullName == text }) {
        null -> sendMessage(
            chatId,
            "Хороший настрой;)\nНа что он больше всего похож из списка?",
            replyMarkup = createMarkupWithMoods()
        )

        EXITING, DREAMY, CHILL -> {
            botState.guests[chatId]?.apply {
                this.mood = mood
                state = AT_SWAMP
            }
            sendMessage(chatId, "Спасибо! Проходите на Болото", ReplyKeyboardRemove())
            sendPhoto(chatId, createPhotoPath(botState, botState.guests[chatId]!!.character!!))
        }
    }
}

private fun createPhotoPath(botState: BotState, character: Character) =
    botState.photoPath + "\\${character.name.lowercase()}.png"

fun TextHandlerEnvironment.checkPickingCharacter(botState: BotState, chatId: Long, text: String) {
    Character.entries.firstOrNull { it.fullName == text.trim() }
        .takeIf { notDuplicate(botState, text) }?.also {
            botState.guests[chatId]?.apply {
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

private fun notDuplicate(botState: BotState, text: String) =
    !botState.guests.map { (k, v) -> v.character?.fullName }.contains(text)

fun TextHandlerEnvironment.checkWelcomeCommands(botState: BotState, chatId: Long) {
    when (text) {
        "/start" -> {
            sendMessage(chatId, "Какой вы персонаж?", createMarkupWithCharacters())
            botState.guests[chatId] = GuestInfo(chatId, PICKING_CHARACTER)
        }
    }
}