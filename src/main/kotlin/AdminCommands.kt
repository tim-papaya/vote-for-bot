import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment


fun TextHandlerEnvironment.checkQuestionResultsWithAnswers(botState: BotState, adminChatId: Long) {
    val rightResults = botState.guests.filter { it.value.answers.contains(botState.getCurrentQuestion()?.id) }
        .filter { it.value.answers[botState.getCurrentQuestion()?.id]?.right ?: false }
        .map { it.value.character?.fullName }
        .joinToString(",")
        .plus(": правильный ответ")

    sendMessage(adminChatId, rightResults)
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
    botState.guests.forEach {
        sendMessage(it.key, question.text, createQuestionWithAnswersReply(question))
    }
}
