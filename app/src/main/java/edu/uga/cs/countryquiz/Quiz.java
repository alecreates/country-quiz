package edu.uga.cs.countryquiz;

/**
 * A POJO class representing a Quiz.
 */
public class Quiz {
    private long id;
    private String date;
    private String result;

    public Quiz() {
        this.id = -1;
        this.date = null;
        this.result = null;
    }

    public Quiz(String date, String result) {
        this.id = -1;
        this.date = date;
        this.result = result;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getResult() {
        return this.result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
