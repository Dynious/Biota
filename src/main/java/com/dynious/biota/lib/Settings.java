package com.dynious.biota.lib;

public class Settings
{
    public static final float NORMAL_PHOSPHORUS = 15F;
    public static final float DELTA_PHOSPHORUS = 5F;

    public static final float NORMAL_POTASSIUM = 200F;
    public static final float DELTA_POTASSIUM = 50F;

    public static final float NORMAL_NITROGEN = 7.5F;
    public static final float DELTA_NITROGEN = 2.5F;

    public static final float NUTRIENT_SHORTAGE_STOP_GROWTH = 0.75F;
    public static final float NUTRIENT_SHORTAGE_DEATH = 0.35F;


    public static final int TICKS_PER_BIOSYSTEM_UPDATE = 200;

    //24000 ticks per MC day. One day for 1.0 change.
    public static final float BIOSYSTEM_CHANGE_RATE = (float) TICKS_PER_BIOSYSTEM_UPDATE /24000;

    //168000 ticks per MC week. One week for 1.0 change in spread.
    public static final float BIOSYSTEM_SPREAD_RATE = (float) TICKS_PER_BIOSYSTEM_UPDATE /168000;
}
