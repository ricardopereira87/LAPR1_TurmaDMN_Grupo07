import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.la4j.Matrix;
import org.la4j.Vector;

import java.awt.*;
import java.io.IOException;

class GNUPlotTest {

    double[] matrizTeste = {12.,23.,123.,167.,233.,300.};
    double[] []aux = {{1,32,5,10,35,2},{3,25,35,76,33,22},{10,12,3,1,0,0}};
    double[] []aux2 = {{1,32,5,10,35,2},{3,25,35,76,33,22},{10,12,3,1,0,0},{3,21,43,53,40,43},{3,43,83,90,50,43}};
    Matrix matrix =  Matrix.from2DArray(aux);
    Matrix matrix2 =  Matrix.from2DArray(aux2);
    @BeforeEach
    void setUp(){

    }

    @Test
    void gnuplot() throws IOException, InterruptedException {
        GNUPlot gnu = new GNUPlot();
        gnu.setTaxa_variacao(matrizTeste);
        String dados = gnu.datFileBuilder(matrizTeste,"comentario");
        String scriptVer = gnu.scriptBuilder(0,"Crescimento",dados);
        gnu.writeToFile("scriptVer",scriptVer);
        gnu.plot("scriptVer");
        String scriptGerar = gnu.scriptBuilder(1,"Crescimento",dados);
        gnu.writeToFile("script",scriptGerar);
        gnu.plot("script");

    }

    @Test
    void exemploMain() throws IOException, InterruptedException {
        GNUPlot gnu = new GNUPlot();
        gnu.setTaxa_variacao(matrizTeste);
        gnu.gerarScriptCrescimentoPopulacao(0, "scriptExemploVer" ); //output 0 é abrir o grafico
        gnu.plot("scriptExemploVer");
        int decisao = 2;
        gnu.gerarScriptCrescimentoPopulacao(decisao,"scriptExemploTexto");
        gnu.plot("scriptExemploTexto");

    }



    @Test
    void exemploNaoNormalizada() throws IOException, InterruptedException {
        GNUPlot gnu = new GNUPlot();
        gnu.setDistribuicao_cada_instante(matrix2);
        gnu.gerarScriptNumeroPorClasse(0, "scriptVer" ); //output 0 é abrir o grafico
        gnu.plot("scriptVer");
        int decisao = 2;
        gnu.gerarScriptNumeroPorClasse(decisao,"script1");
        gnu.plot("script1");

    }

    @Test
    void oneForEach() throws IOException, InterruptedException {
        GNUPlot gnu = new GNUPlot(matrix2,matrix,matrizTeste,matrizTeste);
        int decisao = 1;
        gnu.gerarScriptNumeroPorClasseNormalizado(decisao,"script99","normalizado");
        gnu.plot("script99");
        gnu.gerarScriptNumeroPorClasse(decisao,"script98","naoNormalizado");
        gnu.plot("script98");
        gnu.gerarScriptCrescimentoPopulacao(decisao,"script97","crescimentoPopulacao");
        gnu.plot("script97");
        gnu.gerarScriptNumTotalIndividuos(decisao,"script96","numTotalIndividuos");
        gnu.plot("script96");

    }
    @Test
    void plot() throws IOException, InterruptedException {
        GNUPlot gnu = new GNUPlot();

    }
}