import java.io.IOException;
import java.util.*;


public class Main {
    private final static String DIRECTORY = "C:\\Users\\Katerina\\Desktop\\folder";

    public static void main(String[] args) throws IOException {

        Company company = SamplesController.parseJournal(DIRECTORY);

        System.out.println("Введите необходимый месяц латинскими буквами в формате K7,A7,A5: ");
        Scanner scanner = new Scanner(System.in);
        String code = scanner.nextLine();

        SamplesController.printSamples(company, code);

    }
}
