import java.io.*;
import java.net.*;
import java.util.*;

public class Conexao {

      private DatagramSocket servidor;
      private InetAddress IP;

      // Tamanho dos pacotes enviados
      private int tamPacote = 300;

      public Conexao() throws UnknownHostException {
            IP = InetAddress.getByName("localhost");
      }

      public void abre() throws SocketException, UnknownHostException {
            servidor = new DatagramSocket(5554);
            servidor.setSoTimeout(10000);
      }

      public void fecha() {
            servidor.close();
      }

      public void recebeArquivo() throws Exception {
            Arquivo arq = new Arquivo();
            System.out.println("Aguardando...");

            int aux = 0; // iNeed
            int recebido = 0;
            int cont = 0;

            while (true) {
                  Pacote pacoteRecebido;
                  try {
                        pacoteRecebido = recebePacote(arq);
                        System.out.println("Conexao iniciada");
                  } catch (Exception e) {
                        System.out.println("Cliente nao iniciou a transmissao");
                        return;
                  }

                  System.out.println("\nPacote" + pacoteRecebido.getNum() + " recebido ✓");
                  //System.out.println("\nPacote" + pacoteRecebido.getNum() + " tamanho: " + pacoteRecebido.getTamPacote());

                  if (recebido == pacoteRecebido.getNum())
                        cont++;
                  else
                        recebido = pacoteRecebido.getNum();
                  if (recebido == aux) {
                        arq.addPedaco(pacoteRecebido);
                        aux++;
                  }

                  if (cont == 3) {
                        aux = recebido;
                        cont = 0;
                  }

                  aviso(aux);
                  System.out.println("\nPacote " + aux + " solicitado");
                  if (aux - 1 == pacoteRecebido.getNumeroPacotes())
                        break;
            }

            System.out.println("Arquivo " + arq.getNome() + " recebido ✓");
            arq.salva();

            System.out.println("Esperando pelo cliente ");
            try {
                  recebeFimTransmissao();
                  System.out.println("Transmissao acabou ");
            } catch (Exception e) {
                  System.out.println("Sem resposta do cliente!");
            }

      }

      private void recebeFimTransmissao() throws IOException {
            while (true) {
                  byte[] dadosRecebidos = new byte[3];
                  DatagramPacket recebePacote = new DatagramPacket(dadosRecebidos, dadosRecebidos.length);
                  servidor.receive(recebePacote);
                  String textoRecebido = new String(recebePacote.getData());
                  Scanner texto = new Scanner(textoRecebido);
                  aviso(Integer.parseInt(texto.nextLine()) + 1);
                  texto.close();
                  System.out.println("Cliente enviou pacote errado");

                  if (textoRecebido.equals("end"))
                        break;
            }
      }

      private void aviso(int number) throws IOException {
            byte[] saida;
            if (number < 10)
                  saida = ("00" + number).getBytes();
            else if (number < 100)
                  saida = ("0" + number).getBytes();
            else
                  saida = ("" + number).getBytes();

            DatagramPacket enviarPacote = new DatagramPacket(saida, saida.length, IP, 5555);
            servidor.send(enviarPacote);
      }

      private Pacote recebePacote(Arquivo arq) throws IOException {
            byte[] dadosRecebidos = new byte[tamPacote];
            DatagramPacket recebePacote = new DatagramPacket(dadosRecebidos, dadosRecebidos.length);
            servidor.receive(recebePacote);

            return arq.getPacote(recebePacote.getData());
      }

}
