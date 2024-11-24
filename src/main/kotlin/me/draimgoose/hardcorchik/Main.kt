package me.draimgoose.hardcorchik

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class Main : JavaPlugin() {
    private var attemptCount = 0 // Счётчик попыток

    override fun onEnable() {
        logger.info("Hardcorchik плагин включён!")
    }

    override fun onDisable() {
        logger.info("Hardcorchik плагин отключён!")
    }

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

    private fun resetWorld(oldWorldName: String) {
        attemptCount++

        // Уникальное имя для нового мира
        val newWorldName = "world_${System.currentTimeMillis()}"

        // Удаляем старый мир
        deleteOldWorld(oldWorldName)

        // Создаём новый мир
        val newWorld: World? = WorldCreator(newWorldName).createWorld()
        if (newWorld == null) {
            logger.severe("Не удалось создать новый мир с именем $newWorldName!")
            Bukkit.broadcastMessage("Произошла ошибка при создании нового мира!")
            return
        }

        // Телепортируем игроков в новый мир и восстанавливаем их состояние
        Bukkit.getOnlinePlayers().forEach { player ->
            resetPlayerState(player, newWorld)
        }

        Bukkit.broadcastMessage("Мир был успешно сброшен! Попытка #$attemptCount началась.")
    }

    private fun deleteOldWorld(worldName: String) {
        val world = Bukkit.getWorld(worldName)
        if (world != null) {
            Bukkit.unloadWorld(world, false) // Выгружаем мир из памяти
        }

        val worldFolder = File(worldName)
        if (worldFolder.exists()) {
            worldFolder.deleteRecursively() // Удаляем папку мира
            logger.info("Старый мир $worldName успешно удалён.")
        } else {
            logger.warning("Папка мира $worldName не найдена.")
        }
    }

    private fun resetPlayerState(player: Player, newWorld: World) {
        // Телепортируем игрока в новый мир
        player.teleport(newWorld.spawnLocation)

        // Устанавливаем режим выживания
        if (player.gameMode != GameMode.SURVIVAL) {
            player.gameMode = GameMode.SURVIVAL
        }

        // Сбрасываем здоровье и сытость
        player.health = 20.0
        player.foodLevel = 20

        // Очищаем инвентарь
        player.inventory.clear()

        // Сбрасываем опыт
        player.exp = 0f
        player.level = 0

        // Уведомляем игрока
        player.sendMessage("Вы начали новую попытку! Удачи!")
    }
}
