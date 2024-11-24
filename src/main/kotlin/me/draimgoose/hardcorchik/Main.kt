package me.draimgoose.hardcorchik

import me.draimgoose.hardcorchik.managers.AchievementManager
import me.draimgoose.hardcorchik.managers.ScoreboardManager
import me.draimgoose.hardcorchik.managers.WorldManager
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
    lateinit var worldManager: WorldManager
    lateinit var scoreboardManager: ScoreboardManager
    lateinit var achievementManager: AchievementManager

    override fun onEnable() {
        // Инициализация классов
        achievementManager = AchievementManager(this)
        scoreboardManager = ScoreboardManager(this)
        worldManager = WorldManager(this, achievementManager, scoreboardManager)

        // Регистрируем команду
        getCommand("resetworld")?.setExecutor(worldManager)

        logger.info("Hardcorchik плагин включён!")
    }

    override fun onDisable() {
        logger.info("Hardcorchik плагин отключён!")
    }
}
