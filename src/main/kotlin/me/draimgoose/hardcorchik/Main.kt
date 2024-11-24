package me.draimgoose.hardcorchik

import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class Main : JavaPlugin(), Listener {

    override fun onEnable() {
        // Plugin startup logic
        server.pluginManager.registerEvents(this, this)
        logger.info("Hardcor4ik успешно запущен.")
    }

    override fun onDisable() {
        // Plugin shutdown logic
        logger.info("Hardcor4ik успешно отключен.")
    }

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val player = event.entity
        val world = player.world

        // Проверяем, что мир в хардкорном режиме
        if (world.isHardcore) {
            // Уведомляем игроков о сбросе мира
            Bukkit.broadcastMessage("Игрок ${player.name} погиб в хардкорном мире! Мир будет сброшен...")

            // Задержка перед сбросом мира
            Bukkit.getScheduler().runTaskLater(this, Runnable {
                resetWorld(world.name) // Передаём имя мира
            }, 100L) // 5 секунд (100 тиков)
        }
    }

    private fun resetWorld(worldName: String) {
        val worldFolder = File(worldName)

        // Удаляем текущий мир
        if (worldFolder.exists()) {
            Bukkit.getOnlinePlayers().forEach { it.kickPlayer("Мир сбрасывается!") }
            Bukkit.unloadWorld(worldName, false)
            worldFolder.deleteRecursively()
        }

        // Создаем новый мир
        val newWorld: World? = WorldCreator(worldName).createWorld()

        if (newWorld == null) {
            // Если мир не удалось создать, логируем ошибку и уведомляем игроков
            logger.severe("Не удалось создать мир с именем $worldName!")
            Bukkit.broadcastMessage("Произошла ошибка при сбросе мира!")
            return
        }

        // Телепортируем игроков в новый мир
        Bukkit.getScheduler().runTask(this, Runnable {
            Bukkit.getOnlinePlayers().forEach { player ->
                player.teleport(newWorld.spawnLocation)
            }
        })

        Bukkit.broadcastMessage("Мир был успешно сброшен и создан заново!")
    }
}
