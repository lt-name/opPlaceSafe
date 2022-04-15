package name.opPlaceSafe;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;

import java.io.File;
import java.util.ArrayList;

public class opPlaceSafe extends PluginBase implements Listener {

    private Config config;
    private ArrayList<String> blocks = new ArrayList<>();

    @Override
    public void onEnable() {
        if (!new File(getDataFolder() + "/blocks.yml").exists()) {
            saveResource("blocks.yml", "blocks.yml", false);
        }
        this.config = new Config(getDataFolder() + "/blocks.yml");
        this.blocks = (ArrayList<String>) config.getStringList("blocks");
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getLogger().info("已加载！");
    }

    @Override
    public void onDisable() {
        this.config.set("blocks", this.blocks);
        this.config.save();
        this.getLogger().info("已卸载！");
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        String block = this.getBlockString(event.getBlock());
        if (player != null && block != null && player.isOp()) {
            if (!this.blocks.contains(block)) {
                this.blocks.add(block);
                this.config.set("blocks", this.blocks);
                this.config.save();
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        String block = this.getBlockString(event.getBlock());
        if (player != null && block != null) {
            if (player.isOp()) {
                this.blocks.remove(block);
                this.config.set("blocks", this.blocks);
                this.config.save();
            }else {
                if (this.blocks.contains(block)) {
                    player.sendMessage("§e >> §cOP放置的方块无法被破坏！");
                    event.setCancelled();
                }
            }
        }
    }

    public String getBlockString(Block block) {
        if (block == null) { return null; }
        return block.getX() + ":" + block.getY() + ":" + block.getZ() + ":" + block.getLevel().getFolderName();
    }

}
