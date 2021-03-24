import org.la4j.Matrix;
import org.la4j.*;
import org.la4j.decomposition.EigenDecompositor;
import org.la4j.matrix.dense.Basic2DMatrix;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;


public class Main {

    static boolean haInteracao = false;

    public static void main(String[] args) throws IOException, InterruptedException {

        MainTest tests = new MainTest();
        tests.writeToFile();
        String fileName = null;
        String outPutFileName = null;

        int formatoGNUPLOT = 0;
        int nr_geracoes = 0;
        boolean calcularValorEVetor = false;
        boolean calcularDimensaoDaPopulacao = false;
        boolean calcularVariacao = false;
        boolean fileExists = false;
        if (args.length > 2) {
            fileName = args[args.length - 2];
            outPutFileName = args[args.length - 1];
            fileExists = true;
        }

        for (int i = 0; i < args.length; i++) {

            switch (args[i]) {
                case "-t":
                    nr_geracoes = Integer.parseInt(args[i + 1]);
                    break;
                case "-g":
                    switch (args[i + 1].trim()) {
                        case "1":
                            formatoGNUPLOT = 1;
                            break;
                        case "2":
                            formatoGNUPLOT = 2;
                            break;
                        case "3":
                            formatoGNUPLOT = 3;
                            break;
                        default:
                            formatoGNUPLOT = 0;
                    }
                    break;
                case "-e":
                    calcularValorEVetor = true;
                    break;
                case "-v":
                    calcularDimensaoDaPopulacao = true;
                    break;
                case "-r":
                    calcularVariacao = true;
                    break;
                case "-n":
                    fileName = args[i + 1].trim();
                    haInteracao = true;
                    fileExists = true;
                    break;
            }
        }
        program(fileName, outPutFileName, nr_geracoes, formatoGNUPLOT, calcularValorEVetor, calcularDimensaoDaPopulacao, calcularVariacao, fileExists);
    }

    public static void program(String fileName, String outPutFileName, int nr_geracoes, int formatoGNUPLOT, boolean calcularValorEVetor, boolean calcularDimensaoDaPopulacao, boolean calcularVariacao, boolean fileExist) throws IOException, InterruptedException {
        double[] P_0 = null;
        double[] taxa_sobrevivencia = null;
        double[] taxa_fecundidade = null;
        Matrix matriz_leslie = null;
        Matrix initial_distribution_population = null;
        Matrix matriz_normalizada = null;
        double[] dimensao_populacao = null;
        Matrix distribuicao_cada_instante = null;
        double[] taxa_variacao = null;
        int n_idades = 0;


        if (haInteracao) {
            modoInterativo(fileExist, matriz_leslie, P_0, taxa_sobrevivencia, taxa_fecundidade, n_idades, initial_distribution_population, taxa_variacao, matriz_normalizada, distribuicao_cada_instante, dimensao_populacao, fileName);

        } else {
            modoNaoInterativo(calcularValorEVetor, calcularDimensaoDaPopulacao, formatoGNUPLOT, calcularVariacao, P_0, taxa_sobrevivencia, taxa_fecundidade, n_idades, nr_geracoes, fileName, outPutFileName);
        }
    }

    private static void modoInterativo(boolean fileExist, Matrix matriz_leslie, double[] P_0, double[] taxa_sobrevivencia, double[] taxa_fecundidade, int n_idades, Matrix initial_distribution_population, double[] taxa_variacao, Matrix matriz_normalizada, Matrix distribuicao_cada_instante, double[] dimensao_populacao, String filename) throws IOException, InterruptedException {
        menu2();

        Scanner sc = new Scanner(System.in);
        String instrucao = sc.next();
        while (instrucao != null) {

            Scanner fileReader;
            switch (instrucao) {
                case "0":
                    System.out.println("========|A aplicação fechou|=======");
                    System.exit(1);
                    break;
                case "1":
                    if (fileExist) {

                        fileReader = new Scanner(new File(filename));
                        for (int i = 0; i < 3; i++) {

                            String[] line = fileReader.nextLine().trim().split(",");
                            switch (line[0].charAt(0)) {
                                case 'f':
                                    taxa_fecundidade = formarVetor(line);
                                    break;
                                case 'x':
                                    P_0 = formarVetor(line);
                                    n_idades = P_0.length;
                                    break;
                                case 's':
                                    taxa_sobrevivencia = formarVetor(line);
                                    break;
                            }
                        }
                        if (taxa_sobrevivencia != null && taxa_fecundidade != null) {
                            matriz_leslie = formarMatrizLeslie(taxa_sobrevivencia, taxa_fecundidade);
                            System.out.println(matriz_leslie);
                        } else {
                            System.out.println("===|WARNING: NÃO FOI POSSÍVEL CARREGAR AS INFORMAÇÕES DO FICHEIRO|===");
                        }

                    } else {
                        System.out.println("Introduza o numero de diferentes idades");
                        n_idades = sc.nextInt();
                        System.out.println("Introduza a distribuição inicial da população");
                        P_0 = new double[n_idades];

                        for (int i = 0; i < n_idades; i++) {
                            System.out.println("Introduza um valor");
                            P_0[i] = sc.nextDouble();

                        }

                        System.out.println("Introduza a taxa de fecundidade");
                        taxa_fecundidade = new double[n_idades];
                        for (int i = 0; i < n_idades; i++) {
                            System.out.println("Introduza um valor");
                            taxa_fecundidade[i] = sc.nextDouble();
                        }

                        System.out.println("Introduza a taxa de sobrevivencia");
                        taxa_sobrevivencia = new double[n_idades - 1];
                        for (int i = 0; i < n_idades - 1; i++) {
                            System.out.println("Introduza um valor");
                            taxa_sobrevivencia[i] = sc.nextDouble();
                        }

                    }
                    break;
                case "2":
                    if (taxa_fecundidade != null && taxa_sobrevivencia != null) {
                        System.out.println("|Distribuição Inicial da População P0 = " + Arrays.toString(P_0));
                        System.out.println("|Vetor Taxa de Fecundidade = " + Arrays.toString(taxa_fecundidade));
                        System.out.println("|Vetor Taxa de Sobrevivência = " + Arrays.toString(taxa_sobrevivencia) + "\n\n");
                        System.out.println("|Introduza o número de gerações a estimar|");

                        int n_geracao = sc.nextInt();
                        matriz_leslie = formarMatrizLeslie(taxa_sobrevivencia, taxa_fecundidade);
                        System.out.println("|Matriz de Leslie:|");
                        System.out.println(matriz_leslie);

                        initial_distribution_population = Matrix.from1DArray(n_idades, 1, P_0);
                        System.out.println("|Matriz de distribuição inicial da população|");
                        System.out.println(initial_distribution_population);

                        Matrix P_N = distribuicaoTotalDaPopulacaoNormalizada(matriz_leslie, n_geracao, initial_distribution_population);
                        System.out.printf("|Matriz de Distribuição Total Normalizada com %d gerações|\n", n_geracao);
                        System.out.println(P_N);
                        System.out.printf("|Matriz de Distribuicao A cada instante de %d gerações|\n", n_geracao);

                        distribuicao_cada_instante = distribuicaoDaPopulacaoCadaInstante(matriz_leslie, n_geracao, initial_distribution_population);
                        matriz_normalizada = normalize(distribuicao_cada_instante);

                        System.out.println(distribuicao_cada_instante);
                    } else {
                        System.out.println("===|WARNING: CARREGUE AS INFORMAÇÕES ANTES DE UTILIZAR ESTA FUNCIONALIDADE|===");
                    }
                    break;

                case "3":
                    if (matriz_leslie != null && initial_distribution_population != null) {
                        System.out.println("|Insira a geração em que quer determinar a dimensão da população|");

                        int n_geracao_2 = sc.nextInt();
                        System.out.println("|O total da população dessa geração é:| ");

                        dimensao_populacao = getDimensaoPopulacaoTotal(matriz_leslie, n_geracao_2, initial_distribution_population);
                        System.out.println(dimensao_populacao[n_geracao_2 - 1] + "\n");

                        taxa_variacao = getTaxaVariacao(matriz_leslie, n_geracao_2, initial_distribution_population);
                        System.out.println("|Taxa de Variação|");
                        System.out.printf("%.4f\n", taxa_variacao[n_geracao_2 - 1]);
                    } else {
                        System.out.println("===|WARNING: CARREGUE AS INFORMAÇÕES ANTES DE UTILIZAR ESTA FUNCIONALIDADE|===");
                    }
                    break;

                case "4":
                    if (matriz_leslie != null) {
                        Matrix[] valoresProprios = valoresProprios(matriz_leslie);
                        System.out.println("========|Vetores Próprios da matriz|==========");
                        System.out.println(valoresProprios[0]);
                        System.out.println("========|Valores Próprios da matriz|==========");
                        System.out.println(valoresProprios[1]);
                        System.out.println("=======|Maior valor próprio da Matriz|========");
                        double lambda = lambda(valoresProprios[1]);
                        System.out.printf("%.4f \n\n", lambda);
                        System.out.println("=====|Previsão de crescimento da população|===");
                        if (lambda < 1) {
                            System.out.println("A população irá decrescer" + "\n");
                        } else {
                            if (lambda == 1) {
                                System.out.println("A população irá manter" + "\n");
                            } else {
                                System.out.println("A população irá crescer" + "\n");
                            }
                        }
                    } else {
                        System.out.println("===|WARNING| MATRIZ DE LESLIE NULA|===");
                    }

                    break;
                case "5":
                    if (matriz_leslie != null) {
                        if (matriz_normalizada != null && distribuicao_cada_instante != null && taxa_variacao != null && dimensao_populacao != null) {
                            GNUPlot gnu = new GNUPlot(matriz_normalizada, distribuicao_cada_instante, taxa_variacao, dimensao_populacao);
                            System.out.println("====|Introduza o tipo de gráfico para gerar|====\n |1|-Crescimento\n |2|-NumPopulação\n |3|-NumPorClasse\n |4|-NumPorClasseNormalizada\n");
                            String grafico = sc.next();
                            System.out.println("Com que nome pretende gerar o gráfico?");
                            String nomeGrafico = sc.next();
                            System.out.println("Pretende guardar em que formato? \n |1|-PNG\n |2|-TXT\n |3|-EPS\n |0|-Não pretendo guardar");
                            int guardar = sc.nextInt();
                            switch (grafico) {
                                case "1":
                                    gnu.gerarScriptCrescimentoPopulacao(0, "crescimento_" + java.time.LocalDate.now()); //output 0 é abrir o grafico
                                    gnu.plot("crescimento_" + java.time.LocalDate.now());
                                    if (guardar != 0) {
                                        gnu.gerarScriptCrescimentoPopulacao(guardar, nomeGrafico);
                                        gnu.plot(nomeGrafico);
                                    }
                                    break;
                                case "2":
                                    gnu.gerarScriptNumTotalIndividuos(0, "numPopulacao_" + java.time.LocalDate.now());
                                    gnu.plot("numPopulacao_" + java.time.LocalDate.now());
                                    if (guardar != 0) {
                                        gnu.gerarScriptNumTotalIndividuos(guardar, nomeGrafico);
                                        gnu.plot(nomeGrafico);
                                    }

                                    break;
                                case "3":
                                    gnu.gerarScriptNumeroPorClasse(0, "numPorClasse_" + java.time.LocalDate.now());
                                    gnu.plot("numPorClasse_" + java.time.LocalDate.now());
                                    if (guardar != 0) {
                                        gnu.gerarScriptNumeroPorClasse(guardar, nomeGrafico);
                                        gnu.plot(nomeGrafico);
                                    }

                                    break;
                                case "4":
                                    gnu.gerarScriptNumeroPorClasseNormalizado(0, "numPorClasseNormalizada_" + java.time.LocalDate.now());
                                    gnu.plot("numPorClasseNormalizada_" + java.time.LocalDate.now());
                                    if (guardar != 0) {
                                        gnu.gerarScriptNumeroPorClasseNormalizado(guardar, nomeGrafico);
                                        gnu.plot(nomeGrafico);
                                    }
                                    break;
                                default:
                                    System.out.println("===|WARNING| OPÇÃO INVÁLIDA|===");
                                    break;
                            }
                        } else {
                            System.out.println("===|WARNING: CARREGUE AS INFORMAÇÕES ANTES DE UTILIZAR ESTA FUNCIONALIDADE|===");
                        }
                    } else {
                        System.out.println("===|WARNING| MATRIZ DE LESLIE NULA|===");
                    }
                    break;
                case "6":
                    System.out.println("========|INFORMAÇÕES APAGADAS|=======");
                    boolean flag=true;
                    while(flag){
                        System.out.println("Pretende carregar informações de outro ficheiro?");
                        System.out.println("1-Sim");
                        System.out.println("2-Não");
                        String dec = sc.next();
                        switch (dec) {
                            case "1":
                                fileExist = true;
                                System.out.println("Introduza o nome do novo ficheiro");
                                filename = sc.next();
                                flag=false;
                                break;
                            case "2":
                                fileExist = false;
                                flag=false;
                                break;
                            default:
                                System.out.println("===|WARNING| OPÇÃO INVÁLIDA|===");
                                break;
                        }
                    }
                    if (fileExist) {


                        fileReader = new Scanner(new File(filename));
                        for (int i = 0; i < 3; i++) {

                            String[] line = fileReader.nextLine().trim().split(",");
                            switch (line[0].charAt(0)) {
                                case 'f':
                                    taxa_fecundidade = formarVetor(line);
                                    break;
                                case 'x':
                                    P_0 = formarVetor(line);
                                    n_idades = P_0.length;
                                    break;
                                case 's':
                                    taxa_sobrevivencia = formarVetor(line);
                                    break;
                            }
                        }

                        if (taxa_sobrevivencia != null && taxa_fecundidade != null) {
                            matriz_leslie = formarMatrizLeslie(taxa_sobrevivencia, taxa_fecundidade);
                            System.out.println(matriz_leslie);
                        } else {
                            System.out.println("===|WARNING: NÃO FOI POSSÍVEL CARREGAR AS INFORMAÇÕES DO FICHEIRO|===");
                        }

                    } else {
                        System.out.println("Introduza o numero de diferentes idades");
                        n_idades = sc.nextInt();
                        System.out.println("Introduza a distribuição inicial da população");
                        P_0 = new double[n_idades];
                        for (int i = 0; i < n_idades; i++) {
                            System.out.println("Introduza um valor");
                            P_0[i] = sc.nextDouble();

                        }
                        System.out.println("Introduza a taxa de fecundidade");
                        taxa_fecundidade = new double[n_idades];
                        for (int i = 0; i < n_idades; i++) {
                            System.out.println("Introduza um valor");
                            taxa_fecundidade[i] = sc.nextDouble();
                        }
                        System.out.println("Introduza a taxa de sobrevivencia");
                        taxa_sobrevivencia = new double[n_idades - 1];
                        for (int i = 0; i < n_idades - 1; i++) {
                            System.out.println("Introduza um valor");
                            taxa_sobrevivencia[i] = sc.nextDouble();
                        }

                    }
                    break;

                default:
                    System.out.println("======|A APLICAÇÃO REINICIOU|======");
                    break;

            }

            menu2();
            instrucao = sc.next();

        }
    }

    private static void modoNaoInterativo(boolean calcularValorEVetor, boolean calcularDimensaoDaPopulacao, int formatoGNUPLOT, boolean calcularVariacao, double[] P_0, double[] taxa_sobrevivencia, double[] taxa_fecundidade, int n_idades, int nr_geracoes, String filename, String outPutFilename) throws IOException, InterruptedException {

        FileWriter myWriter = new FileWriter(outPutFilename);
        Scanner fileReader = new Scanner(new File(filename));
        for (int i = 0; i < 3; i++) {

            String[] line = fileReader.nextLine().trim().split(",");
            switch (line[0].charAt(0)) {
                case 'f':
                    taxa_fecundidade = formarVetor(line);
                    break;
                case 'x':
                    P_0 = formarVetor(line);
                    n_idades = P_0.length;
                    break;
                case 's':
                    taxa_sobrevivencia = formarVetor(line);
                    break;
            }
        }
        GNUPlot gnu = new GNUPlot();

        Matrix distribuicao_cada_instante;
        Matrix normalizada;
        Matrix matriz_leslie = formarMatrizLeslie(taxa_sobrevivencia, taxa_fecundidade);
        myWriter.write("K=" + nr_geracoes);
        myWriter.write("\nMatriz de Leslie\n");
        myWriter.write(matriz_leslie.toString());


        Matrix initial_distribution_population = Matrix.from1DArray(n_idades, 1, P_0);

        myWriter.write("\nNumero total de Individuos\n");
        myWriter.write("(t, Nt)\n");
        double[] dimensaoPopTotalGeracoes = getDimensaoPopulacaoTotal(matriz_leslie, nr_geracoes, initial_distribution_population);
        for (int i = 0; i < dimensaoPopTotalGeracoes.length; i++) {
            myWriter.write("(" + i + ", " + dimensaoPopTotalGeracoes[i] + ")\n");
        }

        myWriter.write("\n");
        gnu.setDimensaoPopTotalGeracoes(dimensaoPopTotalGeracoes);
        gnu.gerarScriptNumTotalIndividuos(formatoGNUPLOT, "Tot");
        gnu.plot("Tot");

        if (calcularVariacao) {

            myWriter.write("Crescimento da População\n");
            myWriter.write("(t, delta_t)\n");
            double[] taxa_variacao = getTaxaVariacao(matriz_leslie, nr_geracoes, initial_distribution_population);

            for (int i = 0; i < taxa_variacao.length; i++) {
                myWriter.write("(" + i + ", " + taxa_variacao[i] + ")\n");
            }

            gnu.setTaxa_variacao(taxa_variacao);
            gnu.gerarScriptCrescimentoPopulacao(formatoGNUPLOT, "taxaVar");
            gnu.plot("taxaVar");
        }

        if (calcularDimensaoDaPopulacao) {
            myWriter.write("\nNumero Por Classe (nao normalizado):\n");
            distribuicao_cada_instante = distribuicaoDaPopulacaoCadaInstante(matriz_leslie, nr_geracoes, initial_distribution_population);
            myWriter.write(distribuicao_cada_instante.toString());
            myWriter.write("\n");
            myWriter.write("Numero Por Classe (normalizado):\n");
            normalizada = normalize(distribuicao_cada_instante);
            myWriter.write(normalizada.toString());
            myWriter.write("\n");

            gnu.setDistribuicao_cada_instante(distribuicao_cada_instante);
            gnu.gerarScriptNumeroPorClasse(formatoGNUPLOT, "dist");
            gnu.plot("dist");


            gnu.setNormalizada(normalizada);
            gnu.gerarScriptNumeroPorClasseNormalizado(formatoGNUPLOT, "distN");
            gnu.plot("distN");

        }

        if (calcularValorEVetor) {
            Matrix[] valoresProprios = valoresProprios(matriz_leslie);
            myWriter.write("Vetores Próprios da matriz\n");
            myWriter.write(String.valueOf(valoresProprios[0]));
            myWriter.write("Valores Próprios da matriz\n");
            myWriter.write(String.valueOf(valoresProprios[1]));
            myWriter.write("\nMaior valor próprio da Matriz:\n");
            double lambda = lambda(valoresProprios[1]);
            myWriter.write("lambda: " + lambda);
        }
        myWriter.close();

    }

    public static Matrix normalize(Matrix toBeNormalized) {
        Matrix normalized = new Basic2DMatrix(toBeNormalized.rows(), toBeNormalized.columns());
        for (int i = 0; i < toBeNormalized.rows(); i++) {
            double sumRow = toBeNormalized.getRow(i).sum();
            for (int j = 0; j < toBeNormalized.columns(); j++) {
                double value = (toBeNormalized.get(i, j) * 100) / sumRow;
                normalized.set(i, j, value);
            }
        }
        return normalized;
    }

    private static void menu2() {
        System.out.println("|==============================|MODO INTERATIVO|===================================|");
        System.out.println("|==| |1|-Carregar Informações acerca de uma espécie                             |==|");
        System.out.println("|==| |2|-Gerar distribuição normal e normalizada da população para cada instante|==|");
        System.out.println("|==| |3|-Calcular dimensão da população                                         |==|");
        System.out.println("|==| |4|-Comportamento Asimptotico                                              |==|");
        System.out.println("|==| |5|-Gerar representação gráfica                                            |==|");
        System.out.println("|==| |6|-Apagar informações e carregar novas                                    |==|");
        System.out.println("|==| |0|-SAIR                                                                   |==|");
        System.out.println("|==============================|MODO INTERATIVO|===================================|");
    }

    public static double[] getTaxaVariacao(Matrix L, int n_geracao, Matrix P_0) {
        double[] taxaVariacao = new double[n_geracao];

        for (int i = 0; i < n_geracao; i++) {
            double primeira_taxa = getDimensaoPopulacaoPorGeracao(L, i, P_0);
            double segunda_taxa = getDimensaoPopulacaoPorGeracao(L, i + 1, P_0);
            taxaVariacao[i] = segunda_taxa / primeira_taxa;
        }

        return taxaVariacao;
    }

    public static double getDimensaoPopulacaoPorGeracao(Matrix L, int n_geracao, Matrix P_0) {
        Matrix P_N = distribuicaoTotalDaPopulacaoNormalizada(L, n_geracao, P_0);
        return (P_N.sum());
    }

    public static double[] getDimensaoPopulacaoTotal(Matrix L, int n_geracao, Matrix P_0) {
        double[] dimensaoTotal = new double[n_geracao];
        for (int i = 0; i < n_geracao; i++) {
            dimensaoTotal[i] = getDimensaoPopulacaoPorGeracao(L, i, P_0);

        }
        return dimensaoTotal;
    }

    public static Matrix distribuicaoDaPopulacaoCadaInstante(Matrix L, int n_geracao, Matrix P_0) {
        double[][] matrix = new double[P_0.rows()][n_geracao + 1];
        for (int j = 0; j < P_0.rows(); j++) {
            matrix[j][0] = P_0.get(j, 0);
        }
        Matrix distribuicao_cada_instante = Matrix.from2DArray(matrix);
        for (int i = 1; i <= n_geracao; i++) {
            distribuicao_cada_instante.setColumn(i, convertToVector(distribuicaoTotalDaPopulacaoNormalizada(L, i, P_0)));
        }
        return distribuicao_cada_instante;
    }

    public static Vector convertToVector(Matrix m) {
        double[] vect_aux = new double[m.rows()];
        for (int i = 0; i < m.rows(); i++) {
            vect_aux[i] = m.get(i, 0);
        }
        return Vector.fromArray(vect_aux);
    }

    public static Matrix distribuicaoTotalDaPopulacaoNormalizada(Matrix L, int n_geracao, Matrix P_0) {
        Matrix L_n = L.power(n_geracao);
        return L_n.multiply(P_0);
    }

    public static double[] formarVetor(String[] line) {
        double[] vetor = new double[line.length];

        for (int i = 0; i < line.length; i++) {
            String[] element = line[i].trim().split("=");
            vetor[i] = Double.parseDouble(element[1]);
        }
        return vetor;
    }

    public static Matrix formarMatrizLeslie(double[] taxaSobrevivencia, double[] taxaFecundidade) {
        double[][] matrix = new double[taxaFecundidade.length][taxaFecundidade.length];
        for (int i = 0; i < taxaFecundidade.length; i++) {
            matrix[0][i] = taxaFecundidade[i];
        }
        int counter = 0;
        for (int i = 1; i < taxaFecundidade.length; i++) {
            for (int j = 0; j < taxaFecundidade.length; j++) {
                if (i == j + 1) {
                    matrix[i][j] = taxaSobrevivencia[counter];
                    counter++;
                } else {
                    matrix[i][j] = 0;
                }
            }
        }
        return Matrix.from2DArray(matrix);
    }

    public static Matrix[] valoresProprios(Matrix matriz_leslie) {
        EigenDecompositor e = new EigenDecompositor(matriz_leslie);
        return e.decompose();
    }

    public static double lambda(Matrix m) {
        double maior = 0;
        for (int i = 0; i < m.rows(); i++) {
            for (int j = 0; j < m.columns(); j++) {
                if (Math.abs(m.get(i, j)) > maior) {
                    maior = Math.abs(m.get(i, j));
                }
            }
        }

        return maior;
    }

}
