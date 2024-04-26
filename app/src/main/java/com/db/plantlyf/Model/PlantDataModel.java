package com.db.plantlyf.Model;

public class PlantDataModel {

    public String plantName;
    public int plantCount;

    public PlantDataModel(String plantName, int plantCount) {
        this.plantName = plantName;
        this.plantCount = plantCount;
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public int getPlantCount() {
        return plantCount;
    }

    public void setPlantCount(int plantCount) {
        this.plantCount = plantCount;
    }
}
