package me.badbones69.crazyenchantments;

import me.badbones69.crazyenchantments.api.CrazyManager;
import me.badbones69.crazyenchantments.api.FileManager;
import me.badbones69.crazyenchantments.api.currencyapi.CurrencyAPI;
import me.badbones69.crazyenchantments.commands.*;
import me.badbones69.crazyenchantments.controllers.*;
import me.badbones69.crazyenchantments.enchantments.*;
import me.badbones69.crazyenchantments.func.database.Data
import me.badbones69.crazyenchantments.func.registerListener
import me.badbones69.crazyenchantments.multisupport.Version;
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

        Data(this).load()

        crazyManager.load()

        CurrencyAPI.loadCurrency()

        server.onlinePlayers.forEach {
            crazyManager.loadCEPlayer(it)
            //if (!health) return
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
            AllyEnchantments)

            crazyManager.cePlayers.forEach { crazyManager.backupCEPlayer(it) }
    }

    override fun onDisable() {
        super.onDisable()

        Data(this).save()

        if (crazyManager.allyManager != null) crazyManager.allyManager.forceRemoveAllies()
        server.onlinePlayers.forEach { crazyManager.unloadCEPlayer(it) }
    }

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent): Unit = with(e) {
        crazyManager.loadCEPlayer(player)
        crazyManager.updatePlayerEffects(player)
        //if (!health) return
        player.getAttribute(Attribute.GENERIC_ARMOR)?.baseValue = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue!!
    }

    @EventHandler
    fun onPlayerQuit(e: PlayerQuitEvent): Unit = with(e) {
        crazyManager.unloadCEPlayer(player)
    }
}