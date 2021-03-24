import org.la4j.Matrix;
import org.la4j.Vector;
import org.la4j.matrix.dense.Basic1DMatrix;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.vector.dense.BasicVector;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

class MainTest {

    FileWriter myWriter2;

    MainTest() throws IOException {
        myWriter2 = new FileWriter("tests.txt");
    }

    public void writeToFile() throws IOException {
        myWriter2.write("formarVetorTest = " + formarVetorTest() + "\n");
        myWriter2.write("convertToVectorTest = " + convertToVectorTest() + "\n");
        myWriter2.write("formarMatrizLeslieTest = " + formarMatrizLeslieTest() + "\n");
        myWriter2.write("getDimensaoPopulacaoTotalTest = " + getDimensaoPopulacaoTotalTest() + "\n");
        myWriter2.write("getDimensaoPopulacaoPorGeracaoTest = " + getDimensaoPopulacaoPorGeracaoTest() + "\n");
        myWriter2.write("distribuicaoTotalDaPopulacaoNormalizadaTest = " + distribuicaoTotalDaPopulacaoNormalizadaTest() + "\n");
        myWriter2.write("distribuicaoDaPopulacaoCadaInstanteTest = " + distribuicaoDaPopulacaoCadaInstanteTest() + "\n");
        myWriter2.write("normalizeTest = " + normalizeTest() + "\n");
        myWriter2.write("getTaxaVariacaoTest = " + getTaxaVariacaoTest() + "\n");
        myWriter2.write("valoresPropriosTest = " + valoresPropriosTest() + "\n");
        myWriter2.write("lambdaTest = " + lambdaTest() + "\n");
        myWriter2.close();
    }

    boolean formarVetorTest() {
        String line = "s0=0.50, s1=0.80, s2=0.50";
        String[] lineSplited = line.trim().split(",");
        double[] result = Main.formarVetor(lineSplited);
        double[] expResult = new double[3];
        expResult[0] = 0.5;
        expResult[1] = 0.8;
        expResult[2] = 0.5;

        for (int i = 0; i < expResult.length; i++) {
            if (result[i] != expResult[i]) {
                return false;
            }
        }
        return true;
    }

    boolean formarMatrizLeslieTest() {

        double[] taxa_sobrevivencia = new double[3];
        taxa_sobrevivencia[0] = 0.5;
        taxa_sobrevivencia[1] = 0.8;
        taxa_sobrevivencia[2] = 0.5;
        double[] taxa_fecundidade = new double[4];
        taxa_fecundidade[0] = 0.5;
        taxa_fecundidade[1] = 2.4;
        taxa_fecundidade[2] = 1;
        taxa_fecundidade[3] = 0;

        Matrix result = Main.formarMatrizLeslie(taxa_sobrevivencia, taxa_fecundidade);

        Matrix expResult = new Basic2DMatrix(4, 4);
        expResult.set(0, 0, 0.5);
        expResult.set(0, 1, 2.4);
        expResult.set(0, 2, 1);
        expResult.set(0, 3, 0);
        expResult.set(1, 0, 0.5);
        expResult.set(1, 1, 0);
        expResult.set(1, 2, 0);
        expResult.set(1, 3, 0);
        expResult.set(2, 0, 0);
        expResult.set(2, 1, 0.8);
        expResult.set(2, 2, 0);
        expResult.set(2, 3, 0);
        expResult.set(3, 0, 0);
        expResult.set(3, 1, 0);
        expResult.set(3, 2, 0.5);
        expResult.set(3, 3, 0);


        for (int i = 0; i < result.rows(); i++) {
            for (int j = 0; j < result.columns(); j++) {
                if(result.get(i, j) != expResult.get(i, j)){
                    return false;
                }
            }
        }
        return true;

    }

    boolean normalizeTest() {
        Matrix normalizeThis = new Basic2DMatrix(3, 4);
        normalizeThis.set(0, 0, 20);
        normalizeThis.set(0, 1, 10);
        normalizeThis.set(0, 2, 40);
        normalizeThis.set(0, 3, 30);
        normalizeThis.set(1, 0, 30);
        normalizeThis.set(1, 1, 40);
        normalizeThis.set(1, 2, 100);
        normalizeThis.set(1, 3, 100);
        normalizeThis.set(2, 0, 40);
        normalizeThis.set(2, 1, 50);
        normalizeThis.set(2, 2, 140);
        normalizeThis.set(2, 3, 90);

        Matrix expectedResult = new Basic2DMatrix(3, 4);
        expectedResult.set(0, 0, 20);
        expectedResult.set(0, 1, 10);
        expectedResult.set(0, 2, 40);
        expectedResult.set(0, 3, 30);
        expectedResult.set(1, 0, 11.111);
        expectedResult.set(1, 1, 14.815);
        expectedResult.set(1, 2, 37.037);
        expectedResult.set(1, 3, 37.037);
        expectedResult.set(2, 0, 12.5);
        expectedResult.set(2, 1, 15.625);
        expectedResult.set(2, 2, 43.750);
        expectedResult.set(2, 3, 28.125);

        Matrix result = Main.normalize(normalizeThis);

        for (int i = 0; i < result.rows(); i++) {
            for (int j = 0; j < result.columns(); j++) {
                if((int) result.get(i, j) != (int) expectedResult.get(i, j)){
                    return false;
                }
            }
        }
        return true;
    }

    boolean getTaxaVariacaoTest() throws IOException {
        Matrix matriz_Leslie = new Basic2DMatrix(4, 4);
        matriz_Leslie.set(0, 0, 0.5);
        matriz_Leslie.set(0, 1, 2.4);
        matriz_Leslie.set(0, 2, 1);
        matriz_Leslie.set(0, 3, 0);
        matriz_Leslie.set(1, 0, 0.5);
        matriz_Leslie.set(1, 1, 0);
        matriz_Leslie.set(1, 2, 0);
        matriz_Leslie.set(1, 3, 0);
        matriz_Leslie.set(2, 0, 0);
        matriz_Leslie.set(2, 1, 0.8);
        matriz_Leslie.set(2, 2, 0);
        matriz_Leslie.set(2, 3, 0);
        matriz_Leslie.set(3, 0, 0);
        matriz_Leslie.set(3, 1, 0);
        matriz_Leslie.set(3, 2, 0.5);
        matriz_Leslie.set(3, 3, 0);

        Matrix P_0 = new Basic1DMatrix(4, 1);

        P_0.set(0, 0, 20);
        P_0.set(1, 0, 10);
        P_0.set(2, 0, 40);
        P_0.set(3, 0, 30);

        double[] result = Main.getTaxaVariacao(matriz_Leslie, 5, P_0);
        double[] expResult = new double[5];

        expResult[0] = 1.12;
        expResult[1] = 1.0535714285714286;
        expResult[2] = 1.6898305084745764;
        expResult[3] = 1.4348044132397193;
        expResult[4] = 1.501328206920657;

        for (int i = 0; i < result.length; i++) {
            if(result[i] != expResult[i]){
                return false;
            }
        }

        return true;
    }

    boolean convertToVectorTest() {
        Matrix m = new Basic2DMatrix(3, 1);
        m.set(0, 0, 1);
        m.set(1, 0, 1);
        m.set(2, 0, 1);

        Vector result = Main.convertToVector(m);

        Vector expResult = new BasicVector(3);
        expResult.set(0, 1);
        expResult.set(1, 1);
        expResult.set(2, 1);


        for (int i = 0; i < result.length(); i++) {
            if(result.get(i) != expResult.get(i)){
                return false;
            }

        }
        return true;
    }

    boolean getDimensaoPopulacaoPorGeracaoTest() {
        Matrix matriz_Leslie = new Basic2DMatrix(4, 4);
        matriz_Leslie.set(0, 0, 0.5);
        matriz_Leslie.set(0, 1, 2.4);
        matriz_Leslie.set(0, 2, 1);
        matriz_Leslie.set(0, 3, 0);
        matriz_Leslie.set(1, 0, 0.5);
        matriz_Leslie.set(1, 1, 0);
        matriz_Leslie.set(1, 2, 0);
        matriz_Leslie.set(1, 3, 0);
        matriz_Leslie.set(2, 0, 0);
        matriz_Leslie.set(2, 1, 0.8);
        matriz_Leslie.set(2, 2, 0);
        matriz_Leslie.set(2, 3, 0);
        matriz_Leslie.set(3, 0, 0);
        matriz_Leslie.set(3, 1, 0);
        matriz_Leslie.set(3, 2, 0.5);
        matriz_Leslie.set(3, 3, 0);

        Matrix P_0 = new Basic1DMatrix(4, 1);
        P_0.set(0, 0, 20);
        P_0.set(1, 0, 10);
        P_0.set(2, 0, 40);
        P_0.set(3, 0, 30);


        double result = Main.getDimensaoPopulacaoPorGeracao(matriz_Leslie, 2, P_0);
        double expResult = 118;

        return result == expResult;
    }

    boolean getDimensaoPopulacaoTotalTest() {
        Matrix matriz_Leslie = new Basic2DMatrix(4, 4);
        matriz_Leslie.set(0, 0, 0.5);
        matriz_Leslie.set(0, 1, 2.4);
        matriz_Leslie.set(0, 2, 1);
        matriz_Leslie.set(0, 3, 0);
        matriz_Leslie.set(1, 0, 0.5);
        matriz_Leslie.set(1, 1, 0);
        matriz_Leslie.set(1, 2, 0);
        matriz_Leslie.set(1, 3, 0);
        matriz_Leslie.set(2, 0, 0);
        matriz_Leslie.set(2, 1, 0.8);
        matriz_Leslie.set(2, 2, 0);
        matriz_Leslie.set(2, 3, 0);
        matriz_Leslie.set(3, 0, 0);
        matriz_Leslie.set(3, 1, 0);
        matriz_Leslie.set(3, 2, 0.5);
        matriz_Leslie.set(3, 3, 0);

        Matrix P_0 = new Basic1DMatrix(4, 1);
        P_0.set(0, 0, 20);
        P_0.set(1, 0, 10);
        P_0.set(2, 0, 40);
        P_0.set(3, 0, 30);

        double[] result = Main.getDimensaoPopulacaoTotal(matriz_Leslie, 5, P_0);

        double[] expResult = new double[5];
        expResult[0] = 100;
        expResult[1] = 112;
        expResult[2] = 118;
        expResult[3] = 199.4;
        expResult[4] = 286.1;

        for (int i = 0; i < result.length; i++) {
            if (result[i] != expResult[i]) {
                return false;
            }
        }

        return true;
    }

    boolean distribuicaoTotalDaPopulacaoNormalizadaTest() {
        Matrix matriz_Leslie = new Basic2DMatrix(4, 4);
        matriz_Leslie.set(0, 0, 0.5);
        matriz_Leslie.set(0, 1, 2.4);
        matriz_Leslie.set(0, 2, 1);
        matriz_Leslie.set(0, 3, 0);
        matriz_Leslie.set(1, 0, 0.5);
        matriz_Leslie.set(1, 1, 0);
        matriz_Leslie.set(1, 2, 0);
        matriz_Leslie.set(1, 3, 0);
        matriz_Leslie.set(2, 0, 0);
        matriz_Leslie.set(2, 1, 0.8);
        matriz_Leslie.set(2, 2, 0);
        matriz_Leslie.set(2, 3, 0);
        matriz_Leslie.set(3, 0, 0);
        matriz_Leslie.set(3, 1, 0);
        matriz_Leslie.set(3, 2, 0.5);
        matriz_Leslie.set(3, 3, 0);

        Matrix P_0 = new Basic1DMatrix(4, 1);
        P_0.set(0, 0, 20);
        P_0.set(1, 0, 10);
        P_0.set(2, 0, 40);
        P_0.set(3, 0, 30);


        Matrix result = Main.distribuicaoTotalDaPopulacaoNormalizada(matriz_Leslie, 2, P_0);
        Matrix expResult = new Basic1DMatrix(4, 1);
        expResult.set(0, 0, 69);
        expResult.set(1, 0, 37);
        expResult.set(2, 0, 8);
        expResult.set(3, 0, 4);
        for (int i = 0; i < result.rows(); i++) {
            if (result.get(i, 0) != expResult.get(i, 0)) {
                return false;
            }
        }

        return true;
    }

    boolean distribuicaoDaPopulacaoCadaInstanteTest(){
        Matrix matriz_Leslie = new Basic2DMatrix(4, 4);
        matriz_Leslie.set(0, 0, 0.5);
        matriz_Leslie.set(0, 1, 2.4);
        matriz_Leslie.set(0, 2, 1);
        matriz_Leslie.set(0, 3, 0);
        matriz_Leslie.set(1, 0, 0.5);
        matriz_Leslie.set(1, 1, 0);
        matriz_Leslie.set(1, 2, 0);
        matriz_Leslie.set(1, 3, 0);
        matriz_Leslie.set(2, 0, 0);
        matriz_Leslie.set(2, 1, 0.8);
        matriz_Leslie.set(2, 2, 0);
        matriz_Leslie.set(2, 3, 0);
        matriz_Leslie.set(3, 0, 0);
        matriz_Leslie.set(3, 1, 0);
        matriz_Leslie.set(3, 2, 0.5);
        matriz_Leslie.set(3, 3, 0);

        Matrix P_0 = new Basic1DMatrix(4, 1);
        P_0.set(0, 0, 20);
        P_0.set(1, 0, 10);
        P_0.set(2, 0, 40);
        P_0.set(3, 0, 30);

        Matrix result = Main.distribuicaoDaPopulacaoCadaInstante(matriz_Leslie,2,P_0);

        Matrix expResult = new Basic2DMatrix(4,3);

        expResult.set(0,0, 20.00);
        expResult.set(1,0, 10.00);
        expResult.set(2,0, 40.00);
        expResult.set(3,0, 30.00);
        expResult.set(0,1, 74.00);
        expResult.set(1,1, 10.00);
        expResult.set(2,1, 8.00);
        expResult.set(3,1, 20.00);
        expResult.set(0,2, 69.00);
        expResult.set(1,2, 37.00);
        expResult.set(2,2, 8.00);
        expResult.set(3,2, 4.00);

        for (int i = 0; i < result.rows(); i++) {
            for (int j = 0; j < result.columns(); j++) {
                if(result.get(i, j) != expResult.get(i, j)){
                    return false;
                }
            }
        }


        return true;
    }

    boolean valoresPropriosTest(){
        Matrix matriz_Leslie = new Basic2DMatrix(4, 4);
        matriz_Leslie.set(0, 0, 0.5);
        matriz_Leslie.set(0, 1, 2.4);
        matriz_Leslie.set(0, 2, 1);
        matriz_Leslie.set(0, 3, 0);
        matriz_Leslie.set(1, 0, 0.5);
        matriz_Leslie.set(1, 1, 0);
        matriz_Leslie.set(1, 2, 0);
        matriz_Leslie.set(1, 3, 0);
        matriz_Leslie.set(2, 0, 0);
        matriz_Leslie.set(2, 1, 0.8);
        matriz_Leslie.set(2, 2, 0);
        matriz_Leslie.set(2, 3, 0);
        matriz_Leslie.set(3, 0, 0);
        matriz_Leslie.set(3, 1, 0);
        matriz_Leslie.set(3, 2, 0.5);
        matriz_Leslie.set(3, 3, 0);

        Matrix[] result = Main.valoresProprios(matriz_Leslie);

        Matrix[] expResult = new Matrix[2];
        Matrix m1 = new Basic2DMatrix(4,4);
        m1.set(0,0,-0.933);
        m1.set(1,0,-0.314);
        m1.set(2,0,-0.169);
        m1.set(3,0,-0.057);
        m1.set(0,1,-1.446);
        m1.set(1,1,1.488);
        m1.set(2,1,-2.220);
        m1.set(3,1,1.863);
        m1.set(0,2,0.544);
        m1.set(1,2,-0.074);
        m1.set(2,2,-0.593);
        m1.set(3,2,1.199);
        m1.set(0,3,0);
        m1.set(1,3,0);
        m1.set(2,3,0);
        m1.set(3,3,1.936);

        Matrix m2 = new Basic2DMatrix(4,4);
        m2.set(0,0,1.488);
        m2.set(1,0,0);
        m2.set(2,0,0);
        m2.set(3,0,0);
        m2.set(0,1,0);
        m2.set(1,1,-0.494);
        m2.set(2,1,-0.158);
        m2.set(3,1,0);
        m2.set(0,2,0);
        m2.set(1,2,0.158);
        m2.set(2,2,-0.494);
        m2.set(3,2,0);
        m2.set(0,3,0);
        m2.set(1,3,0);
        m2.set(2,3,0);
        m2.set(3,3,0);

        expResult[0]=m1;
        expResult[1]=m2;


        for(int x = 0; x<result.length;x++){
            for (int i = 0; i < result[x].rows(); i++) {
                for (int j = 0; j < result[x].columns(); j++) {
                    if(result[x].get(i, j) != expResult[x].get(i, j)){
                        return Math.abs(result[x].get(i, j) - expResult[x].get(i, j)) > 1e-4;
                    }
                }
            }

        }

        return true;
    }

    boolean lambdaTest(){

        Matrix m2 = new Basic2DMatrix(4,4);
        m2.set(0,0,1.488);
        m2.set(1,0,0);
        m2.set(2,0,0);
        m2.set(3,0,0);
        m2.set(0,1,0);
        m2.set(1,1,-0.494);
        m2.set(2,1,-0.158);
        m2.set(3,1,0);
        m2.set(0,2,0);
        m2.set(1,2,0.158);
        m2.set(2,2,-0.494);
        m2.set(3,2,0);
        m2.set(0,3,0);
        m2.set(1,3,0);
        m2.set(2,3,0);
        m2.set(3,3,0);

        double result = Main.lambda(m2);

        double expResult = 1.488;
        return result == expResult;

    }
}