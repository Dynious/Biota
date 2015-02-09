package com.dynious.biota.lib;

public class MathLib
{
    public static float getFittedValue(float lowestGrowthValue)
    {
        /*
        y = (x-x2)(x-x3) / (x1-x2)(x1-x3) * y1 +
            (x-x1)(x-x3) / (x2-x1)(x2-x3) * y2 +
            (x-x1)(x-x2) / (x3-x1)(x3-x2) * y3
        */
        //TODO: This worked different than I thought, might be broken?
        return 4 * (((lowestGrowthValue-1)*(lowestGrowthValue-Settings.NUTRIENT_ABUNDANCE_FOR_MAX_GROWTH) / (Settings.NUTRIENT_SHORTAGE_FOR_STOP_GROWTH -1)*(Settings.NUTRIENT_SHORTAGE_FOR_STOP_GROWTH -Settings.NUTRIENT_ABUNDANCE_FOR_MAX_GROWTH)) * Settings.MINIMAL_PLANT_GROWTH +
                ((lowestGrowthValue-Settings.NUTRIENT_SHORTAGE_FOR_STOP_GROWTH)*(lowestGrowthValue-Settings.NUTRIENT_ABUNDANCE_FOR_MAX_GROWTH) / (1-Settings.NUTRIENT_SHORTAGE_FOR_STOP_GROWTH)*(1-Settings.NUTRIENT_ABUNDANCE_FOR_MAX_GROWTH)) * Settings.NORMAL_PLANT_GROWTH +
                ((lowestGrowthValue-Settings.NUTRIENT_SHORTAGE_FOR_STOP_GROWTH)*(lowestGrowthValue-1) / (Settings.NUTRIENT_ABUNDANCE_FOR_MAX_GROWTH -Settings.NUTRIENT_SHORTAGE_FOR_STOP_GROWTH)*(Settings.NUTRIENT_ABUNDANCE_FOR_MAX_GROWTH -1)) * Settings.MAX_PLANT_GROWTH);
    }
}
