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
      int congestionAvoidance = 7;
      int auxCongestion = 5;
      boolean boolCongestion = false;
      int enviando = 0;
      int enviaQtd = 1;
      while (confirmado < arquivo.getTamPacote()) {

         if (enviaQtd > 0 && enviando < arquivo.getTamPacote()) {
            enviaPacote(arquivo.getItensPacotes(enviando));
            System.out.println("\nPacote " + enviando + " enviado ->");
            enviando++;
         }

         if (enviaQtd == 2 && enviando < arquivo.getTamPacote()) {
            enviaPacote(arquivo.getItensPacotes(enviando));
            System.out.println("\nPacote " + enviando + " enviado ->");
            enviando++;
         }

         if (recebePacote(confirmado) == 1) {
            System.out.println("\nPacote " + confirmado + " confirmado ✓");
            confirmado++;
            
            // Slow Start
            if (confirmado < 4 && !boolCongestion) {
               enviaQtd = 2;
            }

            // Congestion Avoidance
            else {
               
               boolCongestion = true;

               if (confirmado == 4){   
                  System.out.println("\nCongestion Avoidance iniciado.");
               }
               
               // Chegou no último nodo da geração e cria dois filhos
               if (confirmado == congestionAvoidance){
                  enviaQtd = 2;
                  congestionAvoidance += auxCongestion;
                  auxCongestion++;
               }

               // Está nos primeiros nodos da geração
               else {
                  enviaQtd = 1;
               }
               
            }
            
         } else {

            // Time out
            if (recebePacote(confirmado) == 2) {
               enviando = confirmado;
               enviaQtd = 1;   
            }
            
            // 3 ACKs seguidos
            else if (recebePacote(confirmado) == 3) {
               enviando = confirmado;
               enviaQtd = 1;
            }
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

   private int recebePacote(int confirmado) throws IOException {
      byte[] dadosRecebidos = new byte[3];
      DatagramPacket pacoteRecebido = new DatagramPacket(dadosRecebidos, dadosRecebidos.length);

      for (int tentativas = 0; tentativas < 3; tentativas++) {
         try {
            cliente.receive(pacoteRecebido);
            int numeroRecebido = Integer.parseInt(new String(pacoteRecebido.getData()));

            if (numeroRecebido - 1 == confirmado)
               return 1;
         } catch (SocketTimeoutException e) {
            // Volta pro mesmo pacote
            System.out.println("Limite de tempo, procurando por " + confirmado + " novamente");
            return 2;
         }
      }
      // O tamanho do congestion avoidance cai pela metade
      System.out.println("Engano, procurando por " + confirmado + " novamente");
      return 3;
   }

   private void enviaPacote(String fileText) throws IOException {
      byte[] saida = fileText.getBytes();
      DatagramPacket enviaPacote = new DatagramPacket(saida, saida.length, IP, 1971);
      cliente.send(enviaPacote);
   }
}
