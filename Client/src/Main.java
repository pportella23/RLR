import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Conexao c = new Conexao(512);
        Scanner keyboard = new Scanner(System.in);
        c.abre();
        System.out.println("Conexao iniciada");
        while(true) {
            System.out.println("Digite 0 para enviar um arquivo");
            if (!keyboard.nextLine().equals("0")) break;


            try {
                System.out.println("Informe o nome do arquivo");
                c.enviaArquivo(keyboard.nextLine());
            }
            catch(Exception e) {
                System.out.println("Arquivo não encontrado");
            }
        }
        keyboard.close();
        c.fecha();
        System.out.println("Conexao finalizada!");
    }
}
