
package backend;

public class Persona {
   private String nombre;
   private int edad;
   private String numeroTelefono;

    public Persona(String nombre,int edad, String numeroTelefono) {
        this.nombre = nombre;
        this.edad = edad;
        this.numeroTelefono = numeroTelefono;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    
    public String getNumeroTelefono() {
        return numeroTelefono;
    }

    public void setNumeroTelefono(String numeroTelefono) {
        this.numeroTelefono = numeroTelefono;
    }

    @Override
    public String toString() {
        return "Persona{" + "nombre=" + nombre + ", numeroTelefono=" + numeroTelefono + '}';
    }
   
   
}
