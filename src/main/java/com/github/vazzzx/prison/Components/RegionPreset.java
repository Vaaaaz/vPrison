package com.github.vazzzx.prison.Components;

import com.github.vazzzx.prison.Utils.Cuboid;
import lombok.Data;
import org.bukkit.Location;

@Data
public class RegionPreset {

    private Location pos1;
    private Location pos2;

    private Cuboid cuboid;

}
