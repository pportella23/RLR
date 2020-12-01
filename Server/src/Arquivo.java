import java.io.*;
import java.util.*;

public class Arquivo {
    private String nome;
    private String conteudo;

    private ArrayList<Pacote> pacotes;

    public Arquivo() throws Exception {
        pacotes = new ArrayList<Pacote>();
    }

    public String getNome() {
        return nome;
    }

    private void montarPedacos() {
        conteudo = "";
        for (Pacote Pacote : pacotes) {
            conteudo = conteudo + Pacote.getConteudo();
        }
    }

    public Pacote getPacote(byte[] segment) {
        String numero = "";
        String tamanho = "";
        String nomePacote = "";
        String crc = "";
        String conteudoPacote = "";

        int i;

        for (i = 0; segment[i] != 10; i++)
            numero = numero + (char) segment[i];

        for (i++; segment[i] != 10; i++)
            tamanho = tamanho + (char) segment[i];

        for (i++; segment[i] != 10; i++)
            nomePacote = nomePacote + (char) segment[i];

        for (i++; segment[i] != 10; i++)
            crc = crc + (char) segment[i];

        for (i++; segment.length > i && segment[i] != 0; i++)
            conteudoPacote = conteudoPacote + (char) segment[i];

        Pacote pacote = new Pacote(Integer.parseInt(numero), nomePacote, segment.length);
        pacote.setNumeroPacotes(Integer.parseInt(tamanho));
        pacote.setConteudo(conteudoPacote);
        pacote.setCRC(crc);
        nome = nomePacote;

        return pacote;
    }

    private boolean testeCRC(Pacote pacote) {
        long crcRecebido = Long.parseLong(pacote.getCRC());
        long crcCriado = pacote.geradorCRC(pacote.getConteudo());

        if (crcRecebido == crcCriado)
            return true;

        return false;
    }

    public boolean addPedaco(Pacote pacote) {
        if (!testeCRC(pacote)) {
            System.out.println("CRC incorreto!");
            return false;
        }
        if (pacote.getNum() == pacotes.size()) {
            pacotes.add(pacote);
            System.out.println("Pacote adicionado");
        } else {
            System.out.println("Este pacote já foi adicionado");
        }

        return true;

    }

    public void salva() throws Exception {
        montarPedacos();
        File arquivo = new File("../arq_saida/" + nome);
        FileOutputStream fileWritter = new FileOutputStream(arquivo);
        fileWritter.write(conteudo.getBytes());
        fileWritter.close();
    }

    public String getPacotes() {
        String teste = "";
        for (Pacote pacote : pacotes) {
            teste = teste + pacote.getConteudo();
        }
        return teste;
    }

}
