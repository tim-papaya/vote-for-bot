import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment


fun TextHandlerEnvironment.checkQuestionResultsWithAnswers(botState: BotState, adminChatId: Long) {
    val rightResults = botState.guests.filter { it.value.answers.contains(botState.getCurrentQuestion()?.id) }
        .filter { it.value.answers[botState.getCurrentQuestion()?.id]?.right ?: false }
        .map { it.value.character?.fullName ?: "" }
        .joinToString(",")
        .plus(": правильный ответ")

    sendMessage(adminChatId, rightResults)
}

fun TextHandlerEnvironment.checkQuestionResultsWithoutAnswers(botState: BotState, adminChatId: Long, question: Question) {
    val results = extractResultsFromGuestAnswers(botState, question)

    sendMessage(adminChatId, results)
}

private fun extractResultsFromGuestAnswers(botState: BotState, question: Question): String {
    val results = botState.guests.map {
        "${it.value.character!!.fullName}: ${it.value.answers[question.id]?.text ?: "-"}"
    }.joinToString("\n")
    return results
}

fun TextHandlerEnvironment.sendQuestionToQuests(
    botState: BotState,
    firstArgument: String
) {
    val question = botState.questions[firstArgument]
    if (question == null) {
        sendMessage(message.chat.id, "Вопрос не найден")
        return
    }
    botState.setCurrentQuestion(question)
    botState.guests.filter { it.value.state == State.READY_FOR_FINAL_TEST }
        .forEach {
            if (!question.hasAnswers)
                sendMessage(it.key, question.text)
            else
                sendMessage(it.key, question.text, createQuestionWithAnswersReply(question))

        }
}

fun TextHandlerEnvironment.getStatsForQuestionResultsWithAnswers(botState: BotState, adminChatId: Long) {
    val results = botState.guests.map { guestInfoEntry ->
        val count = guestInfoEntry.value.answers.count { it.value.right }
        "${guestInfoEntry.value.character?.fullName ?: ""}: $count"
    }.joinToString("\n")

    sendMessage(adminChatId, results)
}

fun TextHandlerEnvironment.getStatsForQuestionResultsWithoutAnswers(botState: BotState, adminChatId: Long) {
    val results = botState.guests.map { guestInfoEntry ->
        val count = guestInfoEntry.value.answers.count { it.value.right }
        "${guestInfoEntry.value.character?.fullName ?: ""}: $count"
    }.joinToString("\n")

    sendMessage(adminChatId, results)
}
