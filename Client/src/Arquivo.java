import java.io.*;
import java.util.*;

public class Arquivo {
    private String nome;
    private String conteudo;
    private ArrayList<Pacote> pacotes;

    public Arquivo(String nome, int tamPacote) throws Exception {
        abre(nome);
        setPacotes(tamPacote);
    }

    private void abre(String nome) throws Exception {
        File arq;
        arq = new File("../in_files/" + nome);
        this.nome = arq.getName();
        InputStream input = null;
        input = new FileInputStream(arq);
        byte[] buffer = new byte[10000];
        input.read(buffer);
        input.close();
        conteudo = new String(formata(buffer));
    }

    private void setPacotes(int tamPacote) {
        pacotes = new ArrayList<Pacote>();
        for (int i = 0; true; i++) {
            Pacote pacote = new Pacote(i, nome, tamPacote);
            String pedaco = getPedaco(i, pacote.getTamConteudo());
            if (pedaco.length() == 0)
                break;
            pacote.setConteudo(pedaco);
            pacotes.add(pacote);
        }
        for (Pacote objetoPacote : pacotes) {
            objetoPacote.setNumeroPacotes(pacotes.size() - 1);
        }
    }

    private String getPedaco(int numPedaco, int tamPedaco) {
        String pedaco = "";
        for (int i = (numPedaco) * tamPedaco; tamPedaco > 0; i++) {
            if (i == conteudo.length())
                break;
            if (i > conteudo.length())
                return "";
            pedaco = pedaco + conteudo.charAt(i);
            tamPedaco--;
        }
        return pedaco;
    }

    public int getTamPacote() {
        return pacotes.size();
    }

    public String getItensPacotes(int i) {
        return pacotes.get(i).toString();
    }

    public String toString() {
        String texto = "";
        for (Pacote objetoPacote : pacotes) {
            texto = texto + objetoPacote.toString() + "\n----------------------\n";
        }
        return texto;
    }

    public byte[] formata(byte[] pacote) {
        String formatado = "";
        for (int i = 0; pacote[i] != 0; i++)
            formatado = formatado + (char) pacote[i];
        return formatado.getBytes();
    }

}
