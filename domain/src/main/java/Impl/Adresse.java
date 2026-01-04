package Impl;

public class Adresse {
    private final String pays, ville, rue, lieuDit;
    private final int numero;
    private final double[] positionGps; // [lat, lon]

    public Adresse(String pays, String ville, String rue, int numero, String lieuDit, double lat, double lon) {
        this.pays = pays; this.ville = ville; this.rue = rue; this.numero = numero; this.lieuDit = lieuDit;
        this.positionGps = new double[] {lat, lon};
    }
    public String getPays() { return pays; }
    public String getVille() { return ville; }
    public String getRue() { return rue; }
    public int getNumero() { return numero; }
    public String getLieuDit() { return lieuDit; }
    public double[] getPositionGps() { return positionGps; }
}
