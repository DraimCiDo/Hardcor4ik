package me.draimgoose.hardcorchik.managers

import me.draimgoose.hardcorchik.Main
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Scoreboard

class ScoreboardManager(private val plugin: Main) {
    private val scoreboard: Scoreboard

    init {
        val scoreboardManager = Bukkit.getScoreboardManager()
        if (scoreboardManager == null) {
            plugin.logger.severe("Ошибка: ScoreboardManager не найден!")
            throw IllegalStateException("ScoreboardManager не доступен.")
        }
        scoreboard = scoreboardManager.newScoreboard

        val obj = scoreboard.registerNewObjective("hardcorchik", "dummy", "${ChatColor.GOLD}GwinBlade's Hardcore")
        obj.displaySlot = DisplaySlot.SIDEBAR
    }

    fun updateAllPlayers(attemptCount: Int, timer: Int, progress: Map<String, Boolean>, achievements: Map<String, Boolean>) {
        val hours = timer / 3600
        val minutes = (timer % 3600) / 60
        val seconds = timer % 60
        val timeFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds)

        val obj = scoreboard.getObjective("hardcorchik") ?: return
        obj.getScore("${ChatColor.YELLOW}Статистика:").score = 8
        obj.getScore("${ChatColor.WHITE} ▶ Попытка: ${ChatColor.GREEN}#$attemptCount").score = 7
        obj.getScore("${ChatColor.WHITE} ▶ Таймер: ${ChatColor.AQUA}$timeFormatted").score = 6
        obj.getScore("${ChatColor.WHITE} ▶ Прогресс: ${ChatColor.GREEN}$progress%").score = 5

        obj.getScore(" ").score = 4 // Пустая строка для разделения
        obj.getScore("${ChatColor.YELLOW}Достижения:").score = 3

        achievements.forEach { (name, completed) ->
            val status = if (completed) "${ChatColor.GREEN}✔" else "${ChatColor.RED}✖"
            obj.getScore("${ChatColor.WHITE} ▶ $name: $status").score = 2 - achievements.keys.indexOf(name)
        }

        Bukkit.getOnlinePlayers().forEach { player ->
            player.scoreboard = scoreboard
        }
    }
}
