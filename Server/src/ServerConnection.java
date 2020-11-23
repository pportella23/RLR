import java.io.*;
import java.net.*;
import java.util.*;

public class ServerConnection {

      private DatagramSocket servidor;
      private InetAddress IP;
      private int tamPacote;

      public ServerConnection(int tamPacote) throws UnknownHostException {
            this.tamPacote = tamPacote;
            IP = InetAddress.getByName("localhost");
      }

      public void abre() throws SocketException, UnknownHostException {
            servidor = new DatagramSocket(1971);
            servidor.setSoTimeout(10000);
      }

      public void fecha() {
            servidor.close();
      }

      public void recebeArquivo() throws Exception {
            ServerFile file = new ServerFile();
            System.out.println("Waiting...");

            int iNeed = 0;
            int received = 0;
            int count = 0;

            while (true) {
                  PacketObject receivedPacket;
                  try {
                        receivedPacket = recebePacote(file);
                        System.out.println("Conexao iniciada");
                  } catch (Exception e) {
                        System.out.println("Cliente nao iniciou a transmissao");
                        return;
                  }


                  System.out.println("\nPacote" + receivedPacket.getNum() + " recebido");
                  if (received == receivedPacket.getNum())
                        count++;
                  else
                        received = receivedPacket.getNum();
                  if (received == iNeed) {

                        file.addPedaco(receivedPacket);
                        iNeed++;
                  }

                  if (count == 3) {
                        iNeed = received;
                        count = 0;
                  }

                  aviso(iNeed);
                  System.out.println("\nPacote " + iNeed + " solicitado");
                  if (iNeed - 1 == receivedPacket.getNumeroPacotes())
                        break;
            }

            System.out.println("Arquivo " + file.getNome() + " recebido");
            file.salva();

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
                  byte[] receiveData = new byte[3];
                  DatagramPacket recebePacote = new DatagramPacket(receiveData, receiveData.length);
                  servidor.receive(recebePacote);
                  String textReceived = new String(recebePacote.getData());
                  Scanner text = new Scanner(textReceived);
                  aviso(Integer.parseInt(text.nextLine()) + 1);
                  text.close();
                  System.out.println("Cliente enviou pacote errado");

                  if (textReceived.equals("end"))
                        break;
            }
      }

      private void aviso(int number) throws IOException {
            byte[] out;
            if (number < 10)
                  out = ("00" + number).getBytes();
            else if (number < 100)
                  out = ("0" + number).getBytes();
            else
                  out = ("" + number).getBytes();

            DatagramPacket sendPacket = new DatagramPacket(out, out.length, IP, 1972);
            servidor.send(sendPacket);
      }

      private PacketObject recebePacote(ServerFile file) throws IOException {
            byte[] receiveData = new byte[tamPacote];
            DatagramPacket recebePacote = new DatagramPacket(receiveData, receiveData.length);
            servidor.receive(recebePacote);

            return file.getPacote(recebePacote.getData());
      }

}
