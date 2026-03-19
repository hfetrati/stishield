package com.hemad.stishield.model.game

import android.content.Context
import com.hemad.stishield.R
import com.hemad.stishield.model.common.UserDataRepository
import kotlinx.serialization.json.Json
import java.io.InputStream
import kotlin.random.Random

class GameRepository(context: Context) {

    val appContext = context
    var levels:List<Level>
    var facts:List<Fact>

    lateinit var currentBattleStats:BattleStats

    val userDataRepository = UserDataRepository(context)


    init {
        levels = readLevelsFromRaw()
        facts = readFactsFromRaw()
        assignActions()
        assignImages()
        resetBattleStats()
    }

    fun resetBattleStats(){
        currentBattleStats = BattleStats(playerHealth = 100, enemyHealth = 100, specialAbilityUsed = false)
    }

    fun restartGame(){
        resetBattleStats()
        userDataRepository.gameLevelIndex = 0
        userDataRepository.gameTaskIndex = 0
        userDataRepository.gameFactIndex = 0
    }

    fun assignImages() {
        levels.forEach { level ->
            level.tasks.forEach { task ->
                task.imageID = when (level.id) {
                    0 -> when (task.id) {
                        0 -> R.drawable.l0t0
                        1,2 -> R.drawable.l0t1_2
                        3,4 -> R.drawable.l0t3_4
                        5,6 -> R.drawable.l0t5_6
                        7,8 -> R.drawable.l0t7_8
                        9 -> R.drawable.l0t9
                        else -> null
                    }
                    1 -> when (task.id) {
                        0,1 -> R.drawable.l1t0_1
                        2 -> R.drawable.l1t2
                        3 -> R.drawable.l1t3
                        else -> null
                    }
                    2 -> when (task.id) {
                        0 -> R.drawable.l2t0
                        1 -> R.drawable.l2t1
                        2 -> R.drawable.l2t2
                        3 -> R.drawable.l2t3
                        else -> null
                    }
                    3 -> when (task.id) {
                        0 -> R.drawable.l3t0
                        1,2 -> R.drawable.l3t1_2
                        3 -> R.drawable.l3t3
                        else -> null
                    }
                    4 -> when (task.id) {
                        0 -> R.drawable.l4_t0
                        1,2 -> R.drawable.l4_t1_2
                        3,4 -> R.drawable.l4_t3_4
                        5 -> R.drawable.l4_t5
                        6 -> R.drawable.l4_t6
                        else -> null
                    }
                    5 -> when (task.id) {
                        0 -> R.drawable.l5t0
                        1 -> R.drawable.l5t1_2
                        2,3 -> R.drawable.l5t3
                        else -> null
                    }
                    6 -> when (task.id) {
                        0 -> R.drawable.l6t0
                        1 -> R.drawable.l6t1
                        2,3 -> R.drawable.l6t2_3
                        4 -> R.drawable.l6t4
                        5 -> R.drawable.l6t5
                        6,8,9,10 -> R.drawable.l6t6_8_9_10
                        7 -> R.drawable.l6t7
                        else -> null
                    }
                    7 -> when (task.id) {
                        0 -> R.drawable.l7t0
                        else -> null
                    }

                    else -> null
                }
            }
        }
    }


    fun assignActions() {
        levels.forEach { level ->
            level.tasks.forEach { task ->
                task.action = when (level.id) {

                    0 -> when (task.id) {
                        0 -> { _ -> GameResult.Continue() }
                        1 -> { _ -> GameResult.Continue() }
                        2 -> { _ -> GameResult.Continue() }
                        3 -> { answer ->
                            if (answer == task.choices[task.correctChoiceIndex!!]) GameResult.Continue() else null
                        }
                        4 -> { _ -> GameResult.Continue() }
                        5 -> { answer ->
                            if (answer == task.choices[task.correctChoiceIndex!!]) GameResult.Continue() else null
                        }
                        6 -> { _ -> GameResult.Continue()}
                        7 -> { answer ->
                            if (answer == task.choices[task.correctChoiceIndex!!]) GameResult.Continue() else null
                        }
                        8 -> { _ -> GameResult.Continue()}
                        9 -> { answer ->
                            when (answer) {
                                "View the scroll" -> GameResult.Achievement()
                                "Continue your journey" -> GameResult.Success()
                                else -> null
                            }
                        }
                        else -> null
                    }
                    1 -> when (task.id){
                        0 -> { answer ->
                            if (answer == task.choices[task.correctChoiceIndex!!]) GameResult.Continue() else null
                        }
                        1 -> { _ -> GameResult.Continue()}
                        2 -> { input -> battleAction(input)}
                        3 -> { answer ->
                            when (answer) {
                                "View the scroll" -> GameResult.Achievement()
                                "Continue your journey" -> GameResult.Success()
                                else -> null
                            }
                        }
                        else -> null
                    }
                    2 -> when (task.id){
                        0 -> { _ -> GameResult.Continue()}
                        1 -> { answer ->
                            if (answer == task.choices[task.correctChoiceIndex!!]) GameResult.Continue() else null
                        }
                        2 -> { answer ->
                            if (answer == task.choices[task.correctChoiceIndex!!]) GameResult.Continue() else null
                        }
                        3 -> { answer ->
                            when (answer) {
                                "View the scroll" -> GameResult.Achievement()
                                "Continue your journey" -> GameResult.Success()
                                else -> null
                            }
                        }
                        else -> null
                    }
                    3 -> when (task.id){
                        0 -> { _ -> GameResult.Continue()}
                        1 -> { _ -> GameResult.Continue()}
                        2 -> { _ -> GameResult.Continue()}
                        3 -> { answer ->
                            when (answer) {
                                "View the scroll" -> GameResult.Achievement()
                                "Continue your journey" -> GameResult.Success()
                                else -> null
                            }
                        }
                        else -> null
                    }
                    4 -> when (task.id){
                        0 -> { _ -> GameResult.Continue()}
                        1 -> { answer ->
                            if (answer == task.choices[task.correctChoiceIndex!!]) GameResult.Continue() else null
                        }
                        2 -> { _ -> GameResult.Continue()}
                        3 -> { input -> battleAction(input)}
                        4 -> { _ -> GameResult.Continue()}
                        5 -> { answer ->
                            if (answer == task.choices[task.correctChoiceIndex!!]) GameResult.Continue() else null
                        }
                        6 -> { answer ->
                            when (answer) {
                                "View the scroll" -> GameResult.Achievement()
                                "Continue your journey" -> GameResult.Success()
                                else -> null
                            }
                        }
                        else -> null
                    }
                    5 -> when (task.id){
                        0 -> { _ -> GameResult.Continue()}
                        1 -> { _ -> GameResult.Continue()}
                        2 -> { _ -> GameResult.Continue()}
                        3 -> { answer ->
                            when (answer) {
                                "View the scroll" -> GameResult.Achievement()
                                "Continue your journey" -> GameResult.Success()
                                else -> null
                            }
                        }
                        else -> null
                    }

                    6 -> when (task.id){
                        0 -> { _ -> GameResult.Continue()}
                        1 -> { answer ->
                            if (answer == task.choices[task.correctChoiceIndex!!]) GameResult.Continue() else null
                        }
                        2 -> { _ -> GameResult.Continue()}
                        3 -> { _ -> GameResult.Continue()}
                        4 -> { answer ->
                            if (answer == task.choices[task.correctChoiceIndex!!]) GameResult.Continue() else null
                        }
                        5 -> { input -> battleAction(input)}
                        6 ->  { _ -> GameResult.Continue()}
                        7 -> { input -> battleAction(input)}
                        8 ->  { _ -> GameResult.Continue()}
                        9 ->  { _ -> GameResult.Continue()}
                        10 -> { answer ->
                            when (answer) {
                                "View the scroll" -> GameResult.Achievement()
                                "Continue your journey" -> GameResult.Success()
                                else -> null
                            }
                        }
                        else -> null
                    }

                    else -> null
                }
            }
        }
    }

    fun battleAction(action: String): GameResult {

        if (currentBattleStats.isEnemyDefeated){
            resetBattleStats()
            return GameResult.Continue()
        } else if (currentBattleStats.isPlayerDefeated) {
            resetBattleStats()
            return GameResult.Failure()
        }

        val enemyName = getCurrentEnemyName()
        var message = ""

        when (action) {
            "Attack" -> {
                val damageToBeast = Random.nextInt(15, 21)
                currentBattleStats.enemyHealth -= damageToBeast
                message = "You strike the $enemyName, dealing ${damageToBeast}% damage.\n\n"
                if (Random.nextBoolean()) {
                    val damageToPlayer = Random.nextInt(5, 11)
                    currentBattleStats.playerHealth -= damageToPlayer
                    message += "The $enemyName retaliates, dealing ${damageToPlayer}% damage to you.\n\n"
                }
            }
            "Defend" -> {
                val damage = Random.nextInt(1, 6)
                currentBattleStats.playerHealth -= damage
                message = "You brace yourself, but the $enemyName's attack still hits, dealing ${damage}% damage.\n\n"
            }
            "Use Voodoo" -> {
                currentBattleStats.specialAbilityUsed = true
                if (Random.nextBoolean()) {
                    currentBattleStats.playerHealth = 100
                    message = "You used voodoo, restoring your health to full.\n\n"
                } else {
                    val damageToBeast = Random.nextInt(30, 41)
                    currentBattleStats.enemyHealth -= damageToBeast
                    message = "You used voodoo, dealing ${damageToBeast}% damage.\n\n"
                }
            }
        }

        if (currentBattleStats.enemyHealth > 0) {
            val beastAction = Random.nextInt(1, 3)
            when (beastAction) {
                1 -> {
                    val damage = Random.nextInt(10, 16)
                    currentBattleStats.playerHealth -= damage
                    message += "The $enemyName swipes at you, dealing ${damage}% damage.\n\n"
                }
                2 -> {
                    val damage = Random.nextInt(20, 26)
                    currentBattleStats.playerHealth -= damage
                    val recoilDamage = Random.nextInt(5, 11)
                    currentBattleStats.enemyHealth -= recoilDamage
                    message += "The $enemyName charges at you, dealing ${damage}% damage but takes ${recoilDamage}% recoil damage.\n\n"
                }
            }
        }
        val possibleActions:List<String>
        if (currentBattleStats.playerHealth <= 0) {
            possibleActions = listOf("Try Again")
        } else if (currentBattleStats.enemyHealth <= 0) {
            possibleActions = listOf("Continue your journey")
        } else if (!currentBattleStats.specialAbilityUsed) {
            possibleActions = listOf("Attack", "Defend","Use Voodoo")
        } else {
            possibleActions = listOf("Attack", "Defend")
        }

        if (currentBattleStats.playerHealth <= 0) {
            message += "<b>You have been defeated by the $enemyName.\n\n</b>"
        } else if (currentBattleStats.enemyHealth <= 0) {
            message += "<b>You have defeated the $enemyName!\n\n</b>"
        }

        val healthBars = createHealthBars(currentBattleStats.playerHealth, currentBattleStats.enemyHealth, enemyName!!)
        message = healthBars + message

        if (currentBattleStats.playerHealth <= 0) {
            currentBattleStats.isPlayerDefeated = true
        } else if (currentBattleStats.enemyHealth <= 0) {
            currentBattleStats.isEnemyDefeated = true
        }

        return GameResult.Repeat(content = BattleResult(message, possibleActions))

    }

    fun createHealthBars(playerHealth: Int, enemyHealth: Int, enemyName: String): String {
        val totalBars = 20
        val clampedPlayerHealth = playerHealth.coerceIn(0, 100)
        val clampedEnemyHealth = enemyHealth.coerceIn(0, 100)

        fun createHealthBar(clampedHealth: Int): String {
            val filledBars = (clampedHealth * totalBars) / 100
            val emptyBars = totalBars - filledBars
            return "[" + "#".repeat(filledBars) + "-".repeat(emptyBars) + "]"
        }

        val playerHealthBar = createHealthBar(clampedPlayerHealth)
        val enemyHealthBar = createHealthBar(clampedEnemyHealth)

        return """
        You:
        $playerHealthBar ${clampedPlayerHealth}%
        
        $enemyName:
        $enemyHealthBar ${clampedEnemyHealth}%
        
        
    """.trimIndent()
    }

    fun incrementLevel(increment:Int){
        userDataRepository.gameLevelIndex += increment
    }

    fun setTaskIndex(newIndex:Int){
        userDataRepository.gameTaskIndex = newIndex
    }

    fun incrementTaskIndex(increment:Int){
        userDataRepository.gameTaskIndex += increment
    }

    fun incrementFactIndex(increment: Int){
        userDataRepository.gameFactIndex += increment
    }

    suspend fun incrementPoints(increment: Int):Result<Unit>{
        userDataRepository.points += increment
        val result = userDataRepository.updateUserPoints()
        return result
    }

    fun getCurrentTask():Task {
        return levels[userDataRepository.gameLevelIndex].tasks[userDataRepository.gameTaskIndex]
    }

    fun getCurrentFact():Fact {
        return facts[userDataRepository.gameFactIndex]
    }

    fun getCurrentFactIndex():Int {
        return userDataRepository.gameFactIndex
    }

    fun getCurrentLevelTitle():String {
        return levels[userDataRepository.gameLevelIndex].title
    }

    fun getCurrentEnemyName():String?{
        return levels[userDataRepository.gameLevelIndex].tasks[userDataRepository.gameTaskIndex].enemyName
    }

    fun readFactsFromRaw():List<Fact> {

        val inputStream: InputStream = appContext.resources.openRawResource(R.raw.facts)
        val jsonString = inputStream.bufferedReader().use { it.readText() }

        return Json.decodeFromString(jsonString)
    }

    fun readLevelsFromRaw(): List<Level> {

        val inputStream: InputStream = appContext.resources.openRawResource(R.raw.game_levels)
        val jsonString = inputStream.bufferedReader().use { it.readText() }

        return Json.decodeFromString(jsonString)

    }







}