package com.dynious.biota.lib;

public class MathLib
{
    public static float getFittedValue(float currentValue, float min, float normal, float max)
    {
        /*
        y = (x-x2)(x-x3) / (x1-x2)(x1-x3) * y1 +
            (x-x1)(x-x3) / (x2-x1)(x2-x3) * y2 +
            (x-x1)(x-x2) / (x3-x1)(x3-x2) * y3
        */
        //TODO: This worked different than I thought, might be broken?
        return 4 * (((currentValue-normal)*(currentValue-max) / (min-normal)*(min-max)) * Settings.MINIMAL_PLANT_GROWTH +
                ((currentValue-min)*(currentValue-max) / (normal-min)*(normal-max)) * Settings.NORMAL_PLANT_GROWTH +
                ((currentValue-min)*(currentValue-normal) / (max-min)*(max-normal)) * Settings.MAX_PLANT_GROWTH);
    }
}
