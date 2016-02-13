/*
 * PruebaRuntime.java
 *
 * Created on 17 de abril de 2005, 15:43
 */

package ar.com.tecsat.loans.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Ejemplo simple de arranque de un programa externo desde java.
 * @author  Chuidiang
 */
public class PruebaRuntime {
    
    /** Creates a new instance of PruebaRuntime */
    public PruebaRuntime() 
    {
        try
        {
            // Se lanza el ejecutable.
            Process p=Runtime.getRuntime().exec("");
            
            // Se obtiene el stream de salida del programa
            InputStream is = p.getInputStream();
            
            // Se prepara un bufferedReader para poder leer la salida más
            // comodamente.
            BufferedReader br = new BufferedReader (new InputStreamReader (is));
            
            // Se lee la primera linea
            String aux = br.readLine();
            
            // Mientras se haya leido alguna linea
            while (aux!=null)
            {
                // Se escribe la linea en pantalla
                System.out.println (aux);
                
                // y se lee la siguiente.
                aux = br.readLine();
            }
        } 
        catch (Exception e)
        {
            // Excepciones si hay algún problema al arrancar el ejecutable o al
            // leer su salida.
            e.printStackTrace();
        }
    }
    
    /**
     * Crea la clase principal que ejecuta el comando dir y escribe en pantalla
     * lo que devuelve dicho comando.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new PruebaRuntime();
    }
    
}
