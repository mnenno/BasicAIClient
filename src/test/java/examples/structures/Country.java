package examples.structures;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Country {
    @JsonProperty("name")
    private String name;

    @JsonProperty("capital")
    private String capital;

    @JsonProperty("languages")
    private List<String> languages;


    // Default constructor (needed for Jackson)
    public Country() {
    }

    // Constructor with all fields
    public Country(String capital, List<String> languages, String name) {
        this.capital = capital;
        this.languages = languages;
        this.name = name;
    }

    // Getters and Setters
    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    // toString method
    @Override
    public String toString() {
        return "Country{" +
                "name='" + name + '\'' +
                ", capital='" + capital + '\'' +
                ", languages=" + languages +
                '}';
    }
}
