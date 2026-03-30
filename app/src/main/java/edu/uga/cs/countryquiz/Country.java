package edu.uga.cs.countryquiz;

// POJO class representing a single country
public class Country {
    private long id;
    private String countryName;
    private String capital;
    private String continent;
    private String code;

    public Country()
    {
        this.id = -1;
        this.countryName = null;
        this.capital = null;
        this.continent = null;
        this.code = null;
    }

    public Country( String countryName, String capital, String continent, String code) {
        this.id = -1;
        this.countryName = countryName;
        this.capital = capital;
        this.continent = continent;
        this.code = code;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
