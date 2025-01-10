package src;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author alessandro.piovani@istat.it
 */

// Classe per rappresentare ciascun elemento della collezione
class ArimaCoefficient {
    private int index;
    private String coef;
    private String type;

    // Costruttore
    public ArimaCoefficient(int index, String coef, String type) {
        this.index = index;
        this.coef = coef;
        this.type = type;
    }

    // Getter
    public int getIndex() {
        return index;
    }

    public String getCoef() {
        return coef;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "ArimaCoefficient{index=" + index + ", coef='" + coef + '\'' + ", type='" + type + "'}";
    }
}

public class ArimaSplitter {

    // Funzione per dividere i coefficienti nelle quattro collezioni
    public static List<List<ArimaCoefficient>> splitArimaCoefficients(List<String> arimaCoefs, List<String> arimaCoefTypes, int p, int bp, int q, int bq) {
        List<List<ArimaCoefficient>> collections = new ArrayList<>();
        collections.add(new ArrayList<>()); // Collezione 1
        collections.add(new ArrayList<>()); // Collezione 2
        collections.add(new ArrayList<>()); // Collezione 3
        collections.add(new ArrayList<>()); // Collezione 4

        int currentIndex = 0;

        // Popolare la prima collezione con i primi p elementi
        for (int i = 0; i < p && currentIndex < arimaCoefs.size(); i++, currentIndex++) {
            collections.get(0).add(new ArimaCoefficient(i + 1, arimaCoefs.get(currentIndex), arimaCoefTypes.get(currentIndex)));
        }

        // Popolare la seconda collezione con i successivi q elementi
        for (int i = 0; i < q && currentIndex < arimaCoefs.size(); i++, currentIndex++) {
            collections.get(1).add(new ArimaCoefficient(i + 1, arimaCoefs.get(currentIndex), arimaCoefTypes.get(currentIndex)));
        }

        // Popolare la terza collezione con i successivi bp elementi
        for (int i = 0; i < bp && currentIndex < arimaCoefs.size(); i++, currentIndex++) {
            collections.get(2).add(new ArimaCoefficient(i + 1, arimaCoefs.get(currentIndex), arimaCoefTypes.get(currentIndex)));
        }

        // Popolare la quarta collezione con i successivi bq elementi
        for (int i = 0; i < bq && currentIndex < arimaCoefs.size(); i++, currentIndex++) {
            collections.get(3).add(new ArimaCoefficient(i + 1, arimaCoefs.get(currentIndex), arimaCoefTypes.get(currentIndex)));
        }

        return collections;
    }

//    // Metodo main per esempio d'uso
//    public static void main(String[] args) {
//        List<String> arimaCoefs = List.of("9", "8", "3", "7", "1", "-6", "5", "4");
//        List<String> arimaCoefTypes = List.of("A", "B", "C", "D", "E", "F", "G", "H");
//        int p = 3, bp = 2, q = 2, bq = 1;
//
//        List<List<ArimaCoefficient>> result = splitArimaCoefficients(arimaCoefs, arimaCoefTypes, p, bp, q, bq);
//
//        for (int i = 0; i < result.size(); i++) {
//            System.out.println("Collection " + (i + 1) + ":" + result.get(i));
//        }
//    }
}
