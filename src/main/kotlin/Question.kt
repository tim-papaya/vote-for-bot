class Question(
    val id: String,
    val text: String,
    val hasAnswers: Boolean,
    val answers: List<String>,
    private val right: Int
) {

    fun isRightAnswer(answer: String) = answers[right] == answer
}

class Answer(
    val questionId: String,
    val right: Boolean,
    val text: String
)