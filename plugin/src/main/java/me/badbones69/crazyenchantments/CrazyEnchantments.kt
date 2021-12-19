package me.badbones69.crazyenchantments;

import me.badbones69.crazyenchantments.api.CrazyManager;
import me.badbones69.crazyenchantments.api.FileManager;
import me.badbones69.crazyenchantments.api.currencyapi.CurrencyAPI;
import me.badbones69.crazyenchantments.commands.*;
import me.badbones69.crazyenchantments.listeners.controllers.*;
import me.badbones69.crazyenchantments.listeners.enchantments.armor.Armor
import me.badbones69.crazyenchantments.listeners.enchantments.armor.Boots
import me.badbones69.crazyenchantments.listeners.enchantments.armor.Helmets
import me.badbones69.crazyenchantments.listeners.enchantments.tools.Hoes
import me.badbones69.crazyenchantments.listeners.enchantments.tools.PickAxes
import me.badbones69.crazyenchantments.listeners.enchantments.tools.Tools
import me.badbones69.crazyenchantments.listeners.enchantments.weapons.Axes
import me.badbones69.crazyenchantments.listeners.enchantments.weapons.Bows
import me.badbones69.crazyenchantments.listeners.enchantments.weapons.Swords
import me.badbones69.crazyenchantments.func.database.Data
import me.badbones69.crazyenchantments.func.registerListener
import me.badbones69.crazyenchantments.listeners.enchantments.AllyEnchantments
import me.badbones69.crazyenchantments.api.multisupport.Version;
import org.bukkit.attribute.Attribute
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

class CrazyEnchantments : JavaPlugin() , Listener {

    private val crazyManager = CrazyManager.getInstance()
    private val fileManager = FileManager.getInstance()

    override fun onLoad() {
        when {
            Version.isOlder(Version.v1_16_R5) -> {
                logger.warning("This version is no longer supported. Please update to 1.16.5 or newer!")
                server.pluginManager.disablePlugin(this)
            }
        }
    }

    override fun onEnable() {
        super.onEnable()
        fileManager.logInfo(false).setup(this)

        Data.load(this)

        crazyManager.load()

        CurrencyAPI.loadCurrency()

        server.onlinePlayers.forEach {
            crazyManager.loadCEPlayer(it)
            it.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = it.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue!!
        }

        getCommand("crazyenchantments")?.setExecutor(CECommand())
        getCommand("tinkerer")?.setExecutor(TinkerCommand())
        getCommand("blacksmith")?.setExecutor(BlackSmithCommand())
        getCommand("gkit")?.setExecutor(GkitzCommand())

        registerListener(this,
            ShopControl(),
            InfoGUIControl(),
            LostBookController(),
            EnchantmentControl(),
            DustControl(),
            Tinkerer(),
            AuraListener(),
            ScrollControl(),
            BlackSmith(),
            ArmorListener(),
            ProtectionCrystal(),
            Scrambler(),
            CommandChecker(),
            FireworkDamage(),
            Bows(),
            Axes(),
            Tools(),
            Hoes(),
            Helmets(),
            PickAxes(),
            Boots(),
            Armor(),
            Swords(),
            AllyEnchantments
        )

            crazyManager.cePlayers.forEach { crazyManager.backupCEPlayer(it) }
    }

    override fun onDisable() {
        super.onDisable()

        Data.save(this)

        if (crazyManager.allyManager != null) crazyManager.allyManager.forceRemoveAllies()
        server.onlinePlayers.forEach { crazyManager.unloadCEPlayer(it) }
    }

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent): Unit = with(e) {
        crazyManager.loadCEPlayer(player)
        crazyManager.updatePlayerEffects(player)
        player.getAttribute(Attribute.GENERIC_ARMOR)?.baseValue = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue!!
    }

    @EventHandler
    fun onPlayerQuit(e: PlayerQuitEvent): Unit = with(e) {
        crazyManager.unloadCEPlayer(player)
    }
}