import org.la4j.Matrix;
import org.la4j.Vector;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GNUPlot {


    private String path;
    public static final String PATH = "src/main/files/";
    public static final String SCRIPT_NUM_POPULACAO =  "scriptNumPopulacao";
    public static final String SCRIPT_CRESCIMENTO =  "scriptCrescimento";
    public static final String SCRIPT_NUM_POR_CLASSE =  "scriptNumPorClasse";
    public static final String SCRIPT_NUM_POR_CLASSE_NORMALIZADO =  "scriptNumPorClasseNormalizado";

    private Matrix normalizada;  //numero por classe(normalizado)
    private Matrix distribuicao_cada_instante;  //numero por classe(nao normalizado)
    private double[] taxa_variacao;  // crescimento populacao
    private double[] dimensaoPopTotalGeracoes;   //Numero Total de Individuos

    public GNUPlot(Matrix normalizada, Matrix distribuicao_cada_instante, double[] taxa_variacao, double[] dimensaoPopTotalGeracoes) {
        this.normalizada=normalizada;
        this.distribuicao_cada_instante=distribuicao_cada_instante;
        this.taxa_variacao=taxa_variacao;
        this.dimensaoPopTotalGeracoes=dimensaoPopTotalGeracoes;
    }

    public GNUPlot(){

    }

    public void setNormalizada(Matrix normalizada) {
        this.normalizada = normalizada;
    }

    public void setDistribuicao_cada_instante(Matrix distribuicao_cada_instante) {
        this.distribuicao_cada_instante = distribuicao_cada_instante;
    }

    public void setTaxa_variacao(double[] taxa_variacao) {
        this.taxa_variacao = taxa_variacao;
    }

    public void setDimensaoPopTotalGeracoes(double[] dimensaoPopTotalGeracoes) {
        this.dimensaoPopTotalGeracoes = dimensaoPopTotalGeracoes;
    }

    public void gerarScriptNumTotalIndividuos(int output, String nomeFicheiro) throws IOException {
        String dataFicheiro = datFileBuilder(dimensaoPopTotalGeracoes,"num total de individuos");
        String scriptCompleto = scriptBuilder(output,"NumPopulacao",dataFicheiro);
        writeToFile(nomeFicheiro,scriptCompleto);

    }

    public void gerarScriptCrescimentoPopulacao(int output, String nomeFicheiro ) throws IOException {
        String dataFicheiro = datFileBuilder(taxa_variacao,"crescimentoDePopulacao");
        String scriptCompleto = scriptBuilder(output,"Crescimento",dataFicheiro);
        writeToFile(nomeFicheiro,scriptCompleto);
    }

    public void gerarScriptNumeroPorClasse(int output, String nomeFicheiro) throws IOException {
        String dataFicheiro = "";
        for (int i = 0; i <distribuicao_cada_instante.getColumn(0).length(); i++) {
            dataFicheiro += datFileBuilder(distribuicao_cada_instante.getRow(i), "geracao " + (i+1) );
        }
        String scriptCompleto = scriptBuilder(output,"NumPorClasse",dataFicheiro,distribuicao_cada_instante.getColumn(0).length());
        writeToFile(nomeFicheiro,scriptCompleto);

    }


    public void gerarScriptNumeroPorClasseNormalizado(int output, String nomeFicheiro) throws IOException {
        String dataFicheiro = "";
        for (int i = 0; i <normalizada.getColumn(0).length(); i++) {
            dataFicheiro += datFileBuilder(normalizada.getRow(i), "geracao " + i+1);
        }
        String scriptCompleto = scriptBuilder(output,"NumPorClasseNormalizado",dataFicheiro,distribuicao_cada_instante.getColumn(0).length());
        writeToFile(nomeFicheiro,scriptCompleto);
    }

    public void plot(String scriptFile) throws IOException, InterruptedException {
        Path absPath = Paths.get(scriptFile).toAbsolutePath();
        Runtime command = Runtime.getRuntime();
        String comando = "gnuplot \"" + absPath +".txt\" -p";
        System.out.println(comando);
        Process p = command.exec(comando);
        p.waitFor();
    }

    public String datFileBuilder(double[] eixoY, String nomeHeader){
        int[] eixoX = new int[eixoY.length];
        //preencher o array do eixo do x com tempo t que vai de 0 até a length do array do eixo Y
        for (int i = 0; i < eixoY.length; i++) {
            eixoX[i] = i;
        }
        String ret = "#\"" + nomeHeader + "\"";
        for (int i = 0; i < eixoX.length; i++) {
            ret += "\n"+eixoX[i] + " " + eixoY[i];
        }
        ret += "\n";
        ret += "\n";
        return ret;
    }

    private String datFileBuilder(Vector eixoY, String nomeHeader){
        int[] eixoX = new int[eixoY.length()];
        //preencher o array do eixo do x com tempo t que vai de 0 até a length do array do eixo Y
        for (int i = 0; i < eixoY.length(); i++) {
            eixoX[i] = i;
        }
        String ret = "\n\"" + nomeHeader + "\"";
        for (int i = 0; i < eixoX.length; i++) {
            ret += "\n"+eixoX[i] + " " + eixoY.get(i);
        }
        ret += "\n";
        ret += "\n";
        return ret;
    }

    public void writeToFile(String nomeFicheiro, String toWrite) throws IOException {
        File file = new File(nomeFicheiro+".txt");
        // alguma coisa com o gnuplot não me deixa dar overwrite aos ficheiros?
        // dando delete mas deveria ser irrelevante
        if (file.exists()) file.delete();
        file.createNewFile();
        System.out.println("file created");
        FileWriter fw = new FileWriter(file);
        fw.write(toWrite);
        fw.close();
    }

    /**
     *
     *
     * @param output 0 = ver grafico 1= png   2= txt    3=eps
     * @param tipoGrafico
     */
    public String scriptBuilder ( int output, String tipoGrafico, String data){
        String dados = "$data << EOD\n" +
                data +
                "\nEOD\n";
        String ret = "";
        switch (output){
            case 0: ret+= "set terminal wxt\n" +
                    "set output '"+tipoGrafico+".png'\n";
                break;
            case 1: ret +="set terminal png size 500,400 \n" +
                    "set output '"+tipoGrafico+".png'\n";
                break;
            case 2: ret +="set terminal dumb \n" +
                    "set output '"+tipoGrafico+".txt'\n";
                break;
            case 3: ret +="set terminal eps  \n" +
                    "set output '"+tipoGrafico+".eps'\n";
                break;
            default: throw new IllegalArgumentException("output type is wrong! 1,2 or 3");
        }
        switch (tipoGrafico){
            case "Crescimento":
                ret += dados;
                ret += "set xlabel 'tempo (t)'\n" +
                        "set ylabel 'Δt'\n" +
                        "set title 'Crescimento da População'\n" +
                        "plot '$data' using 1:2 title columnheader with linespoints\n";
                break;
            case "NumPopulacao":
                ret+= dados;
                ret += "set xlabel 'tempo (t)'\n" +
                        "set ylabel 'numero de elementos (n)'\n" +
                        "set title 'Numero total de Indivíduos em relação com o tempo'\n" +
                        "set boxwidth 0.5\n" +
                        "set style fill solid\n" +
                        "plot '$data' using 2:xtic(1) with boxes title ''\n";
                break;
            case "NumPorClasse":
                ret +=dados;
                ret += "set key inside top left\n" +
                        "set xlabel 'tempo (t)'\n" +
                        "set ylabel 'numero de elementos (n)'\n" +
                        "set title 'Número Por Classe(não normalizado)'\n" +
                        "plot for [i=0:2] '$data' index i with lines title columnheader(1)\n";
                break;
            case"NumPorClasseNormalizado":
                ret+=dados;
                ret+= "set key inside top left\n" +
                        "set xlabel 'tempo (t)'\n" +
                        "set ylabel 'numero de elementos (n)'\n" +
                        "set title 'Número Por Classe(normalizado)'\n" +
                        "plot for [i=0:2] '$data' index i with lines title columnheader(1)\n";
                break;
            default: throw new IllegalArgumentException("Tipo de gráfico nao especificado");
        }
        return ret;
    }

    public String scriptBuilder ( int output, String tipoGrafico, String data,int numGeracoes){
        String dados = "$data << EOD\n" +
                data +
                "\nEOD\n";
        String ret = "";
        switch (output){
            case 0: ret+= "set terminal wxt\n" +
                    "set output '"+tipoGrafico+".png'\n";
                break;
            case 1: ret +="set terminal png size 500,400 \n" +
                    "set output '"+tipoGrafico+".png'\n";
                break;
            case 2: ret +="set terminal dumb \n" +
                    "set output '"+tipoGrafico+".txt'\n";
                break;
            case 3: ret +="set terminal eps  \n" +
                    "set output '"+tipoGrafico+".eps'\n";
                break;
            default: throw new IllegalArgumentException("output type is wrong! 1,2 or 3");
        }
        switch (tipoGrafico){
            case "Crescimento":
                ret += dados;
                ret += "set xlabel 'tempo (t)'\n" +
                        "set ylabel 'Δt'\n" +
                        "set title 'Crescimento da População'\n" +
                        "plot '$data' using 1:2 title columnheader with linespoints\n";
                break;
            case "NumPopulacao":
                ret+= dados;
                ret += "set xlabel 'tempo (t)'\n" +
                        "set ylabel 'numero de elementos (n)'\n" +
                        "set title 'Numero total de Indivíduos em relação com o tempo'\n" +
                        "set boxwidth 0.5\n" +
                        "set style fill solid\n" +
                        "plot '$data' using 2:xtic(1) with boxes title ''\n";
                break;
            case "NumPorClasse":
                ret +=dados;
                ret += "set key inside top left\n" +
                        "set xlabel 'tempo (t)'\n" +
                        "set ylabel 'numero de elementos (n)'\n" +
                        "set title 'Número Por Classe(não normalizado)'\n" +
                        "plot for [i=0:"+(numGeracoes-1)+"] '$data' index i with lines title columnheader(1)\n";
                break;
            case"NumPorClasseNormalizado":
                ret+=dados;
                ret+= "set key inside top left\n" +
                        "set xlabel 'tempo (t)'\n" +
                        "set ylabel 'numero de elementos (n)'\n" +
                        "set title 'Número Por Classe(normalizado)'\n" +
                        "plot for [i=0:"+(numGeracoes-1)+"] '$data' index i with lines title columnheader(1)\n";
                break;
            default: throw new IllegalArgumentException("Tipo de gráfico nao especificado");
        }
        return ret;
    }


    public String scriptBuilder ( int output, String tipoGrafico, String data, String nomeGrafico){
        String dados = "$data << EOD\n" +
                data +
                "\nEOD\n";
        String ret = "";
        switch (output){
            case 0: ret+= "set terminal wxt\n" +
                    "set output '"+ nomeGrafico+".png'\n";
                break;
            case 1: ret +="set terminal png size 500,400 \n" +
                    "set output '"+ nomeGrafico+".png'\n";
                break;
            case 2: ret +="set terminal dumb \n" +
                    "set output '"+ nomeGrafico+".txt'\n";
                break;
            case 3: ret +="set terminal eps  \n" +
                    "set output '"+ nomeGrafico+".eps'\n";
                break;
            default: throw new IllegalArgumentException("output type is wrong! 1,2 or 3");
        }
        switch (tipoGrafico){
            case "Crescimento":
                ret += dados;
                ret += "set xlabel 'tempo (t)'\n" +
                        "set ylabel 'Δt'\n" +
                        "set title 'Crescimento da População'\n" +
                        "plot '$data' using 1:2 title columnheader with linespoints\n";
                break;
            case "NumPopulacao":
                ret+= dados;
                ret += "set xlabel 'tempo (t)'\n" +
                        "set ylabel 'numero de elementos (n)'\n" +
                        "set title 'Numero total de Indivíduos em relação com o tempo'\n" +
                        "set boxwidth 0.5\n" +
                        "set style fill solid\n" +
                        "plot '$data' using 2:xtic(1) with boxes title ''\n";
                break;
            case "NumPorClasse":
                ret +=dados;
                ret += "set key inside top left\n" +
                        "set xlabel 'tempo (t)'\n" +
                        "set ylabel 'numero de elementos (n)'\n" +
                        "set title 'Número Por Classe(não normalizado)'\n" +
                        "plot for [i=0:2] '$data' index i with lines title columnheader(1)\n";
                break;
            case"NumPorClasseNormalizado":
                ret+=dados;
                ret+= "set key inside top left\n" +
                        "set xlabel 'tempo (t)'\n" +
                        "set ylabel 'numero de elementos (n)'\n" +
                        "set title 'Número Por Classe(normalizado)'\n" +
                        "plot for [i=0:2] '$data' index i with lines title columnheader(1)\n";
                break;
            default: throw new IllegalArgumentException("Tipo de gráfico nao especificado");
        }
        return ret;
    }

    public String scriptBuilder ( int output, String tipoGrafico, String data,int numGeracoes,String nomeGrafico){
        String dados = "$data << EOD\n" +
                data +
                "\nEOD\n";
        String ret = "";
        switch (output){
            case 0: ret+= "set terminal wxt\n" +
                    "set output '"+ nomeGrafico+".png'\n";
                break;
            case 1: ret +="set terminal png size 500,400 \n" +
                    "set output '"+ nomeGrafico+".png'\n";
                break;
            case 2: ret +="set terminal dumb \n" +
                    "set output '"+ nomeGrafico+".txt'\n";
                break;
            case 3: ret +="set terminal eps  \n" +
                    "set output '"+ nomeGrafico+".eps'\n";
                break;
            default: throw new IllegalArgumentException("output type is wrong! 1,2 or 3");
        }
        switch (tipoGrafico){
            case "Crescimento":
                ret += dados;
                ret += "set xlabel 'tempo (t)'\n" +
                        "set ylabel 'Δt'\n" +
                        "set title 'Crescimento da População'\n" +
                        "plot '$data' using 1:2 title columnheader with linespoints\n";
                break;
            case "NumPopulacao":
                ret+= dados;
                ret += "set xlabel 'tempo (t)'\n" +
                        "set ylabel 'numero de elementos (n)'\n" +
                        "set title 'Numero total de Indivíduos em relação com o tempo'\n" +
                        "set boxwidth 0.5\n" +
                        "set style fill solid\n" +
                        "plot '$data' using 2:xtic(1) with boxes title ''\n";
                break;
            case "NumPorClasse":
                ret +=dados;
                ret += "set key inside top left\n" +
                        "set xlabel 'tempo (t)'\n" +
                        "set ylabel 'numero de elementos (n)'\n" +
                        "set title 'Número Por Classe(não normalizado)'\n" +
                        "plot for [i=0:"+(numGeracoes-1)+"] '$data' index i with lines title columnheader(1)\n";
                break;
            case"NumPorClasseNormalizado":
                ret+=dados;
                ret+= "set key inside top left\n" +
                        "set xlabel 'tempo (t)'\n" +
                        "set ylabel 'numero de elementos (n)'\n" +
                        "set title 'Número Por Classe(normalizado)'\n" +
                        "plot for [i=0:"+(numGeracoes-1)+"] '$data' index i with lines title columnheader(1)\n";
                break;
            default: throw new IllegalArgumentException("Tipo de gráfico nao especificado");
        }
        return ret;
    }


    public void open(String ficheiro){
        File file = new File("graf");

    }

    public void gerarScriptNumeroPorClasseNormalizado(int output, String nomeFicheiro,String nomeGrafico) throws IOException {
        String dataFicheiro = "";
        for (int i = 0; i <normalizada.getColumn(0).length(); i++) {
            dataFicheiro += datFileBuilder(normalizada.getRow(i), "geracao " + i+1);
        }
        String scriptCompleto = scriptBuilder(output,"NumPorClasseNormalizado",dataFicheiro,distribuicao_cada_instante.getColumn(0).length(),nomeGrafico);
        writeToFile(nomeFicheiro,scriptCompleto);
    }

    public void gerarScriptNumTotalIndividuos(int output, String nomeFicheiro,String nomeGrafico) throws IOException {
        String dataFicheiro = datFileBuilder(dimensaoPopTotalGeracoes,"num total de individuos");
        String scriptCompleto = scriptBuilder(output,"NumPopulacao",dataFicheiro,nomeGrafico);
        writeToFile(nomeFicheiro,scriptCompleto);

    }

    public void gerarScriptCrescimentoPopulacao(int output, String nomeFicheiro,String nomeGrafico ) throws IOException {
        String dataFicheiro = datFileBuilder(taxa_variacao,"crescimentoDePopulacao");
        String scriptCompleto = scriptBuilder(output,"Crescimento",dataFicheiro,nomeGrafico);
        writeToFile(nomeFicheiro,scriptCompleto);
    }

    public void gerarScriptNumeroPorClasse(int output, String nomeFicheiro,String nomeGrafico) throws IOException {
        String dataFicheiro = "";
        for (int i = 0; i <distribuicao_cada_instante.getColumn(0).length(); i++) {
            dataFicheiro += datFileBuilder(distribuicao_cada_instante.getRow(i), "geracao " + (i+1) );
        }
        String scriptCompleto = scriptBuilder(output,"NumPorClasse",dataFicheiro,distribuicao_cada_instante.getColumn(0).length(),nomeGrafico);
        writeToFile(nomeFicheiro,scriptCompleto);

    }
}