package me.badbones69.crazyenchantments;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.badbones69.crazyenchantments.api.CrazyManager;
import me.badbones69.crazyenchantments.api.FileManager.Files;
import me.badbones69.crazyenchantments.api.enums.Messages;
import me.badbones69.crazyenchantments.api.objects.ItemBuilder;
import me.badbones69.crazyenchantments.controllers.FireworkDamage;
import me.badbones69.crazyenchantments.multisupport.Support;
import me.badbones69.crazyenchantments.multisupport.Version;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Methods {
    
    private static Random random = new Random();
    private static CrazyManager ce = CrazyManager.getInstance();
    private static Support support = Support.getInstance();
    public final static Pattern HEX_PATTERN = Pattern.compile("#[a-fA-F0-9]{6}");
    
    public static String color(String message) {
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuilder buffer = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(buffer, net.md_5.bungee.api.ChatColor.of(matcher.group()).toString());
        }
        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }
    
    public static String removeColor(String msg) {
        return ChatColor.stripColor(msg);
    }
    
    public static int getRandomNumber(String range) {
        int number = 1;
        String[] split = range.split("-");
        if (isInt(split[0]) && isInt(split[1])) {
            int max = Integer.parseInt(split[1]) + 1;
            int min = Integer.parseInt(split[0]);
            number = min + random.nextInt(max - min);
        }
        return number;
    }
    
    public static boolean hasPermission(CommandSender sender, String perm, boolean toggle) {
        if (sender instanceof Player) {
            return hasPermission((Player) sender, perm, toggle);
        } else {
            return true;
        }
    }
    
    public static boolean hasPermission(Player player, String perm, boolean toggle) {
        if (player.hasPermission("crazyenchantments." + perm) || player.hasPermission("crazyenchantments.admin")) {
            return true;
        } else {
            if (toggle) {
                player.sendMessage(Messages.NO_PERMISSION.getMessage());
            }
            return false;
        }
    }
    
    public static ItemStack addGlow(ItemStack item) {
        return addGlow(item, true);
    }
    
    public static ItemStack addGlow(ItemStack item, boolean toggle) {
        ItemStack it = item.clone();
        try {
            if (toggle) {
                if (item.hasItemMeta()) {
                    if (item.getItemMeta().hasEnchants()) {
                        return item;
                    }
                }
                item.addUnsafeEnchantment(Enchantment.LUCK, 1);
                ItemMeta meta = item.getItemMeta();
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                item.setItemMeta(meta);
            }
            return item;
        } catch (NoClassDefFoundError e) {
            return it;
        }
    }
    
    public static ItemStack getItemInHand(Player player) {
        return player.getInventory().getItemInMainHand();
    }
    
    public static void setItemInHand(Player player, ItemStack item) {
        player.getInventory().setItemInMainHand(item);
    }
    
    public static String getPrefix() {
        return getPrefix("");
    }
    
    public static String getPrefix(String string) {
        return color(Files.CONFIG.getFile().getString("Settings.Prefix") + string);
    }
    
    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
    
    public static Player getPlayer(String name) {
        return Bukkit.getServer().getPlayer(name);
    }
    
    public static boolean isPlayerOnline(String playerName, CommandSender sender) {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(playerName)) {
                return true;
            }
        }
        sender.sendMessage(Messages.NOT_ONLINE.getMessage());
        return false;
    }
    
    public static void removeItem(ItemStack item, Player player) {
        removeItem(item, player, 1);
    }
    
    public static void removeItem(ItemStack item, Player player, int amount) {
        try {
            boolean found = false;
            if (player.getInventory().contains(item)) {
                if (item.getAmount() <= amount) {
                    player.getInventory().removeItem(item);
                    found = true;
                } else {
                    found = true;
                    item.setAmount(item.getAmount() - amount);
                }
            }
            if (!found) {
                ItemStack offHand = player.getEquipment().getItemInOffHand();
                if (offHand.isSimilar(item)) {
                    if ((amount - offHand.getAmount()) >= 0) {
                        player.getEquipment().setItemInOffHand(new ItemStack(Material.AIR, 1));
                    } else {
                        item.setAmount(offHand.getAmount() - amount);
                    }
                }
            }
        } catch (Exception e) {}
        player.updateInventory();
    }
    
    public static ItemStack removeItem(ItemStack item) {
        return removeItem(item, 1);
    }
    
    public static ItemStack removeItem(ItemStack item, int amount) {
        ItemStack itemStack = item.clone();
        if (item.getAmount() <= amount) {
            itemStack = new ItemStack(Material.AIR);
        } else {
            itemStack.setAmount(item.getAmount() - amount);
        }
        return itemStack;
    }
    
    public static ItemStack addLore(ItemStack item, String i) {
        ArrayList<String> lore = new ArrayList<>();
        ItemMeta m = item.getItemMeta();
        if (item.getItemMeta().hasLore()) {
            lore.addAll(item.getItemMeta().getLore());
        }
        lore.add(color(i));
        if (lore.contains(color(Files.CONFIG.getFile().getString("Settings.WhiteScroll.ProtectedName")))) {
            lore.remove(color(Files.CONFIG.getFile().getString("Settings.WhiteScroll.ProtectedName")));
            lore.add(color(Files.CONFIG.getFile().getString("Settings.WhiteScroll.ProtectedName")));
        }
        if (lore.contains(color(Files.CONFIG.getFile().getString("Settings.ProtectionCrystal.Protected")))) {
            lore.remove(color(Files.CONFIG.getFile().getString("Settings.ProtectionCrystal.Protected")));
            lore.add(color(Files.CONFIG.getFile().getString("Settings.ProtectionCrystal.Protected")));
        }
        m.setLore(lore);
        item.setItemMeta(m);
        return item;
    }
    
    public static int getPercent(String argument, ItemStack item, List<String> originalLore, int defaultValue) {
        String arg = defaultValue + "";
        for (String originalLine : originalLore) {
            originalLine = Methods.color(originalLine).toLowerCase();
            if (originalLine.contains(argument.toLowerCase())) {
                String[] b = originalLine.split(argument.toLowerCase());
                for (String itemLine : item.getItemMeta().getLore()) {
                    boolean toggle = false;// Checks to make sure the lore is the same.
                    if (b.length >= 1) {
                        if (itemLine.toLowerCase().startsWith(b[0])) {
                            arg = itemLine.toLowerCase().replace(b[0], "");
                            toggle = true;
                        }
                    }
                    if (b.length >= 2) {
                        if (itemLine.toLowerCase().endsWith(b[1])) {
                            arg = arg.toLowerCase().replace(b[1], "");
                        } else {
                            toggle = false;
                        }
                    }
                    if (toggle) {
                        break;
                    }
                }
                if (isInt(arg)) {
                    break;
                }
            }
        }
        int percent = defaultValue;
        if (isInt(arg)) {
            percent = Integer.parseInt(arg);
        }
        return percent;
    }
    
    public static boolean hasArgument(String arg, List<String> message) {
        for (String line : message) {
            line = Methods.color(line).toLowerCase();
            if (line.contains(arg.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean randomPicker(int max) {
        if (max <= 0) {
            return true;
        }
        int chance = 1 + random.nextInt(max);
        return chance == 1;
    }
    
    public static boolean randomPicker(int min, int max) {
        if (max <= min || max <= 0) {
            return true;
        }
        int chance = 1 + random.nextInt(max);
        return chance >= 1 && chance <= min;
    }
    
    public static Integer percentPick(int max, int min) {
        if (max == min) {
            return max;
        } else {
            return min + random.nextInt(max - min);
        }
    }
    
    public static boolean isInventoryFull(Player player) {
        return player.getInventory().firstEmpty() == -1;
    }
    
    public static List<LivingEntity> getNearbyLivingEntities(Location loc, double radius, Entity entity) {
        List<Entity> out = entity.getNearbyEntities(radius, radius, radius);
        List<LivingEntity> entities = new ArrayList<>();
        for (Entity en : out) {
            if (en instanceof LivingEntity) {
                entities.add((LivingEntity) en);
            }
        }
        return entities;
    }
    
    public static List<Entity> getNearbyEntities(Location loc, double radius, Entity entity) {
        return entity.getNearbyEntities(radius, radius, radius);
    }
    
    public static void fireWork(Location loc, List<Color> colors) {
        fireWork(loc, new ArrayList<>(colors));
    }
    
    public static void fireWork(Location loc, ArrayList<Color> colors) {
        Firework fw = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta fm = fw.getFireworkMeta();
        fm.addEffects(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE)
        .withColor(colors)
        .trail(false)
        .flicker(false)
        .build());
        fm.setPower(0);
        fw.setFireworkMeta(fm);
        FireworkDamage.addFirework(fw);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(ce.getPlugin(), fw :: detonate, 2);
    }
    
    public static Color getColor(String color) {
        return switch (color.toUpperCase()) {
            case "AQUA" -> Color.AQUA;
            case "BLACK" -> Color.BLACK;
            case "BLUE" -> Color.BLUE;
            case "FUCHSIA" -> Color.FUCHSIA;
            case "GRAY" -> Color.GRAY;
            case "GREEN" -> Color.GREEN;
            case "LIME" -> Color.LIME;
            case "MAROON" -> Color.MAROON;
            case "NAVY" -> Color.NAVY;
            case "OLIVE" -> Color.OLIVE;
            case "ORANGE" -> Color.ORANGE;
            case "PURPLE" -> Color.PURPLE;
            case "RED" -> Color.RED;
            case "SILVER" -> Color.SILVER;
            case "TEAL" -> Color.TEAL;
            case "YELLOW" -> Color.YELLOW;
            default -> Color.WHITE;
        };
    }
    
    public static String stripString(String string) {
        return string != null ? string.replace("-", "").replace("_", "").replace(" ", "") : "";
    }
    
    public static Enchantment getEnchantment(String enchantmentName) {
        try {
            HashMap<String, String> enchantments = getEnchantments();
            enchantmentName = stripString(enchantmentName);
            for (Enchantment enchantment : Enchantment.values()) {
                    if (stripString(enchantment.getKey().getKey()).equalsIgnoreCase(enchantmentName)) {
                        return enchantment;
                    }
            }
        } catch (Exception ignore) {}
        return null;
    }

    public static boolean verifyItemLore(ItemStack item) {
        return item != null && item.getItemMeta() != null && item.hasItemMeta() && item.getItemMeta().getLore() != null && item.getItemMeta().hasLore();
    }
    
    public static HashMap<String, String> getEnchantments() {
        HashMap<String, String> enchantments = new HashMap<>();
        enchantments.put("ARROW_DAMAGE", "Power");
        enchantments.put("ARROW_FIRE", "Flame");
        enchantments.put("ARROW_INFINITE", "Infinity");
        enchantments.put("ARROW_KNOCKBACK", "Punch");
        enchantments.put("DAMAGE_ALL", "Sharpness");
        enchantments.put("DAMAGE_ARTHROPODS", "Bane_Of_Arthropods");
        enchantments.put("DAMAGE_UNDEAD", "Smite");
        enchantments.put("DEPTH_STRIDER", "Depth_Strider");
        enchantments.put("DIG_SPEED", "Efficiency");
        enchantments.put("DURABILITY", "Unbreaking");
        enchantments.put("FIRE_ASPECT", "Fire_Aspect");
        enchantments.put("KNOCKBACK", "KnockBack");
        enchantments.put("LOOT_BONUS_BLOCKS", "Fortune");
        enchantments.put("LOOT_BONUS_MOBS", "Looting");
        enchantments.put("LUCK", "Luck_Of_The_Sea");
        enchantments.put("LURE", "Lure");
        enchantments.put("OXYGEN", "Respiration");
        enchantments.put("PROTECTION_ENVIRONMENTAL", "Protection");
        enchantments.put("PROTECTION_EXPLOSIONS", "Blast_Protection");
        enchantments.put("PROTECTION_FALL", "Feather_Falling");
        enchantments.put("PROTECTION_FIRE", "Fire_Protection");
        enchantments.put("PROTECTION_PROJECTILE", "Projectile_Protection");
        enchantments.put("SILK_TOUCH", "Silk_Touch");
        enchantments.put("THORNS", "Thorns");
        enchantments.put("WATER_WORKER", "Aqua_Affinity");
        enchantments.put("BINDING_CURSE", "Curse_Of_Binding");
        enchantments.put("MENDING", "Mending");
        enchantments.put("FROST_WALKER", "Frost_Walker");
        enchantments.put("VANISHING_CURSE", "Curse_Of_Vanishing");
        return enchantments;
    }
    
    public static void removeDurability(ItemStack item, Player player) {
        if (item.getType().getMaxDurability() == 0) {
            return;
        }
        if (item.hasItemMeta()) {
            try {
                if (item.getItemMeta().isUnbreakable()) {
                    return;
                }
            } catch (NoSuchMethodError ignored) {
            }
            NBTItem nbtItem = new NBTItem(item);
            if (nbtItem.hasNBTData() && nbtItem.hasKey("Unbreakable") && nbtItem.getBoolean("Unbreakable")) {
                return;
            }
            if (item.getItemMeta().hasEnchants()) {
                if (item.getItemMeta().hasEnchant(Enchantment.DURABILITY)) {
                    if (Methods.randomPicker(1, 1 + item.getEnchantmentLevel(Enchantment.DURABILITY))) {
                        if (item.getDurability() > item.getType().getMaxDurability()) {
                            player.getInventory().remove(item);
                        } else {
                            item.setDurability((short) (item.getDurability() + 1));
                        }
                    }
                    return;
                }
            }
        }
        if (item.getDurability() > item.getType().getMaxDurability()) {
            player.getInventory().remove(item);
        } else {
            item.setDurability((short) (item.getDurability() + 1));
        }
    }
    
    public static boolean isSimilar(ItemStack one, ItemStack two) {
        if (one.getType() == two.getType()) {
            if (one.hasItemMeta() && two.hasItemMeta()) {
                if (one.getItemMeta().hasDisplayName() && two.getItemMeta().hasDisplayName()) {
                    if (one.getItemMeta().getDisplayName().equalsIgnoreCase(two.getItemMeta().getDisplayName())) {
                        if (one.getItemMeta().hasLore() && two.getItemMeta().hasLore()) {
                            int i = 0;
                            for (String lore : one.getItemMeta().getLore()) {
                                if (!lore.equals(two.getItemMeta().getLore().get(i))) {
                                    return false;
                                }
                                i++;
                            }
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public static void explode(Entity player) {
        Bukkit.getLogger().info("Boom!");
        /*
        if (Version.isNewer(Version.v1_8_R3)) {
            player.getLocation().getWorld().spawnParticle(Particle.FLAME, player.getLocation(), 200);
            player.getLocation().getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 30, .4F, .5F, .4F);
            player.getLocation().getWorld().spawnParticle(Particle.EXPLOSION_HUGE, player.getLocation(), 2);
        } else {
            ParticleEffect.FLAME.display(0, 0, 0, 1, 200, player.getLocation().add(0, 1, 0), 100);
            ParticleEffect.CLOUD.display(.4F, .5F, .4F, 1, 30, player.getLocation().add(0, 1, 0), 100);
            ParticleEffect.EXPLOSION_HUGE.display(0, 0, 0, 0, 2, player.getLocation().add(0, 1, 0), 100);
        }
        player.getWorld().playSound(player.getLocation(), ce.getSound("ENTITY_GENERIC_EXPLODE", "EXPLODE"), 1, 1);
        for (Entity e : Methods.getNearbyEntitiess(player.getLocation(), 3D, player)) {
            if (support.allowsPVP(e.getLocation())) {
                if (e.getType() == EntityType.DROPPED_ITEM) {
                    e.remove();
                } else {
                    if (e instanceof LivingEntity) {
                        LivingEntity en = (LivingEntity) e;
                        if (!support.isFriendly(player, en)) {
                            if (!player.getName().equalsIgnoreCase(e.getName())) {
                                en.damage(5D);
                                if (en instanceof Player) {
                                    if (SupportedPlugins.SPARTAN.isPluginLoaded()) {
                                        //SpartanSupport.cancelSpeed((Player) player);
                                        //SpartanSupport.cancelFly((Player) player);
                                        //SpartanSupport.cancelClip((Player) player);
                                        //SpartanSupport.cancelNormalMovements((Player) player);
                                        //SpartanSupport.cancelNoFall((Player) player);
                                        //SpartanSupport.cancelJesus((Player) player);
                                    }
                                }
                                en.setVelocity(en.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(1).setY(.5));
                            }
                        }
                    }
                }
            }
        }
        */
    }
    
    public static void explode(Entity player, Entity arrow) {
        Bukkit.getLogger().info("Boom!");
        /*
        if (Version.isNewer(Version.v1_8_R3)) {
            arrow.getLocation().getWorld().spawnParticle(Particle.FLAME, arrow.getLocation(), 200);
            arrow.getLocation().getWorld().spawnParticle(Particle.CLOUD, arrow.getLocation(), 30, .4F, .5F, .4F);
            arrow.getLocation().getWorld().spawnParticle(Particle.EXPLOSION_HUGE, arrow.getLocation(), 2);
        } else {
            ParticleEffect.FLAME.display(0, 0, 0, 1, 200, arrow.getLocation().add(0, 1, 0), 100);
            ParticleEffect.CLOUD.display(.4F, .5F, .4F, 1, 30, arrow.getLocation().add(0, 1, 0), 100);
            ParticleEffect.EXPLOSION_HUGE.display(0, 0, 0, 0, 2, arrow.getLocation().add(0, 1, 0), 100);
        }
        player.getWorld().playSound(player.getLocation(), ce.getSound("ENTITY_GENERIC_EXPLODE", "EXPLODE"), 1, 1);
        for (Entity e : Methods.getNearbyEntitiess(arrow.getLocation(), 3D, arrow)) {
            if (support.allowsPVP(e.getLocation())) {
                if (e.getType() == EntityType.DROPPED_ITEM) {
                    e.remove();
                } else {
                    if (e instanceof LivingEntity) {
                        LivingEntity en = (LivingEntity) e;
                        if (!support.isFriendly(player, en)) {
                            if (!player.getName().equalsIgnoreCase(e.getName())) {
                                en.damage(5D);
                                if (en instanceof Player) {
                                    if (SupportedPlugins.SPARTAN.isPluginLoaded()) {
                                        //SpartanSupport.cancelSpeed((Player) player);
                                        //SpartanSupport.cancelFly((Player) player);
                                        //SpartanSupport.cancelClip((Player) player);
                                        //SpartanSupport.cancelNormalMovements((Player) player);
                                        //SpartanSupport.cancelNoFall((Player) player);
                                        //SpartanSupport.cancelJesus((Player) player);
                                    }
                                }
                                en.setVelocity(en.getLocation().toVector().subtract(arrow.getLocation().toVector()).normalize().multiply(1).setY(.5));
                            }
                        }
                    }
                }
            }
        }
        */
    }
    
    public static ItemBuilder getRandomPaneColor() {
        List<String> colors = Arrays.asList(
        "WHITE_STAINED_GLASS_PANE",// 0
        "ORANGE_STAINED_GLASS_PANE",// 1
        "MAGENTA_STAINED_GLASS_PANE",// 2
        "LIGHT_BLUE_STAINED_GLASS_PANE",// 3
        "YELLOW_STAINED_GLASS_PANE",// 4
        "LIME_STAINED_GLASS_PANE",// 5
        "PINK_STAINED_GLASS_PANE",// 6
        "GRAY_STAINED_GLASS_PANE",// 7
        //Skipped 8 due to it being basically invisible in a GUI.
        "CYAN_STAINED_GLASS_PANE",// 9
        "PURPLE_STAINED_GLASS_PANE",// 10
        "BLUE_STAINED_GLASS_PANE",// 11
        "BROWN_STAINED_GLASS_PANE",// 12
        "GREEN_STAINED_GLASS_PANE",// 13
        "RED_STAINED_GLASS_PANE",// 14
        "BLACK_STAINED_GLASS_PANE");// 15
        return new ItemBuilder().setMaterial(colors.get(random.nextInt(colors.size())));
    }
}