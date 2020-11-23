import java.io.*;
import java.util.*;

public class ServerFile {
    private String nome;
    private String conteudo;

    private ArrayList<PacketObject> pacotes;

    public ServerFile() throws Exception {
        pacotes = new ArrayList<PacketObject>();
    }

    public String getNome() {
        return nome;
    }

    private void montarPedacos() {
        conteudo = "";
        for (PacketObject packetObject : pacotes) {
            conteudo = conteudo + packetObject.getConteudo();
        }
    }

    public PacketObject getPacote(byte[] segment) {
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

        PacketObject pacote = new PacketObject(Integer.parseInt(numero), nomePacote, segment.length);
        pacote.setNumeroPacotes(Integer.parseInt(tamanho));
        pacote.setConteudo(conteudoPacote);
        pacote.setCRC(crc);
        nome = nomePacote;

        return pacote;
    }

    private boolean testeCRC(PacketObject pacote) {
        long crcRecebido = Long.parseLong(pacote.getCRC());
        long crcCriado = pacote.geradorCRC(pacote.getConteudo());

        if (crcRecebido == crcCriado)
            return true;

        return false;
    }

    public boolean addPedaco(PacketObject pacote) {
        if (!testeCRC(pacote)) {
            System.out.println("CRC incorrect");
            return false;
        }
        if (pacote.getNum() == pacotes.size()) {
            pacotes.add(pacote);
            System.out.println("");
        } else {
            System.out.println("Segmento already added");
        }

        return true;

    }

    public void salva() throws Exception {
        montarPedacos();
        File arquivo = new File("../out_files/" + nome);
        FileOutputStream fileWritter = new FileOutputStream(arquivo);
        fileWritter.write(conteudo.getBytes());
        fileWritter.close();
    }

    public String getPacotes() {
        String teste = "";
        for (PacketObject pacote : pacotes) {
            teste = teste + pacote.getConteudo();
        }
        return teste;
    }

}
