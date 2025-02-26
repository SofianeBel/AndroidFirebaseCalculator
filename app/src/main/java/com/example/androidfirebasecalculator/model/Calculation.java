package com.example.androidfirebasecalculator.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Modèle de données pour représenter un calcul
 */
public class Calculation {
    private String expression;
    private String result;
    private long timestamp;
    private String id;

    // Constructeur vide requis pour Firebase
    public Calculation() {
    }

    public Calculation(String expression, String result) {
        this.expression = expression;
        this.result = result;
        this.timestamp = System.currentTimeMillis();
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Retourne la représentation complète du calcul (expression = résultat)
     */
    public String getFullCalculation() {
        return expression + " = " + result;
    }

    /**
     * Retourne la date formatée
     */
    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}