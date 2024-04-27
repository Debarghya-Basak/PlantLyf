package com.db.plantlyf.Model;

public class PlantNutritionWaterModel {

    public String plantName;
    public String plantNutritionWater;

    public PlantNutritionWaterModel(String plantName, String plantNutritionWater) {
        this.plantName = plantName;
        this.plantNutritionWater = plantNutritionWater;
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public String getPlantNutritionWater() {
        return plantNutritionWater;
    }

    public void setPlantNutritionWater(String plantNutritionWater) {
        this.plantNutritionWater = plantNutritionWater;
    }
}
