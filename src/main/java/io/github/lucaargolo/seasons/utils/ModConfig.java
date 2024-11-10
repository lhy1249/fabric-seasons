package io.github.lucaargolo.seasons.utils;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal", "MismatchedQueryAndUpdateOfCollection", "unused"})
public class ModConfig {

    private static class SeasonLock {
        private boolean isSeasonLocked = false;
        private Season lockedSeason = Season.SPRING;
    }

    private static class SeasonLength {
        private int springLength = 672000 * 3;
        private int summerLength = 672000 * 3;
        private int fallLength = 672000 * 3;
        private int winterLength = 672000 * 3;

    }

    private Season startingSeason = Season.SPRING;

    private SeasonLength seasonLength = new SeasonLength();

    private SeasonLock seasonLock = new SeasonLock();


    private List<String> dimensionAllowlist = List.of(
            "minecraft:overworld"
    );
    
    private boolean doTemperatureChanges = true;

    private boolean shouldSnowReplaceVegetation = true;

    private boolean shouldSnowyBiomesMeltInSummer = true;

    private boolean shouldIceBreakSugarCane = false;

    private List<String> biomeDenylist = List.of(
            "terralith:glacial_chasm",
            "minecraft:frozen_ocean",
            "minecraft:deep_frozen_ocean",
            "minecraft:cold_ocean",
            "minecraft:deep_cold_ocean",
            "minecraft:ocean",
            "minecraft:deep_ocean",
            "minecraft:lukewarm_ocean",
            "minecraft:deep_lukewarm_ocean",
            "minecraft:warm_ocean"
    );

    private List<String> biomeForceSnowInWinterList = List.of(
            "minecraft:plains",
            "minecraft:sunflower_plains",
            "minecraft:stony_peaks"
    );

    private boolean isSeasonTiedWithSystemTime = false;
    private boolean isInNorthHemisphere = true;
    private boolean isFallAndSpringReversed = false;
    private boolean notifyCompat = true;

    private boolean debugCommandEnabled = false;

    public boolean shouldNotifyCompat() {
        return notifyCompat;
    }

    public boolean doTemperatureChanges(Identifier biomeId) {
        return doTemperatureChanges && !biomeDenylist.contains(biomeId.toString());
    }

    public boolean isSnowForcedInBiome(Identifier biomeId) {
        return biomeForceSnowInWinterList.contains(biomeId.toString());
    }

    public boolean shouldSnowReplaceVegetation() {
        return shouldSnowReplaceVegetation;
    }

    public boolean shouldSnowyBiomesMeltInSummer() {
        return shouldSnowyBiomesMeltInSummer;
    }

    public boolean shouldIceBreakSugarCane() {
        return shouldIceBreakSugarCane;
    }

    public int getSpringLength() {
        return seasonLength.springLength;
    }

    public int getSummerLength() {
        return seasonLength.summerLength;
    }

    public int getFallLength() {
        return seasonLength.fallLength;
    }

    public int getWinterLength() {
        return seasonLength.winterLength;
    }

    public int getYearLength() {
        return seasonLength.springLength + seasonLength.summerLength + seasonLength.fallLength + seasonLength.winterLength;
    }

    @Deprecated
    public int getSeasonLength() {
        return getSpringLength();
    }

    public boolean isSeasonLocked() {
        return seasonLock.isSeasonLocked;
    }

    public Season getLockedSeason() {
        return seasonLock.lockedSeason;
    }

    public Season getStartingSeason() {
        return startingSeason;
    }

    public boolean isValidStartingSeason() {
        return switch(startingSeason) {
            case SPRING -> true;
            case SUMMER -> true;
            case FALL -> true;
            case WINTER -> true;
            default -> false;
        };
    }

    public boolean isValidInDimension(RegistryKey<World> dimension) {
        return dimensionAllowlist.contains(dimension.getValue().toString());
    }

    public boolean isSeasonTiedWithSystemTime() {
        return isSeasonTiedWithSystemTime;
    }

    public boolean isInNorthHemisphere() {
        return isInNorthHemisphere;
    }

    public boolean isFallAndSpringReversed() {
        return isFallAndSpringReversed;
    }

    public boolean isDebugCommandEnabled() { return debugCommandEnabled; }

}
