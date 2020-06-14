import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main
{
    private static final Logger logger = LogManager.getLogger(Main.class);
    private static final Marker INVALID_INSTANCE_MARKER = MarkerManager.getMarker("INVALID_INSTANCE");
    private static final Marker INSTANCE_DUPLICATE_MARKER = MarkerManager.getMarker("INSTANCE_DUPLICATE");
    private static String file = "C:\\Users\\Valet\\OneDrive\\журнал";
    private static Hashtable<String, Sample> sampleHashtable;

    public static void main(String[] args) throws IOException {
        sampleHashtable = new Hashtable<>();

        List<File> allFiles;
        allFiles = Files.walk(Paths.get(file))
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .collect(Collectors.toList());

        allFiles.forEach(file1 -> {
            try {
                Workbook workbook = WorkbookFactory.create(file1);
                for (Sheet sheet : workbook){
                    for (int i = sheet.getFirstRowNum(); i < sheet.getLastRowNum(); i++){
                        Row row = sheet.getRow(i);
                        if (row == null)
                        {
                            continue;
                        }
                        Cell cell = row.getCell(1);
                        if (cell == null || cell.getCellType() != CellType.STRING){
                            continue;
                        }
                        if (cell.getStringCellValue().isEmpty() || cell.getStringCellValue().replaceAll(" ", "").length() < row.getCell(1).toString().length())
                        {
                            continue;
                        }
                        String[] type = file1.getPath().split("\\\\");
                        String instance = refractInstance(cell.getStringCellValue().toUpperCase());
                        Sample sample = new Sample(sheet.getSheetName().trim(), instance.trim(), type[type.length - 1].trim());
                        if (!isRightInstance(sample.getInstance())){
                            logger.debug(INVALID_INSTANCE_MARKER, "Invalid instance: {}", sample.toString());
                            continue;
                        }
                        if (sampleHashtable.containsKey(sample.getInstance())){
                            Sample oldSample = sampleHashtable.get(sample.getInstance());
                            if (oldSample.toString().equals(sample.toString())){
                                continue;
                            }
                            String error = sample.toString() + " - " + sampleHashtable.get(sample.getInstance()).toString();
                            logger.info(INSTANCE_DUPLICATE_MARKER, "Duplicate instance: {}", error);
                            continue;
                        }
                        sampleHashtable.put(sample.getInstance(), sample);

                    }
                }
                workbook.close();
            } catch (IOException e) {
                logger.error(e);
            }
        });

        System.out.println("Введите необходимый месяц латинскими буквами в формате K7,A7,A5: ");
        Scanner scanner = new Scanner(System.in);
        String code = scanner.nextLine();
        printSamples(code);
        //samples.forEach(System.out::println);
    }

    private static boolean isRightInstance(String instance){
        Pattern pattern = Pattern.compile(".+([A-Za-z0-9]{2})$");
        return pattern.matcher(instance).matches();
    }

    private static void printSamples(String code) throws FileNotFoundException {
        StringBuilder builder = new StringBuilder();
        String[] codes = code.split(",");
        ArrayList<Sample> samples = new ArrayList<>();
        TreeSet<String> usedType = new TreeSet<>();
        for (String string : codes) {
            for (String str : sampleHashtable.keySet()){
                if (str.contains(string)){
                    Sample sample = sampleHashtable.get(str);
                    samples.add(sample);
                }
            }
        }
        samples.forEach(sample -> {
            String type = sample.getType();
            if (usedType.add(type)){
                builder.append(type);
                builder.append("\n");
                TreeSet<String> usedArt = new TreeSet<>();
                for (Sample spl : samples) {
                    if (!spl.getType().equals(type)){
                        continue;
                    }

                    if (usedArt.add(spl.getArt())){
                        builder.append("\t");
                        builder.append(spl.getArt());
                        builder.append("\n");
                        for (Sample spl2 : samples){
                            if (spl2.getType().equals(type) && spl2.getArt().equals(spl.getArt())){
                                builder.append("\t\t");
                                builder.append(spl2.getInstance());
                                builder.append("\n");
                            }
                        }
                    }
                }
            }
        });
        PrintWriter writer = new PrintWriter("result/samples.txt");
        writer.write(builder.toString());
        writer.flush();
        writer.close();
    }
    private static String refractInstance(String instance){
        String result = instance.replaceAll("К", "K");
        result = result.replaceAll("Л", "L");
        result = result.replaceAll("А", "A");
        result = result.replaceAll("В", "B");
        result = result.replaceAll("Н", "H");
        result = result.replaceAll("С", "C");
        result = result.replaceAll("Е", "E");
        result = result.replaceAll("Д", "D");
        result = result.replaceAll("Ф", "F");
        return result;
    }
}
