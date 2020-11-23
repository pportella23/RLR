import java.util.zip.CRC32;

public class Pacote {
    private int num;
    private int numPacotes;
    private String nome;
    private String conteudo;
    private int tamPacote;
    private String crc;

    public Pacote(int num, String nome, int tamPacote) {
        this.nome = nome;
        this.num = num;
        this.tamPacote = tamPacote;
    }

    public long geradorCRC(String input) {
        CRC32 crc = new CRC32();
        crc.update(input.getBytes());
        return crc.getValue();
    }

    private String formataCRC(long crc) {
        String converted = Long.toString(crc);
        System.out.println("Before convert: " + crc);
        for (int i = converted.length(); i < 10; i++) {
            converted = "0" + converted;
        }
        System.out.println("After convert : " + converted);
        return converted;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
        this.crc = formataCRC(geradorCRC(this.conteudo));
    }

    public void setCRC(String crc) {
        this.crc = crc;
    }
    public int getNumeroPacotes() {
        return numPacotes;
    }

    public void setNumeroPacotes(int numPacotes) {
        this.numPacotes = numPacotes;
    }

    public void setNumero(int num) {
        this.num = num;
    }

    public int getTamConteudo() {
        return tamPacote - ("000\n000\n"+ nome +"\n0123456789\n").length();
    }

    public String getConteudo() {
        return conteudo;
    }

    public String getCRC() {
        return crc;
    }

    public int getNum() {
        return num;
    }

    private String formataNumero(int unformatted) {
        if (unformatted < 10)
            return "00" + unformatted;
        if (unformatted < 100)
            return "0" + unformatted;
        return unformatted + "";
    }

    public String toString() {
        return formataNumero(num) + "\n" + formataNumero(numPacotes) + "\n" + nome + "\n" + crc + "\n" + conteudo;
    }
}
