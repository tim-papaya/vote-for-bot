import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import java.time.LocalTime


fun TextHandlerEnvironment.checkQuestionResultsWithRightAnswer(botState: BotState, adminChatId: Long) {
    val rightResults = botState.guests.filter { it.value.answers.contains(botState.getCurrentQuestion()?.id) }
        .filter { it.value.answers[botState.getCurrentQuestion()?.id]?.right ?: false }
        .map { it.value.character?.fullName ?: "" }
        .joinToString(", ")
        .plus(": правильный ответ")

    sendMessage(adminChatId, rightResults)
}

fun TextHandlerEnvironment.checkQuestionResultsWithoutRightAnswer(
    botState: BotState,
    adminChatId: Long,
    question: Question
) {
    if (question.hasAnswers()) {
        sendMessage(adminChatId, extractAndGroupResultsFromGuestAnswers(botState, question))
    } else {
        sendMessage(adminChatId, extractResultsFromGuestAnswers(botState, question))
    }
}

private fun extractAndGroupResultsFromGuestAnswers(botState: BotState, question: Question): String {
    val results = question.answers.map { answer ->
        val names = botState.guests
            .filter { (it.value.answers[question.id]?.text ?: "") == answer }
            .map { it.value.character!!.fullName }
            .joinToString(", ")
        "\"$answer\": $names"
    }.joinToString("\n")
    return results
}

private fun extractResultsFromGuestAnswers(botState: BotState, question: Question): String {
    val results = botState.guests.map {
        "${it.value.character!!.fullName}: ${it.value.answers[question.id]?.text ?: "-"}"
    }.joinToString("\n")
    return results
}

fun TextHandlerEnvironment.sendQuestionToGuests(
    botState: BotState,
    firstArgument: String
) {
    val question = botState.questions[firstArgument]
    if (question == null) {
        sendMessage(
            message.chat.id,
            "Вопрос не найден"
        )
        return
    }
    botState.setCurrentQuestion(question)
    botState.guests.filter { it.value.state == State.READY_FOR_FINAL_TEST }
        .forEach {
            sendQuestionWithTimer(it.key, botState, question)
        }
    botState.admins.forEach {
        sendQuestionWithTimer(it, botState, question)
    }

}

private fun TextHandlerEnvironment.sendQuestionWithTimer(
    chatId: Long,
    botState: BotState,
    question: Question
) {
    val timerText = "На ответ осталось"
    val timerMessageId = sendMessage(chatId, "$timerText ${botState.questionTimeLimit}").get().messageId

    if (!question.hasAnswers())
        sendMessage(chatId, question.text)
    else
        sendMessage(chatId, question.text, createQuestionWithAnswersReply(question))
    val startTime = LocalTime.now().toSecondOfDay()

    launchTimer(chatId, timerMessageId, botState.questionTimeLimit, startTime, timerText)
}


fun TextHandlerEnvironment.getStatsForQuestionResultsWithRightAnswer(botState: BotState, adminChatId: Long) {
    val results = botState.guests.map { guestInfoEntry ->
        val count = guestInfoEntry.value.answers.count { it.value.right }
        "${guestInfoEntry.value.character?.fullName ?: ""}: $count"
    }.joinToString("\n")

    sendMessage(adminChatId, results)
}

fun TextHandlerEnvironment.getStatsForQuestionResultsWithoutAnswers(botState: BotState, adminChatId: Long) {
    val results = botState.questions.entries.filter {
        !it.value.hasRightAnswer()
    }.joinToString("\n------\n") { q ->
        val guestAnswers = botState.guests
            .filter { it.value.answers.contains(q.value.id) }
            .map { "${it.value.character!!.fullName}: ${it.value.answers[q.value.id]?.text}" }
            .joinToString("\n")
        "${q.value.text} (id:${q.value.id})\n$guestAnswers"
    }

    sendMessage(adminChatId, results)
}

fun TextHandlerEnvironment.getMoods(
    botState: BotState,
    adminChatId: Long
) {
    val moods = Mood.entries.joinToString("\n") { mood ->
        val guests = botState.guests.filter { it.value.mood == mood }
            .map { it.value.character?.fullName ?: "no_character" }
            .joinToString(", ")
        "${mood.fullName}: $guests"
    }
    sendMessage(adminChatId, moods)
}
