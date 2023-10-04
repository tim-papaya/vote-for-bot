private const val NO_ANSWER_CODE = -1

class Question(
    val id: String,
    val text: String,
    val answers: List<String>,
    private val right: Int
) {

    fun isRightAnswer(answer: String) =
        if (right == NO_ANSWER_CODE) false else answers[right] == answer

    fun hasRightAnswer() = right != NO_ANSWER_CODE

    fun hasAnswers() = answers.isNotEmpty()
}

class Answer(
    val questionId: String,
    val right: Boolean,
    val text: String
)