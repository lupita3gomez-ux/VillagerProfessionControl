package mx.netherhost.villagerprofessioncontrol;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.villager.VillagerAcquireProfessionEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.EnumSet;
import java.util.Set;

public final class VillagerProfessionControl extends JavaPlugin implements Listener {
    private final Set<Villager.Profession> allowed = EnumSet.noneOf(Villager.Profession.class);
    private Villager.Profession defaultProfession;
    private boolean enforceExisting;
    private boolean checkOnChunkLoad;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadSettings();
        Bukkit.getPluginManager().registerEvents(this, this);

        if (enforceExisting) {
            Bukkit.getScheduler().runTask(this, this::checkLoadedVillagers);
        }

        getLogger().info("VillagerProfessionControl activado.");
        getLogger().info("Profesiones permitidas: " + allowed);
    }

    private void loadSettings() {
        allowed.clear();
        for (String name : getConfig().getStringList("allowed-professions")) {
            try {
                allowed.add(Villager.Profession.valueOf(name.toUpperCase()));
            } catch (IllegalArgumentException ex) {
                getLogger().warning("Profesión desconocida en config.yml: " + name);
            }
        }

        String defaultName = getConfig().getString("default-profession", "FARMER");
        try {
            defaultProfession = Villager.Profession.valueOf(defaultName.toUpperCase());
        } catch (IllegalArgumentException ex) {
            getLogger().warning("default-profession inválida: " + defaultName + ". Se usará FARMER.");
            defaultProfession = Villager.Profession.FARMER;
        }

        if (!allowed.contains(defaultProfession)) {
            getLogger().warning("default-profession no está permitida. Se usará la primera profesión permitida.");
            if (!allowed.isEmpty()) defaultProfession = allowed.iterator().next();
        }

        enforceExisting = getConfig().getBoolean("enforce-existing", true);
        checkOnChunkLoad = getConfig().getBoolean("check-on-chunk-load", true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAcquireProfession(VillagerAcquireProfessionEvent event) {
        Villager villager = event.getEntity();
        if (!allowed.contains(event.getProfession())) {
            event.setCancelled(true);
            setAllowedProfession(villager);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVillagerSpawn(CreatureSpawnEvent event) {
        if (event.getEntity() instanceof Villager villager) {
            Bukkit.getScheduler().runTask(this, () -> enforceIfNeeded(villager));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkLoad(ChunkLoadEvent event) {
        if (!checkOnChunkLoad) return;
        for (Entity entity : event.getChunk().getEntities()) {
            if (entity instanceof Villager villager) enforceIfNeeded(villager);
        }
    }

    private void checkLoadedVillagers() {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Villager villager) enforceIfNeeded(villager);
            }
        }
    }

    private void enforceIfNeeded(Villager villager) {
        if (!villager.isValid() || villager.isDead()) return;
        if (!allowed.contains(villager.getProfession())) setAllowedProfession(villager);
    }

    private void setAllowedProfession(Villager villager) {
        if (defaultProfession != null && villager.getProfession() != defaultProfession) {
            villager.setProfession(defaultProfession);
        }
    }
}
