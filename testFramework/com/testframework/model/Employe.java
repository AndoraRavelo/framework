package testFramework.com.testframework.model;

public class Employe {
    private String nom;
    private String prenom;
    private Integer age;
    private String poste;
    private Departement dept;

    public Employe() {}

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getPoste() { return poste; }
    public void setPoste(String poste) { this.poste = poste; }

    public Departement getDept() { return dept; }
    public void setDept(Departement dept) { this.dept = dept; }

    
}
