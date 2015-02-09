package com.dynious.biota.api;

/**
 * This will make your plant be detected by the retrogenerator of Biota. You should ALWAYS implement
 * this if you call the onPantBlockAdded/Removed methods!
 *
 * When implementing MAKE SURE you call IBiotaAPI#onPlantBlockAdded, IBiotaAPI#onPlantBlockRemoved,
 * IBiotaAPI#onPlantTick and (if wanted) IBiotaAPI#getPlantColorMultiplier like specified in the JavaDoc of those
 * methods!
 */
public interface IPlant
{
}
