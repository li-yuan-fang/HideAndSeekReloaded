package net.rbcraft.hideandseek.game.skill;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.rbcraft.hideandseek.game.Game;
import net.rbcraft.hideandseek.io.I18n;
import net.rbcraft.hideandseek.reflect.NBTHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Skill {

    protected final SkillType type;

    protected final SkillRole role;

    protected final String name;

    protected final double cd;

    protected final int max;

    protected Skill(String name, SkillType type, SkillRole hider, double cd, int max) {
        this.name = name;
        this.type = type;
        this.role = hider;
        this.cd = cd;
        this.max = max;
    }

    public String getName() {
        return this.name;
    }

    public ItemStack getItemStack() {
        return getItemStack(true);
    }

    public ItemStack getItemStack(boolean withNBT) {
        ItemStack skill = new ItemStack(type.getMaterial(), max);

        ItemMeta meta = skill.getItemMeta();
        meta.setDisplayName(I18n.formats("skill_title_" + getName()));
        List<String> desc = getDesc(I18n.formats("skill_desc_" + getName()));
        if (this.type != SkillType.Q) {
            desc.add(I18n.formats("skill_cd").replace("%sec%", String.valueOf(this.cd)));
            desc.add(I18n.formats("skill_max").replace("%max%", String.valueOf(this.max)));
        } else {
            desc.add(I18n.formats("skill_only"));
        }
        meta.setLore(desc);
        skill.setItemMeta(meta);

        return withNBT ? NBTHelper.setParam(skill, "skill_" + getName()) : skill;
    }

    private List<String> getDesc(String src) {
        ArrayList<String> desc = new ArrayList<>();
        if (src != null && src.length() > 0) {
            if (src.contains("\\n")) {
                desc.addAll(Arrays.asList(src.split("\\\\n")));
            } else {
                desc.add(src);
            }
        }
        return desc;
    }

    public SkillType getType() {
        return type;
    }

    public SkillRole getRole() {
        return role;
    }

    protected void call(Player player, Game game) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null) return;

        ItemStack finalItem = item.clone();
        finalItem.setAmount(1);

        int amount = item.getAmount();
        if (amount == 1) {
            player.getInventory().remove(item);
        } else {
            item.setAmount(amount - 1);
        }

        if (getType() == SkillType.Q || cd == 0) {
            if (getRole() != SkillRole.Spectator) {
                game.broadcast(I18n.formats("game_use_airdrop")
                        .replace("%role%",
                                I18n.formats("game_" + (getRole() == SkillRole.Hider ? "hider" : "seeker")))
                        .replace("%player%", player.getName())
                        .replace("%skill%", I18n.formats("skill_title_" + getName())));
            }
            return;
        }
        game.getManager().coreCounter.start((int) (cd * 20), () -> addItem(player, getRole() == SkillRole.Hider,
                finalItem, game));

        showSkillTip(player);
    }

    private void addItem(Player player, boolean isHider, ItemStack item, Game game) {
        if (player == null || item == null || game == null) return;
        if (!game.isGaming() || !game.isInGame(player)) return;
        if (isHider && !game.getHiders().contains(player)) return;
        player.getInventory().addItem(item);
    }

    protected void showSkillTip(Player player) {
        String tip = I18n.formats("skill_success").replace("%skill%", I18n.formats("skill_title_" + getName()));

        String spec = I18n.formats("skill_tip_" + getName());
        if (spec != null && spec.length() > 0) {
            tip += " " + I18n.formats("skill_success_split") + " " + spec;
        }

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(tip));
    }

    protected abstract boolean function(PlayerInteractEvent event, Game game);

    public void onPlayerUse(PlayerInteractEvent event, Game game) {
        if (function(event, game)) call(event.getPlayer(), game);
    }

}
