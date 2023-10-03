enum class Character(val fullName: String) {
    DONKEY("Осел"),
    DRAGON("Дракониха"),
    CHARMING("Принц Чарминг"),
    DORIS("Дорис"),
    WOLF("Большой и страшный серый волк"),
    MICE_1("Слепая мышь I"),
    MICE_2("Слепая мышь II"),
    SHALTAY("Шалтай-Болтай"),
    PINOCCHIO("Пинокио"),
    PUSS_IN_BOOTS("Кот в сапогах"),
    GINGY("Кот в сапогах"),
    LORD_FARQUAAD("Лорд Фаркуад")
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
