package pl.rar.minecraft.xpcash;

	import java.io.File;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

	public class Main extends JavaPlugin {

	    private static final Logger log = Logger.getLogger("Minecraft");
	    public static Economy econ = null;
	    public static Permission perms = null;

	    @Override
	    public void onDisable() {
	    	
	    }

	    @Override
	    public void onEnable() {
	        if (!setupEconomy() ) {
	            log.severe("[XPCash] Vault was not found, disabling XPCash.");
	            getServer().getPluginManager().disablePlugin(this);
	            return;
	        }
	        setupPermissions();
	        File file = new File(getDataFolder() + File.separator + "config.yml");
	        
	        if(!file.exists()){
		        log.info("[XPCash] No config found, generating a new one...");
		        this.getConfig().addDefault("PurchaseSuccesful", "You purchased %s for your XP.");
		        this.getConfig().addDefault("NewBalance", "Now you have %s.");
		        this.getConfig().addDefault("NotEnoughXP", "You need at least 1 XP level to use /xpcash");
		        this.getConfig().options().copyDefaults(true);
		        this.saveConfig();
	        }
	    }

	    private boolean setupEconomy() {
	        if (getServer().getPluginManager().getPlugin("Vault") == null) {
	            return false;
	        }
	        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
	        if (rsp == null) {
	            return false;
	        }
	        econ = rsp.getProvider();
	        return econ != null;
	    }

	    private boolean setupPermissions() {
	        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
	        perms = rsp.getProvider();
	        return perms != null;
	    }

	    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
	        if(!(sender instanceof Player)) {
	            log.info("Sorry, only players can use XPCash.");
	            return true;
	        }

	        Player player = (Player) sender;

	        if(command.getLabel().equals("xpcash")) {
	        	if (sender.isOp() || sender.hasPermission("xpcash.buy")) {
	        		if (player.getLevel() >= 1){
	        			EconomyResponse r = econ.depositPlayer(player.getName(), player.getLevel());
	    	            if(r.transactionSuccess()) {
	    	            	player.setLevel(0);
	    	                sender.sendMessage(String.format(ChatColor.AQUA + "[XPCash] " + ChatColor.GREEN + this.getConfig().getString("PurchaseSuccesful"), econ.format(r.amount)));
	    	                sender.sendMessage(String.format(ChatColor.AQUA + "[XPCash] " + ChatColor.GREEN + this.getConfig().getString("NewBalance"), econ.format(r.balance)));
	    	            } else {
	    	                sender.sendMessage(String.format("Error: %s", r.errorMessage));
	    	            }
	        		} else if (player.getLevel() <= 0) {
	        			sender.sendMessage(ChatColor.AQUA + "[XPCash] " + ChatColor.RED + this.getConfig().getString("NotEnoughXP"));
	        		}
} else {
	sender.sendMessage(ChatColor.RED + "Sorry, you don't have permission.");
}
}
			return true;
}
}
