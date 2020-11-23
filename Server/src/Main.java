import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Conexao s = new Conexao(300);
        Scanner in = new Scanner(System.in);
        s.abre();
        System.out.println("Conexao iniciada");
        while(true) {
            System.out.println("Digite R para receber um arquivo");
            if (!in.nextLine().equalsIgnoreCase("R")) break;
            s.recebeArquivo();
        }
        in.close();
        s.fecha();
        System.out.println("Conexao encerrada");
    }
}