package com.db.plantlyf.Model;

public class PlantNutritionModel {

    public String plantName;
    public String plantNutrition;

    public PlantNutritionModel(String plantName, String plantNutrition) {
        this.plantName = plantName;
        this.plantNutrition = plantNutrition;
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public String getPlantNutrition() {
        return plantNutrition;
    }

    public void setPlantNutrition(String plantNutrition) {
        this.plantNutrition = plantNutrition;
    }
}
