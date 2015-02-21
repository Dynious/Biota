package com.dynious.biota.lib;

import com.dynious.biota.config.annotations.ConfigFloatValue;
import com.dynious.biota.config.annotations.ConfigIntValue;

public class Settings
{
    @ConfigFloatValue(defaultValue = 15F, comment = "Normal Phosphorus amount (ppm)")
    public static float NORMAL_PHOSPHORUS;
    @ConfigFloatValue(defaultValue = 5F, comment = "The random difference in Phosphorus amount per chunk")
    public static float DELTA_PHOSPHORUS;

    @ConfigFloatValue(defaultValue = 200F, comment = "Normal Potassium amount (ppm)")
    public static float NORMAL_POTASSIUM;
    @ConfigFloatValue(defaultValue = 50F, comment = "The random difference in Potassium amount per chunk")
    public static float DELTA_POTASSIUM;

    @ConfigFloatValue(defaultValue = 7.5F, comment = "Normal Nitrogen amount (ppm)")
    public static float NORMAL_NITROGEN;
    @ConfigFloatValue(defaultValue = 2.5F, comment = "The random difference in Nitrogen amount per chunk")
    public static float DELTA_NITROGEN;

    @ConfigFloatValue(defaultValue = 1F, comment = "Maximum plant growth speed (compared to non-modified) - max = 1")
    public static float MAX_PLANT_GROWTH;
    @ConfigFloatValue(defaultValue = 0.75F, comment = "Normal plant growth speed (compared to non-modified) - max = 1")
    public static float NORMAL_PLANT_GROWTH;
    @ConfigFloatValue(defaultValue = 0F, comment = "Minimal plant growth speed (compared to non-modified) - max = 1")
    public static float MINIMAL_PLANT_GROWTH;

    @ConfigFloatValue(defaultValue = 1.5F, comment = "Nutrient amount amount needed for plant spread (compared to normal)")
    public static float NUTRIENT_ABUNDANCE_FOR_SPREAD;
    @ConfigFloatValue(defaultValue = 1.5F, comment = "Nutrient amount amount needed for plant max growth (compared to normal)")
    public static float NUTRIENT_ABUNDANCE_FOR_MAX_GROWTH;
    @ConfigFloatValue(defaultValue = 1.0F, comment = "Nutrient amount amount needed for plant normal growth (compared to normal)")
    public static float NUTRIENT_AMOUNT_FOR_NORMAL_GROWTH;
    @ConfigFloatValue(defaultValue = 0.75F, comment = "Nutrient amount lower than this will stop plant growth (compared to normal)")
    public static float NUTRIENT_SHORTAGE_FOR_STOP_GROWTH;
    @ConfigFloatValue(defaultValue = 0.35F, comment = "Nutrient amount lower than this will kill plants (compared to normal)")
    public static float NUTRIENT_SHORTAGE_FOR_DEATH;

    @ConfigIntValue(defaultValue = 12, comment = "Light amount amount needed for plant spread (0 - 15)")
    public static int LIGHT_VALUE_FOR_SPREAD;
    @ConfigIntValue(defaultValue = 12, comment = "Light amount amount needed for plant max growth (0 - 15)")
    public static int LIGHT_VALUE_FOR_MAX_GROWTH;
    @ConfigIntValue(defaultValue = 10, comment = "Light amount amount needed for plant normal growth (0 - 15)")
    public static int LIGHT_VALUE_FOR_NORMAL_GROWTH;
    @ConfigIntValue(defaultValue = 8, comment = "Light amount lower than this will stop plant growth (0 - 15)")
    public static int LIGHT_VALUE_FOR_STOP_GROWTH;
    @ConfigIntValue(defaultValue = 6, comment = "Light amount lower than this will kill plants (0 - 15)")
    public static int LIGHT_VALUE_FOR_DEATH;

    @ConfigFloatValue(defaultValue = 0.1F, comment = "The chance plants growth when ticking (1 tick per +- 68 secs) - max 1")
    public static float PLANT_SPREAD_CHANCE;

    @ConfigIntValue(defaultValue = 200, comment = "Ticks between updates in biosystems, higher is better for performance, but will make changes show up later")
    public static int TICKS_PER_BIOSYSTEM_UPDATE;

    @ConfigIntValue(defaultValue = 24000, comment = "The amount of ticks for full change is nutrient values")
    public static int BIOSYSTEM_NUTRIENT_CHANGE_TICKS;

    //24000 ticks per MC day. One day for 1.0 change.
    public static float BIOSYSTEM_CHANGE_RATE = (float) TICKS_PER_BIOSYSTEM_UPDATE / BIOSYSTEM_NUTRIENT_CHANGE_TICKS;

    //Mass fraction of nutrients in biomass
    @ConfigFloatValue(defaultValue = 0.001F, comment = "The amount of Phosphorus per biomass value")
    public static float BIOMASS_PHOSPHORUS_RATE;
    @ConfigFloatValue(defaultValue = 0.006F, comment = "The amount of Potassium per biomass value")
    public static float BIOMASS_POTASSIUM_RATE;
    @ConfigFloatValue(defaultValue = 0.014F, comment = "The amount of Nitrogen per biomass value")
    public static float BIOMASS_NITROGEN_RATE;
    
    private static float NORMAL_RATE = (float) Math.cbrt(1.0 / (BIOMASS_PHOSPHORUS_RATE * BIOMASS_POTASSIUM_RATE * BIOMASS_NITROGEN_RATE));
    
    public static float PHOSPHORUS_CHANGE_RATE = BIOMASS_PHOSPHORUS_RATE * NORMAL_RATE * BIOSYSTEM_CHANGE_RATE;
    public static float POTASSIUM_CHANGE_RATE = BIOMASS_POTASSIUM_RATE * NORMAL_RATE * BIOSYSTEM_CHANGE_RATE;
    public static float NITROGEN_CHANGE_RATE = BIOMASS_NITROGEN_RATE * NORMAL_RATE * BIOSYSTEM_CHANGE_RATE;

    @ConfigFloatValue(defaultValue = 10F, comment = "The speed at which bacteria spread compared to the average change of nutrients")
    public static float BACTERIA_CHANGE_AMOUNT;
    
    public static float BACTERIA_CHANGE_RATE = BACTERIA_CHANGE_AMOUNT*BIOSYSTEM_CHANGE_RATE;

    @ConfigIntValue(defaultValue = 168000, comment = "The amount of ticks for full spread is nutrient values")
    public static int BIOSYSTEM_NUTRIENT_SPREAD_TICKS;

    //168000 ticks per MC week. One week for 1.0 change in spread.
    public static float BIOSYSTEM_SPREAD_RATE = (float) TICKS_PER_BIOSYSTEM_UPDATE / BIOSYSTEM_NUTRIENT_SPREAD_TICKS;

    // Bacteria will start to die when there's this much biomass compared to what they can consume at max
    @ConfigFloatValue(defaultValue = 0.75F, comment = "Bacteria will start to die when there's this much biomass compared to what they can consume at max")
    public static float BACTERIA_DEATH;

    @ConfigFloatValue(defaultValue = 1.2F, comment = "The amount Phosphorus in Bonemeal")
    public static float BONEMEAL_PHOSPHORUS;
    @ConfigFloatValue(defaultValue = 0.0F, comment = "The amount Potassium in Bonemeal")
    public static float BONEMEAL_POTASSIUM;
    @ConfigFloatValue(defaultValue = 0.4F, comment = "The amount Nitrogen in Bonemeal")
    public static float BONEMEAL_NITROGEN;
}
