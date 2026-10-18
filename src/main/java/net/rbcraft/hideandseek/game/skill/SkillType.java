package net.rbcraft.hideandseek.game.skill;

import org.bukkit.Material;

public enum SkillType {
    N(Material.STICK),
    E(Material.PAPER),
    Q(Material.DIAMOND);

    private final Material material;

    SkillType(Material material) {
        this.material = material;
    }

    public Material getMaterial() {
        return this.material;
    }
}
