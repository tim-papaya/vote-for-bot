enum class Character(val fullName: String) {
    DONKEY("Осел"),
    DRAGON("Дракониха"),
    CHARMING("Принц Чарминг"),
    DORIS("Дорис"),
    WOLF("Волк")
}

enum class State {
    PICKING_CHARACTER, CHECK_MOOD, AT_SWAMP, READY_FOR_FINAL_TEST
}

enum class Mood(val fullName: String) {
    EXITING("Возбужденное"),
    DREAMY("Мечтательное"),
    CHILL("Рассудительное")
}

class GuestInfo(
    val chatId: Long,
    var state: State,
    var character: Character? = null,
    var mood: Mood? = null,
    val answers: MutableMap<String, Answer> = HashMap()
) {

}
