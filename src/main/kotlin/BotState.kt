import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File
import java.util.Collections.synchronizedMap
import java.util.Collections.synchronizedSet

class BotState(
    val questions: Map<String, Question>,
    val guests: MutableMap<Long, GuestInfo>,
    private var currentQuestion: Question? = null,
    private val guestsPath: String,
    val photoPath: String,
    val questionTimeLimit: Int = 30,
    val admins: MutableSet<Long>,
    val adminsPath: String,
) {
    fun getCurrentQuestion() = currentQuestion

    fun setCurrentQuestion(question: Question?) {
        currentQuestion = question
    }

    @Synchronized
    fun save() {
        val mapper = jacksonObjectMapper()
        mapper.writeValue(File(guestsPath), guests)
        mapper.writeValue(File(adminsPath), admins)
    }

}

fun loadBotState(questionsPath: String, guestsPath: String, photoPath: String, adminsPath: String): BotState {
    val mapper = jacksonObjectMapper()
    val guests = if (File(guestsPath).exists()) synchronizedMap<Long, GuestInfo>(mapper.readValue(File(guestsPath)))
    else synchronizedMap(mapOf())

    val admins = if (File(adminsPath).exists()) synchronizedSet<Long>(mapper.readValue(File(adminsPath)))
    else synchronizedSet(setOf())

    return BotState(
        mapper.readValue(File(questionsPath)),
        guests,
        guestsPath = guestsPath,
        photoPath = photoPath + "\\",
        admins = admins,
        adminsPath = adminsPath
    )
}


