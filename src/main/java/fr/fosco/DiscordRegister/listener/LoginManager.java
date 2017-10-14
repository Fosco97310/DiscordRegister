package fr.fosco.DiscordRegister.listener;

import fr.fosco.DiscordRegister.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class LoginManager implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Main main = Main.getInstance();
        Player player = event.getPlayer();
        main.loadData(player);
        if(main.getIdPlayers().containsKey(player)) {
            main.sendConfirmationCode(main.getJda().getUserById(main.getIdPlayers().get(player)), player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        Main.getInstance().saveData(player);
    }

    @EventHandler
    public void onPMove(PlayerMoveEvent event){
        Main main = Main.getInstance();
        Player player = event.getPlayer();
        if(main.getCanMove().contains(player)) event.setCancelled(true);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event){
        Main main = Main.getInstance();
        Player player = event.getPlayer();
        if(main.getCanMove().contains(player) && !event.getMessage().startsWith("/dd login")) event.setCancelled(true);
    }

}

