package me.draimgoose.hardcorchik.managers

import me.draimgoose.hardcorchik.Main
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

class AchievementManager(private val plugin: Main) : Listener {
    private val achievements: MutableMap<String, Pair<Int, Int>> = mutableMapOf(
        "Элитры" to Pair(0, 1), // 0/1
        "Вылечить жителя" to Pair(0, 1), // 0/1
        "Аксолотль" to Pair(0, 1), // 0/1
        "Спарить хоглинов" to Pair(0, 4), // 0/4
        "Убить дракона" to Pair(0, 1), // 0/1
        "Убить визера" to Pair(0, 1) // 0/1
    )

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    // Метод для обновления прогресса достижений
    fun updateAchievementProgress(playerName: String, achievement: String, amount: Int) {
        val currentProgress = achievements[achievement]?.first ?: return
        val targetProgress = achievements[achievement]?.second ?: return

        if (currentProgress < targetProgress) {
            achievements[achievement] = Pair(currentProgress + amount, targetProgress)
            plugin.logger.info("$playerName прогрессировал в достижении $achievement: ${currentProgress + amount}/$targetProgress")
        }
    }

    // Метод для получения прогресса достижений
    fun getAchievementsProgress(): Map<String, Boolean> {
        return achievements.mapValues { it.value.first >= it.value.second }
    }

    // Событие для достижения: сделать верстак
    @EventHandler
    fun onPlayerCraft(event: PlayerInteractEvent) {
        val player = event.player
        val item = event.item ?: return
        if (item.type == org.bukkit.Material.CRAFTING_TABLE) {
            updateAchievementProgress(player.name, "Сделать верстак", 1)
        }
    }

    // Событие для достижения: добыть дуба
    @EventHandler
    fun onPlayerMineWood(event: org.bukkit.event.block.BlockBreakEvent) {
        val player = event.player
        if (event.block.type == org.bukkit.Material.OAK_LOG) {
            updateAchievementProgress(player.name, "Добыть дуба", 1)
        }
    }

    // Событие для достижения: убить дракона
    @EventHandler
    fun onDragonKill(event: PlayerDeathEvent) {
        val player = event.entity
        if (player.killer != null && player.killer!!.name != null) {
            updateAchievementProgress(player.killer!!.name, "Убить дракона", 1)
        }
    }
}
