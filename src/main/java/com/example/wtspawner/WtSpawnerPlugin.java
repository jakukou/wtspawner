package com.example.wtspawner;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.TraderLlama;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class WtSpawnerPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("WtSpawner enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("WtSpawner disabled");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("spawntrader")) {
            if (sender instanceof org.bukkit.command.ConsoleCommandSender || sender instanceof org.bukkit.entity.Player) {
                Location loc;
                if (sender instanceof org.bukkit.entity.Player) loc = ((org.bukkit.entity.Player) sender).getLocation();
                else loc = Bukkit.getWorlds().get(0).getSpawnLocation();

                int stayTicks = 24000;
                if (args.length >= 1) {
                    try { stayTicks = Integer.parseInt(args[0]); } catch (NumberFormatException ignored) {}
                }

                spawnTraderWithLlamas(loc, stayTicks);
                sender.sendMessage("已生成流浪商人和羊驼");
                return true;
            }
            return false;
        }
        return false;
    }

    private void spawnTraderWithLlamas(Location loc, int stayTicks) {
        WanderingTrader trader = (WanderingTrader) loc.getWorld().spawnEntity(loc, EntityType.WANDERING_TRADER);
        try {
            trader.setDespawnDelay(stayTicks);
        } catch (Throwable t) {
            getLogger().warning("无法设置 despawn delay: " + t.getMessage());
        }

        for (int i = 0; i < 2; i++) {
            Location spawnLoc = loc.clone().add((i == 0) ? 1 : -1, 0, 0);
            TraderLlama llama = (TraderLlama) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.TRADER_LLAMA);
            try { llama.setCarryingChest(true); } catch (Throwable ignored) {}
            try { ((LivingEntity) llama).setLeashHolder(trader); } catch (Throwable ignored) {}
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!trader.isDead()) trader.remove();
            }
        }.runTaskLater(this, Math.max(1, stayTicks));
    }
}
