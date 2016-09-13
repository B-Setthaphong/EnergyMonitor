package com.frederikam.energymonitor.api;

public class EnergyData {

    private final int gkWhCO2;
    private final int windEnergy;
    private final int solarEnergy;
    private final int centralEnergy;
    private final int decentralEnergy;
    private final int usage;

    public EnergyData(int gkWhCO2, int windEnergy, int solarEnergy, int centralEnergy, int decentralEnergy, int usage) {
        this.gkWhCO2 = gkWhCO2;
        this.windEnergy = windEnergy;
        this.solarEnergy = solarEnergy;
        this.centralEnergy = centralEnergy;
        this.decentralEnergy = decentralEnergy;
        this.usage = usage;
    }

    public int getPollution() {
        return gkWhCO2;
    }
    
    public int getFossilEnergy() {
        return centralEnergy + decentralEnergy;
    }

    public int getWindEnergy() {
        return windEnergy;
    }

    public int getSolarEnergy() {
        return solarEnergy;
    }

    public int getUsage() {
        return usage;
    }
    
    public int getProduction() {
        return windEnergy + solarEnergy + getFossilEnergy();
    }
    
    public int getNetto(){
        return getProduction() - usage;
    }

}
