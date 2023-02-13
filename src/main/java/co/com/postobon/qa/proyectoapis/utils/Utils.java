package co.com.postobon.qa.proyectoapis.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

public class Utils {

    private static final String AVISO = "\n----------------------------------------------------------------\n%s\n----------------------------------------------------------------";
    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    private Utils() {
    }

    public static void esperaExplicita(long duracion) {
        try {
            String s = formatiarAviso("se pausa el programa por " + (duracion / 1000L) + "s");
            LOGGER.info(s);
            Thread.sleep(duracion);
        } catch (InterruptedException e) {
            LOGGER.info(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    public static String formatiarAviso(String texto) {
        return String.format(AVISO, texto);
    }


/*
    public static void Imprimir(List<String> dato)  {
        FileWriter fw = null;
        BufferedWriter bw = null;
        PrintWriter out = null;
        try {
            java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
          //  System.out.println(<<>>);
            String ruta = "C:\\Users\\rcarot\\Documents\\postobon\\src\\test\\resources\\dataDriven\\nombre_archivo.txt";
            //String contenido = dato;
            File file = new File(ruta);
            // Si el archivo no existe es creado
            if (!file.exists()) {
                file.createNewFile();
            }
            fw = new FileWriter(file,true);
            bw = new BufferedWriter(fw);
            out = new PrintWriter(bw);
            out.println(dato);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(out != null)
                    out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if(bw != null)
                    bw.close();
            } catch (IOException e) {
                //exception handling left as an exercise for the reader
            }
            try {
                if(fw != null)
                    fw.close();
            } catch (IOException e) {
                //exception handling left as an exercise for the reader
            }
        }
    }

*/

    public static void Imprimir(List<String> trazaLista) {
        try {
            FileOutputStream os = new FileOutputStream("src\\test\\resources\\nombre_archivo.txt");
            PrintStream ps = new PrintStream(os);
            ps.println(trazaLista +"\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
