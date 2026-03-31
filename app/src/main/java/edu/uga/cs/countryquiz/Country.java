package edu.uga.cs.countryquiz;

/**
 * A POJO class representing a Country.
 */
public class Country {
    private long id;
    private String name;
    private String capital;
    private String continent;
    private String code;

    public Country() {
        this.id = -1;
        this.name = null;
        this.capital = null;
        this.continent = null;
        this.code = null;
    }

    public Country(String name, String capital, String continent, String code) {
        this.id = -1;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public String toString() {
        return id + ": " + name + " (" + capital + "), " + continent + " [" + code + "]";
    }
}
