package com.dynious.biota.biosystem;

import com.dynious.biota.lib.Settings;
import net.minecraft.util.MathHelper;

public class ClientBioSystem
{
    private float phosphorus;
    private float potassium;
    private float nitrogen;

    public ClientBioSystem(float phosphorus, float potassium, float nitrogen)
    {
        this.phosphorus = phosphorus;
        this.potassium = potassium;
        this.nitrogen = nitrogen;
    }

    public int recolorPlants(int original)
    {
        int r = (original & 0xFF0000) >> 16;
        int g = (original & 0xFF00) >> 8;
        int b = original & 0xFF;

        float phosphorusDifferenceFromNormal = (phosphorus / (Settings.NORMAL_PHOSPHORUS)) - 1;
        if (phosphorusDifferenceFromNormal > 0.1F)
            phosphorusDifferenceFromNormal = 0.1F;
        float phosphorusColorChange = (float) Math.pow(phosphorusDifferenceFromNormal, 3);
        r += (int) (phosphorusColorChange*255/2);
        g += (int) (phosphorusColorChange*255/2);
        b -= phosphorusColorChange*255;

        float potassiumDifferenceFromNormal = (potassium / (Settings.NORMAL_POTASSIUM)) - 1;
        if (potassiumDifferenceFromNormal > 0.1F)
            potassiumDifferenceFromNormal = 0.1F;
        float potassiumColorChange = (float) Math.pow(potassiumDifferenceFromNormal, 3);
        r -= (int) (potassiumColorChange*255/2);
        b += (int) (potassiumColorChange*255/2);

        float nitrogenDifferenceFromNormal = (nitrogen / (Settings.NORMAL_NITROGEN)) - 1;
        if (nitrogenDifferenceFromNormal > 0.1F)
            nitrogenDifferenceFromNormal = 0.1F;
        float nitrogenColorChange = (float) Math.pow(nitrogenDifferenceFromNormal, 3);
        r -= (int) (nitrogenColorChange*255/2);
        g -= (int) (nitrogenColorChange*255/2);
        b += nitrogenColorChange*b;

        r = MathHelper.clamp_int(r, 0, 255);
        g = MathHelper.clamp_int(g, 0, 255);
        b = MathHelper.clamp_int(b, 0, 255);

        return (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF;
    }
}
