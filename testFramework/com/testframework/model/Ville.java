package testFramework.com.testframework.model;

public class Ville {
    private Integer id;
    private String nom;
    private String codePostal;

    public Ville() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getCodePostal() { return codePostal; }
    public void setCodePostal(String codePostal) { this.codePostal = codePostal; }
}
