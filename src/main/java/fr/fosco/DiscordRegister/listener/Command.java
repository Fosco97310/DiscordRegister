package fr.fosco.DiscordRegister.listener;

import fr.fosco.DiscordRegister.Main;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class Command implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            FileConfiguration file = Main.getInstance().getConfig();
            if (args.length == 0 || args.length == 1){
                List<String> stringList = file.getStringList("help");
                stringList.forEach(str -> {
                    player.sendMessage(str.replace("&","§"));
                });
            } else if(args.length == 2){
                if(args[0].equalsIgnoreCase("register")){
                    Main.getInstance().registerPlayer(player, args[1]);
                }else if(args[0].equalsIgnoreCase("login")){
                    Main.getInstance().isRightCode(player, args[1]);
                }else if(args[0].equalsIgnoreCase("unregister")){
                    Main.getInstance().getIdPlayers().remove(player);
                }
            }
        }
        return true;
    }
}
