package com.dynious.biota.lib;

public class Settings
{
    public static final float NORMAL_PHOSPHORUS = 15F;
    public static final float DELTA_PHOSPHORUS = 5F;

    public static final float NORMAL_POTASSIUM = 200F;
    public static final float DELTA_POTASSIUM = 50F;

    public static final float NORMAL_NITROGEN = 7.5F;
    public static final float DELTA_NITROGEN = 2.5F;

    public static final float MAX_PLANT_GROWTH = 1F;
    public static final float NORMAL_PLANT_GROWTH = 0.75F;
    public static final float MINIMAL_PLANT_GROWTH = 0F;

    public static final float NUTRIENT_ABUNDANCE_FOR_MAX_GROWTH = 1.5F;
    public static final float NUTRIENT_SHORTAGE_FOR_STOP_GROWTH = 0.75F;
    public static final float NUTRIENT_SHORTAGE_FOR_DEATH = 0.35F;

    public static final int TICKS_PER_BIOSYSTEM_UPDATE = 200;

    //24000 ticks per MC day. One day for 1.0 change.
    public static final float BIOSYSTEM_CHANGE_RATE = (float) TICKS_PER_BIOSYSTEM_UPDATE /24000;

    public static final float BIOMASS_PHOSPHORUS_RATE = 0.001F;
    public static final float BIOMASS_POTASSIUM_RATE = 0.006F;
    public static final float BIOMASS_NITROGEN_RATE = 0.014F;

    private static final float NORMAL_RATE = (float) Math.cbrt(1.0 / (BIOMASS_PHOSPHORUS_RATE * BIOMASS_POTASSIUM_RATE * BIOMASS_NITROGEN_RATE));

    public static final float PHOSPHORUS_CHANGE_RATE = BIOMASS_PHOSPHORUS_RATE * NORMAL_RATE * BIOSYSTEM_CHANGE_RATE;
    public static final float POTASSIUM_CHANGE_RATE = BIOMASS_POTASSIUM_RATE * NORMAL_RATE * BIOSYSTEM_CHANGE_RATE;
    public static final float NITROGEN_CHANGE_RATE = BIOMASS_NITROGEN_RATE * NORMAL_RATE * BIOSYSTEM_CHANGE_RATE;
    public static final float BACTERIA_CHANGE_RATE = 10F*BIOSYSTEM_CHANGE_RATE;

    //168000 ticks per MC week. One week for 1.0 change in spread.
    public static final float BIOSYSTEM_SPREAD_RATE = (float) TICKS_PER_BIOSYSTEM_UPDATE /168000;

    // Bacteria will start to die when there's this much biomass compared to what they can consume at max
    public static final float BACTERIA_DEATH = 0.75F;

    public static final float BONEMEAL_PHOSPHORUS = 1.2F;
    public static final float BONEMEAL_POTASSIUM = 0.0F;
    public static final float BONEMEAL_NITROGEN = 0.4F;
}
