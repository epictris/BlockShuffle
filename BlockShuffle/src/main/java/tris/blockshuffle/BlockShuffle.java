package tris.blockshuffle;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

// Stores data for each player
class PlayerInfo {
    Player player; // The Player object
    Material block; // The block assigned to the player
    Boolean found; // Whether the player has found their block
    Boolean voted = false; // Whether the player has voted
    int commandCooldown = 0;
}

// Stores block material and name
class BlockInfo {
    Material block; // The reference id of the block
    String name; // The in-game name of the block
    BlockInfo(Material block, String name) {
        this.block = block;
        this.name = name;
    }
}

public final class BlockShuffle extends JavaPlugin implements Listener, CommandExecutor {

    // Global variables
    boolean playing = false; // whether the game is currently running
    HashMap<String, PlayerInfo> alivePlayers = new HashMap<String, PlayerInfo>(); // HashMap to store alive players
    HashMap<String, PlayerInfo> deadPlayers = new HashMap<String, PlayerInfo>(); // HashMap to store dead players
    int roundTime = 300; // length of each round in seconds
    int timeRemaining = roundTime; // number of seconds remaining in a given round
    boolean allPlayersSuccessful = false; // whether all alivePlayers have found their assigned block during a round
    int roundCount = 1;
    ArrayList<ArrayList> voteOptions = new ArrayList<ArrayList>(); // stores vote options
    ArrayList<Integer> playerVotes = new ArrayList<Integer>(); // stores player votes
    ArrayList<BlockInfo> easyBlocks = new ArrayList<BlockInfo>() {{ // Stores an ArrayList of all easy blocks
        add(new BlockInfo(Material.ACACIA_DOOR, "Acacia Door"));
        add(new BlockInfo(Material.ACACIA_FENCE, "Acacia Fence"));
        add(new BlockInfo(Material.ACACIA_FENCE_GATE, "Acacia Fence Gate"));
        add(new BlockInfo(Material.ACACIA_LEAVES, "Acacia Leaves"));
        add(new BlockInfo(Material.ACACIA_LOG, "Acacia Log"));
        add(new BlockInfo(Material.ACACIA_PLANKS, "Acacia Planks"));
        add(new BlockInfo(Material.ACACIA_PRESSURE_PLATE, "Acacia Pressure Plate"));
        add(new BlockInfo(Material.ACACIA_SIGN, "Acacia Sign"));
        add(new BlockInfo(Material.ACACIA_SLAB, "Acacia Slab"));
        add(new BlockInfo(Material.ACACIA_STAIRS, "Acacia Stairs"));
        add(new BlockInfo(Material.ACACIA_TRAPDOOR, "Acacia Trapdoor"));
        add(new BlockInfo(Material.ACTIVATOR_RAIL, "Activator Rail"));
        add(new BlockInfo(Material.ANDESITE, "Andesite"));
        add(new BlockInfo(Material.ANDESITE_SLAB, "Andesite Slab"));
        add(new BlockInfo(Material.ANDESITE_STAIRS, "Andesite Stairs"));
        add(new BlockInfo(Material.ANDESITE_WALL, "Andesite Wall"));
        add(new BlockInfo(Material.BARREL, "Barrel"));
        add(new BlockInfo(Material.BEDROCK, "Bedrock"));
        add(new BlockInfo(Material.BIRCH_DOOR, "Birch Door"));
        add(new BlockInfo(Material.BIRCH_FENCE, "Birch Fence"));
        add(new BlockInfo(Material.BIRCH_FENCE_GATE, "Birch Fence Gate"));
        add(new BlockInfo(Material.BIRCH_LEAVES, "Birch Leaves"));
        add(new BlockInfo(Material.BIRCH_LOG, "Birch Log"));
        add(new BlockInfo(Material.BIRCH_PLANKS, "Birch Planks"));
        add(new BlockInfo(Material.BIRCH_PRESSURE_PLATE, "Birch Pressure Plate"));
        add(new BlockInfo(Material.BIRCH_SLAB, "Birch Slab"));
        add(new BlockInfo(Material.BIRCH_STAIRS, "Birch Stairs"));
        add(new BlockInfo(Material.BIRCH_TRAPDOOR, "Birch Trapdoor"));
        add(new BlockInfo(Material.BLAST_FURNACE, "Blast Furnace"));
        add(new BlockInfo(Material.COAL_BLOCK, "Block of Coal"));
        add(new BlockInfo(Material.IRON_BLOCK, "Block of Iron"));
        add(new BlockInfo(Material.REDSTONE_BLOCK, "Block of Redstone"));
        add(new BlockInfo(Material.BONE_BLOCK, "Bone Block"));
        add(new BlockInfo(Material.BOOKSHELF, "Bookshelf"));
        add(new BlockInfo(Material.BRICK_SLAB, "Brick Slab"));
        add(new BlockInfo(Material.BRICK_STAIRS, "Brick Stairs"));
        add(new BlockInfo(Material.BRICK_WALL, "Brick Wall"));
        add(new BlockInfo(Material.BRICK, "Bricks"));
        add(new BlockInfo(Material.CACTUS, "Cactus"));
        add(new BlockInfo(Material.CAMPFIRE, "Campfire"));
        add(new BlockInfo(Material.CARTOGRAPHY_TABLE, "Cartography Table"));
        add(new BlockInfo(Material.CAULDRON, "Cauldron"));
        add(new BlockInfo(Material.CHEST, "Chest"));
        add(new BlockInfo(Material.CHISELED_SANDSTONE, "Chiseled Sandstone"));
        add(new BlockInfo(Material.CHISELED_STONE_BRICKS, "Chiseled Stone Bricks"));
        add(new BlockInfo(Material.CLAY, "Clay"));
        add(new BlockInfo(Material.COAL_ORE, "Coal Ore"));
        add(new BlockInfo(Material.COARSE_DIRT, "Coarse Dirt"));
        add(new BlockInfo(Material.COBBLESTONE, "Cobblestone"));
        add(new BlockInfo(Material.COBBLESTONE_SLAB, "Cobblestone Slab"));
        add(new BlockInfo(Material.COBBLESTONE_STAIRS, "Cobblestone Stairs"));
        add(new BlockInfo(Material.COBBLESTONE_STAIRS, "Cobblestone Wall"));
        add(new BlockInfo(Material.COMPOSTER, "Composter"));
        add(new BlockInfo(Material.CRACKED_STONE_BRICKS, "Cracked Stone Bricks"));
        add(new BlockInfo(Material.CRAFTING_TABLE, "Crafting Table"));
        add(new BlockInfo(Material.CUT_SANDSTONE, "Cut Sandstone"));
        add(new BlockInfo(Material.CUT_SANDSTONE_SLAB, "Cut Sandstone Slab"));
        add(new BlockInfo(Material.DARK_OAK_DOOR, "Dark Oak Door"));
        add(new BlockInfo(Material.DARK_OAK_FENCE, "Dark Oak Fence"));
        add(new BlockInfo(Material.DARK_OAK_FENCE_GATE, "Dark Oak Fence Gate"));
        add(new BlockInfo(Material.DARK_OAK_LEAVES, "Dark Oak Leaves"));
        add(new BlockInfo(Material.DARK_OAK_LOG, "Dark Oak Log"));
        add(new BlockInfo(Material.DARK_OAK_PLANKS, "Dark Oak Planks"));
        add(new BlockInfo(Material.DARK_OAK_PRESSURE_PLATE, "Dark Oak Pressure Plate"));
        add(new BlockInfo(Material.DARK_OAK_SLAB, "Dark Oak Slab"));
        add(new BlockInfo(Material.DARK_OAK_STAIRS, "Dark Oak Stairs"));
        add(new BlockInfo(Material.DARK_OAK_TRAPDOOR, "Dark Oak Trapdoor"));
        add(new BlockInfo(Material.ICE, "Ice"));
        add(new BlockInfo(Material.IRON_BLOCK, "Iron Bars"));
        add(new BlockInfo(Material.IRON_DOOR, "Iron Door"));
        add(new BlockInfo(Material.IRON_ORE, "Iron Ore"));
        add(new BlockInfo(Material.IRON_TRAPDOOR, "Iron Trapdoor"));
        add(new BlockInfo(Material.LADDER, "Ladder"));
        add(new BlockInfo(Material.LANTERN, "Lantern"));
        add(new BlockInfo(Material.LAVA, "Lava"));
        add(new BlockInfo(Material.LEVER, "Lever"));
        add(new BlockInfo(Material.LILY_PAD, "Lily Pad"));
        add(new BlockInfo(Material.LOOM, "Loom"));
        add(new BlockInfo(Material.MAGMA_BLOCK, "Magma Block"));
        add(new BlockInfo(Material.MOVING_PISTON, "Moving Piston"));
        add(new BlockInfo(Material.NOTE_BLOCK, "Note Block"));
        add(new BlockInfo(Material.OAK_DOOR, "Oak Door"));
        add(new BlockInfo(Material.OAK_FENCE, "Oak Fence"));
        add(new BlockInfo(Material.OAK_FENCE_GATE, "Oak Fence Gate"));
        add(new BlockInfo(Material.OAK_LEAVES, "Oak Leaves"));
        add(new BlockInfo(Material.OAK_LOG, "Oak Log"));
        add(new BlockInfo(Material.OAK_PLANKS, "Oak Planks"));
        add(new BlockInfo(Material.OAK_PRESSURE_PLATE, "Oak Pressure Plate"));
        add(new BlockInfo(Material.OAK_SLAB, "Oak Slab"));
        add(new BlockInfo(Material.OAK_STAIRS, "Oak Stairs"));
        add(new BlockInfo(Material.OAK_TRAPDOOR, "Oak Trapdoor"));
        add(new BlockInfo(Material.PISTON, "Piston"));
        add(new BlockInfo(Material.PISTON_HEAD, "Piston Head"));
        add(new BlockInfo(Material.POLISHED_ANDESITE, "Polished Andesite"));
        add(new BlockInfo(Material.POLISHED_ANDESITE_SLAB, "Polished Andesite Slab"));
        add(new BlockInfo(Material.POLISHED_ANDESITE_STAIRS, "Polished Andesite Stairs"));
        add(new BlockInfo(Material.POLISHED_DIORITE, "Polished Diorite"));
        add(new BlockInfo(Material.POLISHED_DIORITE_SLAB, "Polished Diorite Slab"));
        add(new BlockInfo(Material.POLISHED_DIORITE_STAIRS, "Polished Diorite Stairs"));
        add(new BlockInfo(Material.POLISHED_GRANITE, "Polished Granite"));
        add(new BlockInfo(Material.POLISHED_GRANITE_SLAB, "Polished Granite Slab"));
        add(new BlockInfo(Material.POLISHED_GRANITE_STAIRS, "Polished Granite Stairs"));
        add(new BlockInfo(Material.RAIL, "Rail"));
        add(new BlockInfo(Material.RED_MUSHROOM, "Red Mushroom"));
        add(new BlockInfo(Material.REDSTONE_ORE, "Redstone Ore"));
        add(new BlockInfo(Material.REPEATER, "Redstone Repeater"));
        add(new BlockInfo(Material.SAND, "Sand"));
        add(new BlockInfo(Material.SANDSTONE, "Sandstone"));
        add(new BlockInfo(Material.SANDSTONE_SLAB, "Sandstone Slab"));
        add(new BlockInfo(Material.SANDSTONE_STAIRS, "Sandstone Stairs"));
        add(new BlockInfo(Material.SANDSTONE_WALL, "Sandstone Wall"));
        add(new BlockInfo(Material.SEA_LANTERN, "Sea Lantern"));
        add(new BlockInfo(Material.SMITHING_TABLE, "Smithing Table"));
        add(new BlockInfo(Material.SMOKER, "Smoker"));
        add(new BlockInfo(Material.SMOOTH_SANDSTONE, "Smooth Sandstone"));
        add(new BlockInfo(Material.SMOOTH_SANDSTONE_SLAB, "Smooth Sandstone Slab"));
        add(new BlockInfo(Material.SMOOTH_SANDSTONE_STAIRS, "Smooth Sandstone Stairs"));
        add(new BlockInfo(Material.SMOOTH_STONE, "Smooth Stone"));
        add(new BlockInfo(Material.SMOOTH_STONE_SLAB, "Smooth Stone Slab"));
        add(new BlockInfo(Material.SPRUCE_DOOR, "Spruce Door"));
        add(new BlockInfo(Material.SPRUCE_FENCE, "Spruce Fence"));
        add(new BlockInfo(Material.SPRUCE_FENCE_GATE, "Spruce Fence Gate"));
        add(new BlockInfo(Material.SPRUCE_LEAVES, "Spruce Leaves"));
        add(new BlockInfo(Material.SPRUCE_LOG, "Spruce Log"));
        add(new BlockInfo(Material.SPRUCE_PLANKS, "Spruce Planks"));
        add(new BlockInfo(Material.SPRUCE_PRESSURE_PLATE, "Spruce Pressure Plate"));
        add(new BlockInfo(Material.SPRUCE_SLAB, "Spruce Slab"));
        add(new BlockInfo(Material.SPRUCE_STAIRS, "Spruce Stairs"));
        add(new BlockInfo(Material.SPRUCE_TRAPDOOR, "Spruce Trapdoor"));
        add(new BlockInfo(Material.STONE, "Stone"));
        add(new BlockInfo(Material.STONE_BRICK_SLAB, "Stone Brick Slab"));
        add(new BlockInfo(Material.STONE_BRICK_STAIRS, "Stone Brick Stairs"));
        add(new BlockInfo(Material.STONE_BRICK_WALL, "Stone Brick Wall"));
        add(new BlockInfo(Material.STONE_BRICKS, "Stone Bricks"));
        add(new BlockInfo(Material.STONE_PRESSURE_PLATE, "Stone Pressure Plate"));
        add(new BlockInfo(Material.STONE_SLAB, "Stone Slab"));
        add(new BlockInfo(Material.STONE_STAIRS, "Stone Stairs"));
        add(new BlockInfo(Material.STONECUTTER, "Stonecutter"));
        add(new BlockInfo(Material.STRIPPED_ACACIA_LOG, "Stripped Acacia Log"));
        add(new BlockInfo(Material.STRIPPED_BIRCH_LOG, "Stripped Birch Log"));
        add(new BlockInfo(Material.STRIPPED_DARK_OAK_LOG, "Stripped Dark Oak Log"));
        add(new BlockInfo(Material.STRIPPED_OAK_LOG, "Stripped Oak Log"));
        add(new BlockInfo(Material.STRIPPED_SPRUCE_LOG, "Stripped Spruce Log"));
        add(new BlockInfo(Material.TRAPPED_CHEST, "Trapped Chest"));
        add(new BlockInfo(Material.WATER, "Water"));
        add(new BlockInfo(Material.WHITE_BED, "White Bed"));
        add(new BlockInfo(Material.WHITE_CONCRETE, "White Concrete"));
        add(new BlockInfo(Material.WHITE_CONCRETE_POWDER, "White Concrete Powder"));
        add(new BlockInfo(Material.WHITE_WOOL, "White Wool"));
        add(new BlockInfo(Material.DETECTOR_RAIL, "Detector Rail"));
        add(new BlockInfo(Material.DIORITE, "Diorite"));
        add(new BlockInfo(Material.DIORITE_SLAB, "Diorite Slab"));
        add(new BlockInfo(Material.DIORITE_STAIRS, "Diorite Stairs"));
        add(new BlockInfo(Material.DIORITE_WALL, "Diorite Wall"));
        add(new BlockInfo(Material.DIRT, "Dirt"));
        add(new BlockInfo(Material.DISPENSER, "Dispenser"));
        add(new BlockInfo(Material.DRIED_KELP_BLOCK, "Dried Kelp Block"));
        add(new BlockInfo(Material.DROPPER, "Dropper"));
        add(new BlockInfo(Material.FARMLAND, "Farmland"));
        add(new BlockInfo(Material.FLETCHING_TABLE, "Fletching Table"));
        add(new BlockInfo(Material.FLOWER_POT, "Flower Pot"));
        add(new BlockInfo(Material.FURNACE, "Furnace"));
        add(new BlockInfo(Material.GLASS, "Glass"));
        add(new BlockInfo(Material.GLASS_PANE, "Glass Pane"));
        add(new BlockInfo(Material.GRANITE, "Granite"));
        add(new BlockInfo(Material.GRANITE_SLAB, "Granite Slab"));
        add(new BlockInfo(Material.GRANITE_STAIRS, "Granite Stairs"));
        add(new BlockInfo(Material.GRANITE_WALL, "Granite Wall"));
        add(new BlockInfo(Material.GRASS_BLOCK, "Grass Block"));
        add(new BlockInfo(Material.GRASS_PATH, "Grass Path"));
        add(new BlockInfo(Material.GRAVEL, "Gravel"));
        add(new BlockInfo(Material.GRINDSTONE, "Grindstone"));
        add(new BlockInfo(Material.HEAVY_WEIGHTED_PRESSURE_PLATE, "Heavy Weighted Pressure Plate"));
        add(new BlockInfo(Material.HOPPER, "Hopper"));
    }};
    ArrayList<BlockInfo> mediumBlocks = new ArrayList<BlockInfo>() {{ // Stores an ArrayList of all medium blocks
        add(new BlockInfo(Material.HORN_CORAL_BLOCK, "Horn Coral Block"));
        add(new BlockInfo(Material.HAY_BLOCK, "Hay Bale"));
        add(new BlockInfo(Material.GOLD_ORE, "Gold Ore"));
        add(new BlockInfo(Material.FIRE_CORAL_BLOCK, "Fire Coral Block"));
        add(new BlockInfo(Material.EMERALD_ORE, "Emerald Ore"));
        add(new BlockInfo(Material.CUT_RED_SANDSTONE, "Cut Red Sandstone"));
        add(new BlockInfo(Material.COBWEB, "Cobweb"));
        add(new BlockInfo(Material.COCOA, "Cocoa"));
        add(new BlockInfo(Material.ANVIL, "Anvil"));
        add(new BlockInfo(Material.BAMBOO, "Bamboo"));
        add(new BlockInfo(Material.BEE_NEST, "Bee Nest"));
        add(new BlockInfo(Material.BELL, "Bell"));
        add(new BlockInfo(Material.DIAMOND_ORE, "Diamond Ore"));
        add(new BlockInfo(Material.BRAIN_CORAL_BLOCK, "Brain Coral Block"));
        add(new BlockInfo(Material.BUBBLE_CORAL_BLOCK, "Bubble Coral Block"));
        add(new BlockInfo(Material.CARVED_PUMPKIN, "Carved Pumpkin"));
        add(new BlockInfo(Material.CHIPPED_ANVIL, "Chipped Anvil"));
        add(new BlockInfo(Material.CHISELED_RED_SANDSTONE, "Chiseled Red Sandstone"));
        add(new BlockInfo(Material.CUT_RED_SANDSTONE_SLAB, "Cut Red Sandstone Slab"));
        add(new BlockInfo(Material.DAMAGED_ANVIL, "Damaged Anvil"));
        add(new BlockInfo(Material.DARK_PRISMARINE, "Dark Prismarine"));
        add(new BlockInfo(Material.DARK_PRISMARINE_SLAB, "Dark Prismarine Slab"));
        add(new BlockInfo(Material.DARK_PRISMARINE_STAIRS, "Dark Prismarine Stairs"));
        add(new BlockInfo(Material.DEAD_BRAIN_CORAL_BLOCK, "Dead Brain Coral Block"));
        add(new BlockInfo(Material.DEAD_BUBBLE_CORAL_BLOCK, "Dead Bubble Coral Block"));
        add(new BlockInfo(Material.DEAD_FIRE_CORAL_BLOCK, "Dead Fire Coral Block"));
        add(new BlockInfo(Material.DEAD_HORN_CORAL_BLOCK, "Dead Horn Coral Block"));
        add(new BlockInfo(Material.DEAD_TUBE_CORAL_BLOCK, "Dead Tube Coral Block"));
        add(new BlockInfo(Material.JACK_O_LANTERN, "Jack o'Lantern"));
        add(new BlockInfo(Material.JUKEBOX, "Jukebox"));
        add(new BlockInfo(Material.JUNGLE_DOOR, "Jungle Door"));
        add(new BlockInfo(Material.JUNGLE_FENCE, "Jungle Fence"));
        add(new BlockInfo(Material.JUNGLE_FENCE_GATE, "Jungle Fence Gate"));
        add(new BlockInfo(Material.JUNGLE_LEAVES, "Jungle Leaves"));
        add(new BlockInfo(Material.JUNGLE_LOG, "Jungle Log"));
        add(new BlockInfo(Material.JUNGLE_PLANKS, "Jungle Planks"));
        add(new BlockInfo(Material.JUNGLE_PRESSURE_PLATE, "Jungle Pressure Plate"));
        add(new BlockInfo(Material.JUNGLE_SLAB, "Jungle Slab"));
        add(new BlockInfo(Material.JUNGLE_STAIRS, "Jungle Stairs"));
        add(new BlockInfo(Material.JUNGLE_TRAPDOOR, "Jungle Trapdoor"));
        add(new BlockInfo(Material.MELON, "Melon"));
        add(new BlockInfo(Material.MOSSY_COBBLESTONE, "Mossy Cobblestone"));
        add(new BlockInfo(Material.MOSSY_COBBLESTONE_SLAB, "Mossy Cobblestone Slab"));
        add(new BlockInfo(Material.MOSSY_COBBLESTONE_STAIRS, "Mossy Cobblestone Stairs"));
        add(new BlockInfo(Material.MOSSY_COBBLESTONE_WALL, "Mossy Cobblestone Wall"));
        add(new BlockInfo(Material.MOSSY_STONE_BRICK_SLAB, "Mossy Stone Brick Slab"));
        add(new BlockInfo(Material.MOSSY_STONE_BRICK_STAIRS, "Mossy Stone Brick Stairs"));
        add(new BlockInfo(Material.MOSSY_STONE_BRICK_WALL, "Mossy Stone Brick Wall"));
        add(new BlockInfo(Material.MOSSY_STONE_BRICKS, "Mossy Stone Bricks"));
        add(new BlockInfo(Material.MUSHROOM_STEM, "Mushroom Stem"));
        add(new BlockInfo(Material.MYCELIUM, "Mycelium"));
        add(new BlockInfo(Material.NETHERRACK, "Netherrack"));
        add(new BlockInfo(Material.PACKED_ICE, "Packed Ice"));
        add(new BlockInfo(Material.PODZOL, "Podzol"));
        add(new BlockInfo(Material.POWERED_RAIL, "Powered Rail"));
        add(new BlockInfo(Material.PRISMARINE, "Prismarine"));
        add(new BlockInfo(Material.PRISMARINE_BRICK_SLAB, "Prismarine Brick Slab"));
        add(new BlockInfo(Material.PRISMARINE_BRICK_STAIRS, "Prismarine Brick Stairs"));
        add(new BlockInfo(Material.PRISMARINE_BRICKS, "Prismarine Bricks"));
        add(new BlockInfo(Material.PRISMARINE_SLAB, "Prismarine Slab"));
        add(new BlockInfo(Material.PRISMARINE_STAIRS, "Prismarine Stairs"));
        add(new BlockInfo(Material.PRISMARINE_WALL, "Prismarine Wall"));
        add(new BlockInfo(Material.RED_MUSHROOM_BLOCK, "Red Mushroom Block"));
        add(new BlockInfo(Material.RED_SAND, "Red Sand"));
        add(new BlockInfo(Material.RED_SANDSTONE, "Red Sandstone"));
        add(new BlockInfo(Material.RED_SANDSTONE_SLAB, "Red Sandstone Slab"));
        add(new BlockInfo(Material.RED_SANDSTONE_STAIRS, "Red Sandstone Stairs"));
        add(new BlockInfo(Material.RED_SANDSTONE_WALL, "Red Sandstone Wall"));
        add(new BlockInfo(Material.SCAFFOLDING, "Scaffolding"));
        add(new BlockInfo(Material.SEA_PICKLE, "Sea Pickle"));
        add(new BlockInfo(Material.SMOOTH_RED_SANDSTONE, "Smooth Red Sandstone"));
        add(new BlockInfo(Material.SMOOTH_RED_SANDSTONE_SLAB, "Smooth Red Sandstone Slab"));
        add(new BlockInfo(Material.STRIPPED_JUNGLE_LOG, "Stripped Jungle Log"));
        add(new BlockInfo(Material.SMOOTH_RED_SANDSTONE_STAIRS, "Smooth Red Sandstone Stairs"));
        add(new BlockInfo(Material.LIGHT_WEIGHTED_PRESSURE_PLATE, "Light Weighted Pressure Plate"));
        add(new BlockInfo(Material.TERRACOTTA, "Terracotta"));
        add(new BlockInfo(Material.LAPIS_BLOCK, "Lapis Lazuli Block"));
        add(new BlockInfo(Material.LAPIS_ORE, "Lapis Lazuli Ore"));
        add(new BlockInfo(Material.LECTERN, "Lectern"));
        add(new BlockInfo(Material.SNOW, "Snow"));
        add(new BlockInfo(Material.SNOW_BLOCK, "Snow Block"));
        add(new BlockInfo(Material.PUMPKIN, "Pumpkin"));
        add(new BlockInfo(Material.TNT, "TNT"));
    }};
    ArrayList<BlockInfo> hardBlocks = new ArrayList<BlockInfo>() {{ // Stores an ArrayList of all hard blocks
        add(new BlockInfo(Material.SOUL_SAND, "Soul Sand"));
        add(new BlockInfo(Material.SMOOTH_QUARTZ, "Smooth Quartz"));
        add(new BlockInfo(Material.SMOOTH_QUARTZ_SLAB, "Smooth Quartz Slab"));
        add(new BlockInfo(Material.SMOOTH_QUARTZ_STAIRS, "Smooth Quartz Stairs"));
        add(new BlockInfo(Material.STICK, "Sticky Piston"));
        add(new BlockInfo(Material.SLIME_BLOCK, "Slime Block"));
        add(new BlockInfo(Material.COMPARATOR, "Redstone Comparator"));
        add(new BlockInfo(Material.REDSTONE_LAMP, "Redstone Lamp"));
        add(new BlockInfo(Material.QUARTZ_PILLAR, "Quartz Pillar"));
        add(new BlockInfo(Material.QUARTZ_SLAB, "Quartz Slab"));
        add(new BlockInfo(Material.QUARTZ_STAIRS, "Quartz Stairs"));
        add(new BlockInfo(Material.OBSERVER, "Observer"));
        add(new BlockInfo(Material.NETHER_BRICK_FENCE, "Nether Brick Fence"));
        add(new BlockInfo(Material.NETHER_BRICK_SLAB, "Nether Brick Slab"));
        add(new BlockInfo(Material.NETHER_BRICK_STAIRS, "Nether Brick Stairs"));
        add(new BlockInfo(Material.NETHER_BRICK_WALL, "Nether Brick Wall"));
        add(new BlockInfo(Material.NETHER_BRICK, "Nether Bricks"));
        add(new BlockInfo(Material.NETHER_QUARTZ_ORE, "Nether Quartz Ore"));
        add(new BlockInfo(Material.GLOWSTONE, "Glowstone"));
        add(new BlockInfo(Material.ENCHANTING_TABLE, "Enchanting Table"));
        add(new BlockInfo(Material.DAYLIGHT_DETECTOR, "Daylight Detector"));
        add(new BlockInfo(Material.CHISELED_QUARTZ_BLOCK, "Chiseled Quartz Block"));
        add(new BlockInfo(Material.CAKE, "Cake"));
        add(new BlockInfo(Material.BREWING_STAND, "Brewing Stand"));
        add(new BlockInfo(Material.BLUE_ICE, "Blue Ice"));
        add(new BlockInfo(Material.QUARTZ_BLOCK, "Block of Quartz"));
        add(new BlockInfo(Material.DIAMOND_BLOCK, "Block of Diamond"));
        add(new BlockInfo(Material.EMERALD_BLOCK, "Block of Emerald"));
        add(new BlockInfo(Material.GOLD_BLOCK, "Block of Gold"));
        add(new BlockInfo(Material.OBSIDIAN, "Obsidian"));
    }};
    ArrayList<BlockInfo> allBlocks = new ArrayList<BlockInfo>() {{ // Stores an ArrayList of all blocks
        addAll(easyBlocks);
        addAll(mediumBlocks);
        addAll(hardBlocks);
    }};

    // Runs when plugin is enabled (when server starts)
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this); // Registers this plugin to handle events
    }

    // Runs every time a player moves
    @EventHandler
    public void stepOnBlock(PlayerMoveEvent e) {
        if (playing) {
            Player p = e.getPlayer(); // Gets the player who moved

            // Runs only if the player has not found their assigned block yet
            if (alivePlayers.get(p.getName()).found == false) {

                // Runs if the block below the player is the block they were assigned
                if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == alivePlayers.get(p.getName()).block) {
                    alivePlayers.get(p.getName()).found = true; // Sets the found variable in the associated PlayerInfo object to true
                    allPlayersSuccessful = true; // Temporarily sets allPlayersSuccessful to true (this will be reset to false if not all alivePlayers have actually found their block)

                    // Loops through all active alivePlayers
                    for (PlayerInfo player: alivePlayers.values())
                    {

                        // Runs if the player referenced in the for loop is the same player who found their block
                        if(player.player == p) {
                            p.sendMessage(ChatColor.GREEN + "You found your block!"); // Notifies the player that they have found their block
                            p.playSound(p.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1.0F, 8.0F); // Plays a sound to the player
                        } else {
                            player.player.sendMessage(ChatColor.GREEN + p.getName() + " has found their block!"); // Notifies all other alivePlayers that someone has found their block
                        }

                        // if any of the alivePlayers have not found their block, allPlayersSuccessful is reset to false
                        if(!player.found) {
                            allPlayersSuccessful = false;
                        }
                    }

                    // if all alivePlayers have found their block, the timer is reset - starting a new round
                    if(allPlayersSuccessful) {
                        timeRemaining = roundTime;
                        roundCount++;
                    }
                }
            }
        }
    }

    // Runs every time someone dies
    @EventHandler
    public void playerDie(PlayerDeathEvent d) {

        // Runs if the player who died is not part of the game (ie. if they have just been killed because they failed to find their block)
        if (alivePlayers.get(d.getEntity().getName()) == null) {
            d.setDeathMessage(ChatColor.RED + d.getEntity().getName() + " exploded"); // Sets the alivePlayers death message
        }
    }

    public void generateOptions(Integer options, Integer players) {
        voteOptions.clear();
        for (int option = 0; option < options; option++) {
            ArrayList<BlockInfo> blocks = new ArrayList<BlockInfo>();
            for (int block = 0; block < players; block++) {
                if(roundCount == 1) {
                    int randomNumber = (int) (Math.random() * easyBlocks.size());
                    blocks.add(allBlocks.get(randomNumber));
                } else if (roundCount == 2) {
                    blocks.add(allBlocks.get((int) (Math.random() * (easyBlocks.size() + mediumBlocks.size()))));
                } else {
                    blocks.add(allBlocks.get((int) (Math.random() * allBlocks.size())));
                }
            }
            voteOptions.add(blocks);
        }
    }

    public BlockInfo chooseRandomBlock(ArrayList<BlockInfo> list) {
        int randomNumber = (int) (Math.random() * list.size());
        System.out.println(randomNumber);
        BlockInfo randomBlock = list.get(randomNumber);
        System.out.println(randomBlock.name);
        list.remove(randomNumber);
        return randomBlock;
    }

    public Integer getResult(ArrayList<Integer> votes) {
        int maxFrequency = 0;
        int mode = 0;
        for (Integer v : votes) {
            if(Collections.frequency(votes, v) > maxFrequency) {
                maxFrequency = Collections.frequency(votes, v);
                mode = v;
            }
        }
        return mode;
    }

    public void clearChat(Player p) {
        for (int i = 0; i < 20; i++) {
            p.sendMessage("");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(command.getName().equals("tnt")) {
            Player player = (Player) sender;
            if(deadPlayers.get(player.getName()) != null) {
                if(deadPlayers.get(player.getName()).commandCooldown <= 0) {
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_TNT_PRIMED, 1.0f, 6.0F);
                    TNTPrimed tnt = (TNTPrimed) player.getWorld().spawnEntity(player.getLocation(), EntityType.PRIMED_TNT);
                    tnt.setFuseTicks(100);
                    deadPlayers.get(player.getName()).commandCooldown = 20;
                } else {
                    player.sendMessage(ChatColor.RED + "You can't send any commands for " + deadPlayers.get(player.getName()).commandCooldown + " more seconds");
                }
            }
        }

        if(command.getName().equals("creeper")) {
            Player player = (Player) sender;
            if(deadPlayers.get(player.getName()) != null) {
                if(deadPlayers.get(player.getName()).commandCooldown <= 0) {
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_CREEPER_HURT, 1.0f, 2.0F);
                    Entity creeper = (Entity) player.getWorld().spawnEntity(player.getLocation(), EntityType.CREEPER);
                    deadPlayers.get(player.getName()).commandCooldown = 20;
                } else {
                    player.sendMessage(ChatColor.RED + "You can't send any commands for " + deadPlayers.get(player.getName()).commandCooldown + " more seconds");
                }
            }
        }

        if(command.getName().equals("zombie")) {
            Player player = (Player) sender;
            if(deadPlayers.get(player.getName()) != null) {
                if(deadPlayers.get(player.getName()).commandCooldown <= 0) {
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_HURT, 1.0f, 2.0F);
                    Entity zombie = (Entity) player.getWorld().spawnEntity(player.getLocation(), EntityType.ZOMBIE);
                    deadPlayers.get(player.getName()).commandCooldown = 20;
                } else {
                    player.sendMessage(ChatColor.RED + "You can't send any commands for " + deadPlayers.get(player.getName()).commandCooldown + " more seconds");
                }
            }
        }

        if(command.getName().equals("vote")) {
            Player player = (Player) sender;
            if (!deadPlayers.get(player.getName()).voted) {
                playerVotes.add(Integer.parseInt(args[0]));
                alivePlayers.get(player.getName()).voted = true;
                player.sendMessage(ChatColor.GREEN + "You voted for OPTION " + (Integer.parseInt(args[0])+1));
            } else {
                ArrayList<String> alreadyVoted = new ArrayList<String>();
                alreadyVoted.add("You already voted retard");
                alreadyVoted.add("stop fucking pressing the button");
                alreadyVoted.add("seriously please stop");
                alreadyVoted.add("stop it nick hegyi");
                alreadyVoted.add("congratulations you have been accepted into anonymous");
                player.sendMessage(ChatColor.RED + alreadyVoted.get((int)(Math.random()*alreadyVoted.size())));
            }
        }
        // Runs if a player types /blockshuffle
        if (command.getName().equals("blockshuffle")) {
            Bukkit.broadcastMessage("Started Block Shuffle!"); // Sends a message to every player on the server
            Player[] onlinePlayers = Bukkit.getOnlinePlayers().toArray(new Player[Bukkit.getOnlinePlayers().size()]); // Creates a list of all online alivePlayers
            playing = true;
            generateOptions(Bukkit.getOnlinePlayers().size(), Bukkit.getOnlinePlayers().size());

            // Loops through all online players
            for (Player player : onlinePlayers) {
                PlayerInfo playerData = new PlayerInfo(); // Creates new PlayerInfo object for each player
                playerData.player = player.getPlayer(); // sets player property to the given Player object
                alivePlayers.put(player.getName(), playerData); // Adds PlayerInfo objects to the alivePlayers HashMap
            }
            BukkitTask timer = new BukkitRunnable() { // Starts a timer which repeats every 20 ticks (see end of statement for arguments)

                // This is the function which runs every 20 ticks
                @Override
                public void run() {

                    // Runs if a round has just started
                    if (timeRemaining == roundTime) {
                        ArrayList votedBlockList = voteOptions.get(getResult(playerVotes));
                        playerVotes.clear();

                        // Loops through all players who haven't died yet
                        for (PlayerInfo p : alivePlayers.values()) {
                            BlockInfo randomBlock = chooseRandomBlock(votedBlockList);
                            p.block = randomBlock.block;
                            p.found = false; // resets the player's found property to false
                            p.player.sendMessage(ChatColor.YELLOW + "Stand on " + randomBlock.name); // Tells the player which block they have been assigned
                            p.player.playSound(p.player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.0F, 8.0F); // Plays a sound
                        }
                        allPlayersSuccessful = false;
                        generateOptions(Bukkit.getOnlinePlayers().size(), alivePlayers.size());


                        ArrayList<ArrayList> voteOptionsText = new ArrayList<ArrayList>();
                        int numberOfOptions = voteOptions.size();
                        int bufferLength = Integer.toString(numberOfOptions).length();
                        for (ArrayList<BlockInfo> vote : voteOptions) {
                            ArrayList<String> lines = new ArrayList<String>();
                            String line = " " + vote.get(0).name + ", ";
                            for (int block = 1; block < vote.size(); block++) {
                                if(vote.get(block).name.length() + line.length() > 56-bufferLength) {
                                    lines.add(line);
                                    line = vote.get(block).name + ", ";
                                } else {
                                    line += vote.get(block).name + ", ";
                                }
                            }
                            line = line.substring(0, line.length()-2);
                            lines.add(line);
                            voteOptionsText.add(lines);
                        }

                        // Loops through all players who have died
                        for (PlayerInfo p : deadPlayers.values()) { // change to deadPlayers
                            clearChat(p.player);
                            p.player.sendMessage(ChatColor.GREEN + "Click to cast your vote for the next round of blocks");
                            p.player.sendMessage("-----------------------------------------------------");
                            for (int optionNumber = 0; optionNumber < voteOptionsText.size(); optionNumber++) {
                                String lineStart = (optionNumber+1) + ":";
                                for (int lineNumber = 0; lineNumber < voteOptionsText.get(optionNumber).size(); lineNumber++) {
                                    TextComponent line = new TextComponent(ChatColor.AQUA + lineStart + ChatColor.LIGHT_PURPLE + voteOptionsText.get(optionNumber).get(lineNumber));
                                    line.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vote " + optionNumber));
                                    line.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("VOTE FOR OPTION " + (optionNumber+1)).create()));
                                    p.player.spigot().sendMessage(line);
                                    lineStart = "   ";
                                }
                                p.player.sendMessage("-----------------------------------------------------");
                            }
                            p.voted = false;
                        }
                    }

                    // Runs if the round has ended
                    if (timeRemaining == 0) {

                        // Loops through all players who haven't died yet
                        for (PlayerInfo p : alivePlayers.values()) {

                            // Runs if the player has not found their block
                            if (!p.found) {
                                String playerName = p.player.getName();
                                deadPlayers.put(playerName, p); // Adds the player to the list of dead players
                                alivePlayers.remove(playerName); // Removes the player from the list of alive players
                                Bukkit.broadcastMessage(playerName + " failed to stand on their block!"); // Says who failed to all alivePlayers
                                deadPlayers.get(playerName).player.setHealth(0); // Kills the player
                                deadPlayers.get(playerName).player.getWorld().createExplosion(deadPlayers.get(playerName).player.getLocation(), 15, true); // Creates an explosion at the location of the player
                                deadPlayers.get(playerName).player.setGameMode(GameMode.SPECTATOR); // Sets the player's gamemode to Spectator
                            }
                        }

                        // Runs if only 1 player remains
                        if (alivePlayers.size() == 1) {
                            Bukkit.broadcastMessage(ChatColor.GOLD + alivePlayers.entrySet().iterator().next().getValue().player.getName() + " wins!"); // Says who won to all alivePlayers
                            this.cancel(); // Stops the timer loop
                            playing = false;

                        // Runs if 0 alivePlayers remain
                        } else if (alivePlayers.size() == 0) {
                            Bukkit.broadcastMessage(ChatColor.DARK_RED + "Everyone lost!"); // Says that everyone lost to all players
                            this.cancel(); // Stops the timer loop
                            playing = false;
                        } else {
                            timeRemaining = roundTime+1; // Resets timer (1 is added because the timeRemaining variable will be decremented later in the function)
                            roundCount++;
                        }

                    // Runs if 10 seconds or less remain in the round
                    } else if (timeRemaining <= 10) {

                        // Loops through all alivePlayers who haven't died yet
                        for (PlayerInfo p : alivePlayers.values()) {

                            // Runs if the player has found their block
                            if (p.found) {
                                p.player.sendMessage(ChatColor.GREEN + "" + timeRemaining + " seconds remaining!"); // Tells the remaining time to the player
                            } else {
                                p.player.sendMessage(ChatColor.RED + "You have " + timeRemaining + " seconds to stand on your block!"); // Tells the remaining time to the player
                                p.player.playSound(p.player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 10.0F); // Plays a sound
                            }
                        }

                    // Runs every minute
                    } else if (timeRemaining % 60 == 0) {
                        Bukkit.broadcastMessage(timeRemaining / 60 + " minutes remaining!"); // Tells the remaining time to all alivePlayers
                    }
                    for (PlayerInfo p : deadPlayers.values()) { // change to deadPlayers
                        p.commandCooldown --;
                    }
                    timeRemaining--;
                }
            }.runTaskTimer(this, 0, 20); // Sets arguments for the timer function
        }
        return false;
    }
}