import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Conexao c = new Conexao(300);
        Scanner in = new Scanner(System.in);
        c.abre();
        System.out.println("Conexao iniciada");
        while(true) {
            System.out.println("Digite E para enviar um arquivo");
            if (!in.nextLine().equalsIgnoreCase("E")) break;

            try {
                System.out.println("Informe o nome do arquivo");
                c.enviaArquivo(in.nextLine());
            }
            catch(Exception e) {
                System.out.println("Arquivo não encontrado");
            }
        }
        in.close();
        c.fecha();
        System.out.println("Conexao finalizada!");
    }
}