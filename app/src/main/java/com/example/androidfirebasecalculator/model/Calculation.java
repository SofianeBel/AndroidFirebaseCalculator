package com.example.androidfirebasecalculator.model;

import java.util.Date;

/**
 * Classe modèle représentant un calcul
 */
public class Calculation {
    private String id;
    private String userId;
    private String expression;
    private String result;
    private Date timestamp;
    
    // Constructeur vide requis pour Firestore
    public Calculation() {
        timestamp = new Date();
    }
    
    // Constructeur avec expression et résultat
    public Calculation(String expression, String result) {
        this.expression = expression;
        this.result = result;
        this.timestamp = new Date();
    }
    
    // Getters et Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
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
    
    public Date getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    
    /**
     * Retourne une représentation complète du calcul (expression = résultat)
     * @return la chaîne formatée du calcul
     */
    public String getFullCalculation() {
        return expression + " = " + result;
    }
    
    @Override
    public String toString() {
        return "Calculation{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", expression='" + expression + '\'' +
                ", result='" + result + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}