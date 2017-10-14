package fr.fosco.DiscordRegister;

import fr.fosco.DiscordRegister.listener.Command;
import fr.fosco.DiscordRegister.listener.DiscordListener;
import fr.fosco.DiscordRegister.listener.LoginManager;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.impl.UserImpl;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import javax.security.auth.login.LoginException;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Main extends JavaPlugin{

    private JDA jda;
    private static Main instance;
    private HashMap<Player, String> authHashmap = new HashMap<>();
    private HashMap<Player, Long> idPlayers = new HashMap<>();
    private HashMap<User, Player> confirmationHashMap = new HashMap<>();
    private List<Player> canMove = new ArrayList<>();
    private String alphabet = "abcdefghijklmnopqrstuvwxyz123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";


    @Override
    public void onLoad() {
        try {
            jda = new JDABuilder(AccountType.BOT).setToken(getConfig().getString("token")).addEventListener(new DiscordListener()).buildAsync();
        } catch (LoginException e) {
            e.printStackTrace();
        } catch (RateLimitedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        instance =this;
        getServer().getPluginManager().registerEvents(new LoginManager(), this);
        getCommand("dd").setExecutor(new Command());
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        jda.shutdown();
    }

    static public Main getInstance() {
        return instance;
    }

    public List<Player> getCanMove() {
        return canMove;
    }

    public HashMap<Player, Long> getIdPlayers() {
        return idPlayers;
    }

    public HashMap<User, Player> getConfirmationHashMap() {
        return confirmationHashMap;
    }

    public JDA getJda() {
        return jda;
    }

    public void loadData(Player player){
        if(getConfig().get("Users." + player.getUniqueId()) != null) {
            Long key = getConfig().getLong("Users." + player.getUniqueId());
            idPlayers.put(player, key);
        }
    }

    public void saveData(Player player){
        if(idPlayers.get(player) != null) {
            getConfig().set("Users." + player.getUniqueId(), idPlayers.get(player));
        }
        saveConfig();
    }

    public void isRightCode(Player player, String keyword){
        if(idPlayers.get(player) != null){
            if(authHashmap.get(player).equals(keyword)){
                canMove.remove(player);
                authHashmap.remove(player);
                player.sendMessage(getConfig().getString("login").replace("&","§"));
            } else {
                player.sendMessage(getConfig().getString("wrongcode").replace("&","§"));
            }
        } else {
            player.sendMessage(getConfig().getString("havent_code").replace("&","§"));
        }
    }

    public void registerPlayer(Player player, String user){
        String[] newUser = user.split("#");
        List<User> users = getJda().getUsersByName(newUser[0],false);
        User finalUser = users.stream().filter( us -> us.getDiscriminator().equals(newUser[1])).findAny().orElse(null);
        if(finalUser != null){
            player.sendMessage(getConfig().getString("register").replace("%name%", user).replace("&","§"));
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle(getConfig().getString("title_confirm"));
            builder.setColor(Color.MAGENTA.magenta);
            builder.setAuthor(finalUser.getName(), null, "https://minotar.net/avatar/" + player.getName() + ".png?size=256");
            builder.setDescription(getConfig().getString("msg_confirm").replace("%name", player.getName()));
            sendPrivateEmbedMessage(finalUser, builder);
            confirmationHashMap.put(finalUser, player);
        } else {
            player.sendMessage(getConfig().getString("unexist_account").replace("&", "§"));
        }
    }

    public void sendConfirmationCode(User user, Player player) {
        if(idPlayers.containsKey(player)) {
            player.sendMessage(getConfig().getString("message_code").replace("&", "§"));
            canMove.add(player);
            Random random = new Random();
            String s = "";
            for (int i = 0; i < 4; i++) {
                char c = alphabet.charAt(random.nextInt(alphabet.getBytes().length));
                s += c;
            }
            authHashmap.put(player, s);
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle(getConfig().getString("title_code"));
            builder.setColor(Color.MAGENTA);
            builder.setAuthor(user.getName(), null, "https://minotar.net/avatar/" + player.getName() + ".png?size=256");
            builder.setDescription("> " + s);
            sendPrivateEmbedMessage(user, builder);
        } else {
            player.sendMessage(getConfig().getString("can_register").replace("&", "§"));
        }
    }

    private void sendPrivateEmbedMessage(User user, EmbedBuilder builder){
        if(!user.hasPrivateChannel()) user.openPrivateChannel().complete();
        ((UserImpl) user).getPrivateChannel().sendMessage(builder.build()).queue();
    }
}
