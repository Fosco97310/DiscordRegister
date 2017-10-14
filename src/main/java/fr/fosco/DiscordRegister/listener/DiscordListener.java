package fr.fosco.DiscordRegister.listener;


import fr.fosco.DiscordRegister.Main;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;


public class DiscordListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getAuthor().isBot()) return;
        if(event.isFromType(ChannelType.PRIVATE)){
            User user = event.getAuthor();
            Main main = Main.getInstance();
            if(event.getMessage().getContent().equals("!accept")){
                if(main.getConfirmationHashMap().containsKey(user)){
                    main.getIdPlayers().put(main.getConfirmationHashMap().get(user), user.getIdLong());
                    main.getConfirmationHashMap().remove(user);
                }
            }
        }
    }
}
