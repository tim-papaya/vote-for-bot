class Question(
    val text: String,
    val hasAnswers: Boolean,
    val answers: List<String>
) {
    fun answersAsText() =
        answers
}