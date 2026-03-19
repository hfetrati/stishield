package com.hemad.stishield.model.game

data class BattleResult(val message: String, val possibleActions: List<String>)
data class BattleStats(var playerHealth: Int, var enemyHealth: Int, var specialAbilityUsed: Boolean, var isPlayerDefeated:Boolean = false, var isEnemyDefeated:Boolean = false)