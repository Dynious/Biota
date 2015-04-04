package com.dynious.biota.lib;

import com.dynious.biota.config.annotations.ConfigFloatValue;
import com.dynious.biota.config.annotations.ConfigIntValue;

public class Settings
{
    @ConfigFloatValue(defaultValue = 15F, comment = "Normal Phosphorus amount (ppm)")
    public static float NORMAL_PHOSPHORUS;
    // Used to be 0.33
    @ConfigFloatValue(defaultValue = 0.08F, comment = "The random difference in Phosphorus amount per chunk in percents")
    public static float DELTA_PHOSPHORUS;

    @ConfigFloatValue(defaultValue = 200F, comment = "Normal Potassium amount (ppm)")
    public static float NORMAL_POTASSIUM;
    // Used to be 0.25
    @ConfigFloatValue(defaultValue = 0.05F, comment = "The random difference in Potassium amount per chunk in percents")
    public static float DELTA_POTASSIUM;

    @ConfigFloatValue(defaultValue = 7.5F, comment = "Normal Nitrogen amount (ppm)")
    public static float NORMAL_NITROGEN;
    // Used to be 0.33
    @ConfigFloatValue(defaultValue = 0.08F, comment = "The random difference in Nitrogen amount per chunk in percents")
    public static float DELTA_NITROGEN;

    @ConfigFloatValue(defaultValue = 0.1F, comment = "The chance plants growth when ticking (1 tick per +- 68 secs) - max 1")
    public static float PLANT_SPREAD_CHANCE;
    @ConfigFloatValue(defaultValue = 1F, comment = "Maximum plant growth speed (compared to non-modified) - max = 1")
    public static float PLANT_GROWTH_MAX;
    @ConfigFloatValue(defaultValue = 0.75F, comment = "Normal plant growth speed (compared to non-modified) - max = 1")
    public static float PLANT_GROWTH_NORMAL;
    @ConfigFloatValue(defaultValue = 0F, comment = "Minimal plant growth speed (compared to non-modified) - max = 1")
    public static float PLANT_GROWTH_MINIMAL;

    @ConfigFloatValue(defaultValue = 1.5F, comment = "Nutrient amount needed for plant spread (compared to normal)")
    public static float NUTRIENT_AMOUNT_FOR_SPREAD;
    @ConfigFloatValue(defaultValue = 1.5F, comment = "Nutrient amount needed for plant max growth (compared to normal)")
    public static float NUTRIENT_AMOUNT_FOR_MAX_GROWTH;
    @ConfigFloatValue(defaultValue = 1.0F, comment = "Nutrient amount needed for plant normal growth (compared to normal)")
    public static float NUTRIENT_AMOUNT_FOR_NORMAL_GROWTH;
    @ConfigFloatValue(defaultValue = 0.75F, comment = "Nutrient amount lower than this will stop plant growth (compared to normal)")
    public static float NUTRIENT_AMOUNT_FOR_STOP_GROWTH;
    @ConfigFloatValue(defaultValue = 0.35F, comment = "Nutrient amount lower than this will kill plants (compared to normal)")
    public static float NUTRIENT_AMOUNT_FOR_DEATH;

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

    @ConfigIntValue(defaultValue = 200, comment = "Ticks between updates in biosystems, higher is better for performance, but will make changes show up later")
    public static int TICKS_PER_BIOSYSTEM_UPDATE;

    @ConfigIntValue(defaultValue = 24000, comment = "The amount of ticks for full change in nutrient values")
    public static int BIOSYSTEM_NUTRIENT_CHANGE_TICKS;

    //24000 ticks per MC day. One day for 1.0 change.
    public static float BIOSYSTEM_CHANGE_RATE;

    //Mass fraction of nutrients in biomass
    @ConfigFloatValue(defaultValue = 0.001F, comment = "The amount of Phosphorus per biomass value")
    public static float BIOMASS_PHOSPHORUS_RATE;
    @ConfigFloatValue(defaultValue = 0.006F, comment = "The amount of Potassium per biomass value")
    public static float BIOMASS_POTASSIUM_RATE;
    @ConfigFloatValue(defaultValue = 0.014F, comment = "The amount of Nitrogen per biomass value")
    public static float BIOMASS_NITROGEN_RATE;
    
    public static float PHOSPHORUS_CHANGE_RATE;
    public static float POTASSIUM_CHANGE_RATE;
    public static float NITROGEN_CHANGE_RATE;

    @ConfigFloatValue(defaultValue = 100F, comment = "The speed at which bacteria spread compared to the average change of nutrients")
    public static float BACTERIA_CHANGE_AMOUNT;
    
    public static float BACTERIA_CHANGE_RATE;

    @ConfigIntValue(defaultValue = 168000, comment = "The amount of ticks for full spread is nutrient values")
    public static int BIOSYSTEM_NUTRIENT_SPREAD_TICKS;

    //168000 ticks per MC week. One week for 1.0 change in spread.
    public static float BIOSYSTEM_SPREAD_RATE;

    @ConfigFloatValue(defaultValue = 0.75F, comment = "Bacteria will start to die when there's this much biomass compared to what they can consume at max")
    public static float BACTERIA_DEATH;

    @ConfigFloatValue(defaultValue = 1.2F, comment = "Bacteria will start to stop growing when then they are this rate compared to the biomass in the chunk.")
    public static float BACTERIA_GROWTH_MAX;

    @ConfigFloatValue(defaultValue = 0.6F, comment = "The amount Phosphorus in Bonemeal")
    public static float BONEMEAL_PHOSPHORUS;
    @ConfigFloatValue(defaultValue = 0.0F, comment = "The amount Potassium in Bonemeal")
    public static float BONEMEAL_POTASSIUM;
    @ConfigFloatValue(defaultValue = 0.2F, comment = "The amount Nitrogen in Bonemeal")
    public static float BONEMEAL_NITROGEN;

    @ConfigIntValue(defaultValue = 5, comment = "The chance a grass block will get more worn - The larger this value the smaller the chance, 1 is always, 10 = 1/10 chance")
    public static int GRASS_WORN_ENTITY_WALK_ON_CHANCE;

    @ConfigIntValue(defaultValue = 20, comment = "The chance a grass block will grow back when ticking (1 tick per +- 68 secs) - The larger this value the smaller the chance, 1 is always, 10 = 1/10 chance")
    public static int GRASS_GROW_BACK_CHANCE;

    @ConfigIntValue(defaultValue = 1, comment = "The amount of flowers per chunk")
    public static int FLOWER_QUANTITY;

    public static void recalculate()
    {
        BIOSYSTEM_CHANGE_RATE = (float) TICKS_PER_BIOSYSTEM_UPDATE / BIOSYSTEM_NUTRIENT_CHANGE_TICKS;

        System.out.println(BIOSYSTEM_CHANGE_RATE);

        float normalRate = (float) Math.cbrt(1.0 / (BIOMASS_PHOSPHORUS_RATE * BIOMASS_POTASSIUM_RATE * BIOMASS_NITROGEN_RATE));
        PHOSPHORUS_CHANGE_RATE = BIOMASS_PHOSPHORUS_RATE * normalRate * BIOSYSTEM_CHANGE_RATE;
        POTASSIUM_CHANGE_RATE = BIOMASS_POTASSIUM_RATE * normalRate * BIOSYSTEM_CHANGE_RATE;
        NITROGEN_CHANGE_RATE = BIOMASS_NITROGEN_RATE * normalRate * BIOSYSTEM_CHANGE_RATE;


        System.out.println(PHOSPHORUS_CHANGE_RATE);
        System.out.println(POTASSIUM_CHANGE_RATE);
        System.out.println(NITROGEN_CHANGE_RATE);

        BACTERIA_CHANGE_RATE = BACTERIA_CHANGE_AMOUNT*BIOSYSTEM_CHANGE_RATE;
        BIOSYSTEM_SPREAD_RATE = (float) TICKS_PER_BIOSYSTEM_UPDATE / BIOSYSTEM_NUTRIENT_SPREAD_TICKS;
    }
}
