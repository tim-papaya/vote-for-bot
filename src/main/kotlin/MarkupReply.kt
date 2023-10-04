import com.github.kotlintelegrambot.entities.KeyboardReplyMarkup
import com.github.kotlintelegrambot.entities.keyboard.KeyboardButton

fun createMarkupWithCharacters(): KeyboardReplyMarkup {
    val characters = Character.entries.map { it.fullName }
    return KeyboardReplyMarkup(
        listOf(
            createCharactersLine(characters[0], characters[1], characters[2]),
            createCharactersLine(characters[3], characters[4], characters[5]),
            createCharactersLine(characters[6], characters[7], characters[8]),
            createCharactersLine(characters[9], characters[10], characters[11]),
        )
    )
}

private fun createCharactersLine(vararg chars: String) =
    chars.map { KeyboardButton(it) }.toList()

fun createMarkupWithMoods(): KeyboardReplyMarkup =
    Mood.entries.map { listOf(KeyboardButton(it.fullName)) }
        .toList()
        .let { KeyboardReplyMarkup(it) }

fun createQuestionWithAnswersReply(question: Question): KeyboardReplyMarkup =
    question.answers.map { listOf(KeyboardButton(it)) }
        .toList()
        .let { KeyboardReplyMarkup(it) }

