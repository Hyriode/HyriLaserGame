package fr.hyriode.lasergame.game.test;

import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.lasergame.HyriLaserGame;
import fr.hyriode.lasergame.game.gui.BonusMenuGui;
import org.bukkit.entity.Player;

public class TestCommand extends HyriCommand<HyriLaserGame> {

    public TestCommand(HyriLaserGame plugin) {
        super(plugin, new HyriCommandInfo("lgtest")
                .withDescription("test")
                .withPermission(p -> p.getRank().isStaff()));
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        this.handleArgument(ctx, "bonus", output -> {
            if(ctx.getSender() instanceof Player) {
                Player player = (Player) ctx.getSender();
                new BonusMenuGui(this.plugin, player).open();
            }
            ctx.getSender().sendMessage("test");
        });
    }
}
