package com.example.remedy;

public class Remedy {
    private String title;
    private String category;
    private boolean common;
    private String ingredients;
    private String description;
    private String steps;
    private String benefits;
    private String safe;
    private String avoid;
    private String caution;
    private String doctor;
    private String image;

    public Remedy(String title, String category, boolean common,
                  String ingredients, String description,
                  String steps, String benefits,
                  String safe, String avoid,
                  String caution, String doctor,
                  String image) {

        this.title = title;
        this.category = category;
        this.common = common;
        this.ingredients = ingredients;
        this.description = description;
        this.steps = steps;
        this.benefits = benefits;
        this.safe = safe;
        this.avoid = avoid;
        this.caution = caution;
        this.doctor = doctor;
        this.image = image;
    }

    public String getTitle() { return title; }

    public String getCategory() { return category; }

    public boolean isCommon() { return common; }

    public String getIngredients() { return ingredients; }

    public String getDescription() { return description; }

    public String getSteps() { return steps; }

    public String getBenefits() { return benefits; }

    public String getSafe() { return safe; }

    public String getAvoid() { return avoid; }

    public String getCaution() { return caution; }

    public String getDoctor() { return doctor; }

    public String getImage() { return image; }


}
