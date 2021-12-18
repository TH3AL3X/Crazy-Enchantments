package me.badbones69.crazyenchantments.enchantments

import me.badbones69.crazyenchantments.api.CrazyManager
import me.badbones69.crazyenchantments.api.enums.CEnchantments
import me.badbones69.crazyenchantments.api.managers.AllyManager
import me.badbones69.crazyenchantments.api.objects.AllyMob
import me.badbones69.crazyenchantments.api.objects.CEnchantment
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityTargetEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.world.ChunkUnloadEvent
import java.util.*

object AllyEnchantments : Listener {

    private val crazyManager = CrazyManager.getInstance()
    private val allyManager = AllyManager.getInstance()

    private val allyCooldown = hashMapOf<UUID, Calendar>()

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onAllySpawn(e: EntityDamageByEntityEvent): Unit = with(e) {
        if (e.isCancelled && crazyManager.isIgnoredEvent(e)) return

        if (e.entity !is Player && e.damager !is LivingEntity) return

        val player = e.entity as Player
        val enemy = e.damager as LivingEntity

        if (isCooldown(player)) return

        if (allyManager.isAlly(player, enemy)) {
            isCancelled = true
            return
        }

        when {
            e.entity is Player && e.damager is LivingEntity -> {
                runSpawner(player, enemy)
            }
            e.entity is LivingEntity && e.damager is Player -> {
                runSpawner(player, enemy)
            }
        }
    }

    @EventHandler
    fun onAllyTarget(e: EntityTargetEvent): Unit = with(e) {
        if (e.target !is Player && e.entity !is LivingEntity) return

        if (allyManager.isAlly(e.target as Player, e.entity as LivingEntity)) isCancelled = true
    }

    @EventHandler
    fun onAllyDeath(e: EntityDeathEvent): Unit = with(e) {
        if (allyManager.isAllyMob(e.entity)) {
            e.droppedExp = 0
            e.drops.clear()
        }
    }

    @EventHandler
    fun onAllyDespawn(e: ChunkUnloadEvent): Unit = with(e) {
        if (e.chunk.entities.isEmpty()) return

        e.chunk.entities.forEach {
            if (it !is LivingEntity) return
            if (allyManager.isAllyMob(it)) allyManager.getAllyMob(it).forceRemoveAlly()
        }
    }

    @EventHandler
    fun onPlayerLeave(e: PlayerQuitEvent): Unit = with(e) {
        allyManager.forceRemoveAllies(player)
    }

    private fun spawnMobs(player: Player, enemy: LivingEntity, allyType: AllyMob.AllyType, amount: Int) {
        val cooldown = Calendar.getInstance()
        cooldown.add(Calendar.MINUTE, 2)

        allyCooldown[player.uniqueId] = cooldown
        for (i in 0 until amount) {
            val ally = AllyMob(player, allyType)
            ally.spawnAlly(60)
            ally.attackEnemy(enemy)
        }
    }

    private fun runSpawner(player: Player, enemy: LivingEntity) {
        player.equipment?.armorContents?.forEach {
            if (!crazyManager.hasEnchantments(it)) return
            when {
                crazyManager.hasEnchantment(it, CEnchantments.TAMER) -> {
                    val power = crazyManager.getLevel(it, CEnchantments.TAMER)
                    spawnMobs(player, enemy, AllyMob.AllyType.WOLF, power)
                }
                crazyManager.hasEnchantment(it, CEnchantments.GUARDS) -> {
                    val power = crazyManager.getLevel(it, CEnchantments.GUARDS)
                    spawnMobs(player, enemy, AllyMob.AllyType.IRON_GOLEM, power)
                }
                crazyManager.hasEnchantment(it, CEnchantments.BEEKEEPER) -> {
                    val power = crazyManager.getLevel(it, CEnchantments.BEEKEEPER)
                    spawnMobs(player, enemy, AllyMob.AllyType.BEE, power)
                }
                crazyManager.hasEnchantment(it, CEnchantments.NECROMANCER) -> {
                    val power = crazyManager.getLevel(it, CEnchantments.NECROMANCER)
                    spawnMobs(player, enemy, AllyMob.AllyType.ZOMBIE, power)
                }
                crazyManager.hasEnchantment(it, CEnchantments.INFESTATION) -> {
                    val power = crazyManager.getLevel(it, CEnchantments.INFESTATION)
                    spawnMobs(player, enemy, AllyMob.AllyType.ENDERMITE, power * 3)
                    spawnMobs(player, enemy, AllyMob.AllyType.SILVERFISH, power * 3)
                }
                else -> {
                    allyManager.setEnemy(player, enemy)
                }
            }
        }
    }

    private fun isCooldown(player: Player): Boolean {
        when {
            allyCooldown.containsKey(player.uniqueId) -> {
                if (Calendar.getInstance().before(allyCooldown[player.uniqueId])) return true
                allyCooldown.remove(player.uniqueId)
            }
        }
        return false
    }
}