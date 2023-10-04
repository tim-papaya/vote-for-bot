// "Осел",
// "Дракониха",
// "Принц Чарминг",
// "Дорис",
// "Большой и страшный серый волк",
// "Слепая мышь I",
// "Слепая мышь II",
// "Шалтай-Болтай",
// "Пинокио",
// "Кот в сапогах",
// "Пряня",
// "Лорд Фаркуад"

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
    GINGY("Пряня"),
    LORD_FARQUAAD("Лорд Фаркуад")
}

enum class State {
    PICKING_CHARACTER, AT_SWAMP, CHECK_MOOD, CHARACTER_RECEIVED, READY_FOR_FINAL_TEST
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
