package me.draimgoose.hardcorchik.managers

import me.draimgoose.hardcorchik.Main
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.File

class WorldManager(
    private val plugin: Main,
    private val achievementManager: AchievementManager,
    private val scoreboardManager: ScoreboardManager
) : CommandExecutor {
    private var attemptCount = 0

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (command.name.equals("resetworld", ignoreCase = true)) {
            if (sender is Player) {
                resetWorld(sender.world.name)
                sender.sendMessage("Мир был сброшен! Удачи в следующей попытке.")
            } else {
                sender.sendMessage("Эту команду может использовать только игрок!")
            }
            return true
        }
        return false
    }

    fun resetWorld(oldWorldName: String) {
        attemptCount++

        val newWorldName = "world_${System.currentTimeMillis()}"

        deleteOldWorld(oldWorldName)

        val newWorld: World? = WorldCreator(newWorldName).createWorld()
        if (newWorld == null) {
            plugin.logger.severe("Не удалось создать новый мир с именем $newWorldName!")
            Bukkit.broadcastMessage("Произошла ошибка при создании нового мира!")
            return
        }

        Bukkit.getOnlinePlayers().forEach { player ->
            resetPlayerState(player, newWorld)
        }

        Bukkit.broadcastMessage("Мир был успешно сброшен! Попытка #$attemptCount началась.")

        // Обновляем скорборд
        scoreboardManager.updateAllPlayers(attemptCount, 0, achievementManager.getAchievementsProgress())
    }

    private fun deleteOldWorld(worldName: String) {
        val world = Bukkit.getWorld(worldName)
        if (world != null) {
            Bukkit.unloadWorld(world, false)
        }

        val worldFolder = File(worldName)
        if (worldFolder.exists()) {
            worldFolder.deleteRecursively()
            plugin.logger.info("Старый мир $worldName успешно удалён.")
        } else {
            plugin.logger.warning("Папка мира $worldName не найдена.")
        }
    }

    private fun resetPlayerState(player: Player, newWorld: World) {
        player.teleport(newWorld.spawnLocation)
        player.gameMode = GameMode.SURVIVAL
        player.health = 20.0
        player.foodLevel = 20
        player.inventory.clear()
        player.exp = 0f
        player.level = 0
        player.sendMessage("Вы начали новую попытку! Удачи!")
    }
}
