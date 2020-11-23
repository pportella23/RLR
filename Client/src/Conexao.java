import java.io.*;
import java.net.*;

public class Conexao {

   private DatagramSocket cliente;
   private InetAddress IP;
   private int tamPacote;

   public Conexao(int tamPacote) throws UnknownHostException {
      this.tamPacote = tamPacote;
      IP = InetAddress.getByName("localhost");
   }

   public void abre() throws SocketException {
      cliente = new DatagramSocket(1972);
      cliente.setSoTimeout(1000);
   }

   public void fecha() {
      cliente.close();
   }

   public void enviaArquivo(String fileName) throws Exception {
      Arquivo arquivo = new Arquivo(fileName, tamPacote);

      int confirmado = 0;
      int enviando = 0;
      int enviaQtd = 1;
      while (confirmado < arquivo.getTamPacote()) {

         if (enviaQtd > 0 && enviando < arquivo.getTamPacote()) {
            enviaPacote(arquivo.getItensPacotes(enviando));
            System.out.println("Pacote " + enviando + " enviado ->");
            enviando++;
         }

         if (enviaQtd == 2 && enviando < arquivo.getTamPacote()) {
            enviaPacote(arquivo.getItensPacotes(enviando));
            System.out.println("Pacote " + enviando + " enviado ->");
            enviando++;
         }

         if (recebePacote(confirmado)) {
            System.out.println("Pacote " + confirmado + " confirmado");
            confirmado++;
            enviaQtd = 2;

         } else {
            enviando = confirmado;
            enviaQtd = 1;
         }
      }


      System.out.println("Arquivo " + fileName + " enviado ->");

      terminaTransmissao();
      System.out.println("Transmissao finalizada");
   }

   private void terminaTransmissao() throws IOException {
      byte[] saida = "end".getBytes();
      DatagramPacket enviaPacote = new DatagramPacket(saida, saida.length, IP, 1971);
      cliente.send(enviaPacote);
   }

   private boolean recebePacote(int confirmado) throws IOException {
      byte[] dadosRecebidos = new byte[3];
      DatagramPacket pacoteRecebido = new DatagramPacket(dadosRecebidos, dadosRecebidos.length);

      for (int tentativas = 0; tentativas < 3; tentativas++) {
         try {
            cliente.receive(pacoteRecebido);
            int numeroRecebido = Integer.parseInt(new String(pacoteRecebido.getData()));

            if (numeroRecebido - 1 == confirmado)
               return true;
         } catch (SocketTimeoutException e) {
            System.out.println("Limite de tempo, procurando por " + confirmado + " novamente");
            return false;
         }
      }
      System.out.println("Engano, procurando por " + confirmado + " novamente");
      return false;

   }

   private void enviaPacote(String fileText) throws IOException {
      byte[] saida = fileText.getBytes();
      DatagramPacket enviaPacote = new DatagramPacket(saida, saida.length, IP, 1971);
      cliente.send(enviaPacote);
   }

}
