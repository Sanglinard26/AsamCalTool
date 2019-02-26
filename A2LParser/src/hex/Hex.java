/*
 * Creation : 26 févr. 2019
 */
package hex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public final class Hex {

    // :BBAAAATTHHHHHH.....HHHHCC
    // 1.BB est le nombre d'octets de données dans la ligne (en hexadécimal)
    // 2.AAAA est l'adresse absolue (ou relative) du début de la ligne
    // 3.TT est le champ spécifiant le type
    // 4.HH...HHHH est le champ des données
    // 5.CC est l'octet de checksum. C'est le complément à deux de la somme des valeurs binaires des octets de tous les autres champs. (Les calculs
    // sont faits sur 8 bits, en ignorant les retenues.)

    public Hex(File hexFile) {
        parse(hexFile);
    }

    private final void parse(File hexFile) {

        final String EOF = ":00000001FF";

        try (BufferedReader buf = new BufferedReader(new FileReader(hexFile))) {

            long start = System.currentTimeMillis();

            String line;
            byte nbByte;
            int adress;
            String typeData;
            String data;
            String checksum;

            int numLine = 0;

            while ((line = buf.readLine()) != null) {
                if (line.charAt(0) == ':') {

                    numLine++;

                    nbByte = Byte.decode("0X" + line.substring(1, 3));
                    System.out.println("Nombre d'octets : " + nbByte);

                    adress = Integer.parseInt(line.substring(3, 7), 16);
                    System.out.println("Adresse : " + adress);

                    switch (line.substring(7, 9)) {
                    case "00":
                        typeData = "Data";
                        break;
                    case "01":
                        typeData = "End of file";
                        break;
                    case "02":
                        typeData = "Champ d'adresse etendue";
                        break;
                    case "03":
                        typeData = "Start Segment Address Record";
                        break;
                    case "04":
                        typeData = "Extended Linear Address Record";
                        break;
                    case "05":
                        typeData = "Start Linear Address Record";
                        break;
                    default:
                        typeData = "Inconnu";
                        break;
                    }
                    System.out.println("Type : " + typeData);

                    int check = 0;

                    for (int i = 1; i < line.length(); i += 2) {
                        check += Integer.parseInt(line.substring(i, i + 2), 16);
                    }

                    if ((check &= 0xff) != 0) {
                        System.out.println("Erreur checksum sur la ligne " + numLine);
                    }
                }

            }

            System.out.println("\nFini en : " + (System.currentTimeMillis() - start) + "ms\n");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
