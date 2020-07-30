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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SamplesController {
    private static final Logger logger = LogManager.getLogger(SamplesController.class);
    private static final Marker INVALID_INSTANCE_MARKER = MarkerManager.getMarker("INVALID_INSTANCE");
    private static final Marker INSTANCE_DUPLICATE_MARKER = MarkerManager.getMarker("INSTANCE_DUPLICATE");
    private static Hashtable<String, Sample> sampleHashtable;

    public static Company parseJournal(String directory) throws IOException {
        Company company = new Company();
        sampleHashtable = new Hashtable<>();
        List<File> allFiles;
        allFiles = Files.walk(Paths.get(directory))
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .collect(Collectors.toList());

        List<Type> types = new ArrayList<>();

        allFiles.forEach(file1 -> {
            try {
                Workbook workbook = WorkbookFactory.create(file1);
                String[] type = file1.getPath().split("\\\\");
                String typeName = type[type.length - 1].trim();

                List<Art> arts = new ArrayList<>();

                for (Sheet sheet : workbook) {
                    List<Sample> samples = new ArrayList<>();
                    for (int i = sheet.getFirstRowNum(); i < sheet.getLastRowNum(); i++) {
                        if (sheet.getSheetName().contains("орг")){
                            continue;
                        }
                        Row row = sheet.getRow(i);
                        if (row == null) {
                            continue;
                        }
                        Cell cell = row.getCell(1);
                        if (cell == null || cell.getCellType() != CellType.STRING) {
                            continue;
                        }
                        if (cell.getStringCellValue().isEmpty() || cell.getStringCellValue().replaceAll(" ", "").length() < row.getCell(1).toString().length()) {
                            continue;
                        }

                        String instance = refractInstance(cell.getStringCellValue().toUpperCase());
                        Sample sample = new Sample(sheet.getSheetName().trim(), instance.trim(), typeName);
                        if (!isRightInstance(sample.getInstance())) {
                            logger.debug(INVALID_INSTANCE_MARKER, "Invalid instance: {}", sample.toString());
                            continue;
                        }
                        if (sampleHashtable.containsKey(sample.getInstance())) {
                            Sample oldSample = sampleHashtable.get(sample.getInstance());
                            if (oldSample.toString().equals(sample.toString())) {
                                continue;
                            }
                            String error = sample.toString() + " - " + sampleHashtable.get(sample.getInstance()).toString();
                            logger.info(INSTANCE_DUPLICATE_MARKER, "Duplicate instance: {}", error);
                            continue;
                        }
                        sampleHashtable.put(sample.getInstance(), sample);

                        samples.add(sample);
                    }
                    arts.add(new Art(sheet.getSheetName().trim(), samples));
                }

                types.add(new Type(typeName, arts));

                workbook.close();
            } catch (IOException e) {
                logger.error(e);
            }
        });
        company.setTypes(types);
        return company;
    }

    private static String refractInstance(String instance) {
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

    private static boolean isRightInstance(String instance) {
        Pattern pattern = Pattern.compile(".+([A-Za-z0-9]{2})$");
        return pattern.matcher(instance).matches();
    }

    public static void printSamples(Company company, String code) throws FileNotFoundException {
        StringBuilder builder = new StringBuilder();
        String[] codes = code.split(",");
        List<Type> types = findSamples(company, codes).getTypes();
        for (Type type : types) {
            builder.append(type.getName());
            builder.append("\n");
            List<Art> arts = type.getArts();
            for (Art art : arts) {
                builder.append("\t");
                builder.append(art.getName());
                builder.append("\n");
                List<Sample> samples = art.getSamples();
                for (Sample sample : samples) {
                    builder.append("\t\t");
                    builder.append(sample.getInstance());
                    builder.append("\n");
                }
            }
        }

        PrintWriter writer = new PrintWriter("result/samples.txt");
        writer.write(builder.toString());
        writer.flush();
        writer.close();
    }

    private static Company findSamples(Company company, String[] codes){
        Company newCompany = new Company();
        List<Type> types = company.getTypes();
        List<Type> newTypes = new ArrayList<>();
        for (Type type : types){
            List<Art> newArts = new ArrayList<>();
            List<Art> arts = type.getArts();
            for (Art art : arts){
                List<Sample> newSamples = new ArrayList<>();
                List<Sample> samples = art.getSamples();
                for (Sample sample : samples){
                    for (String cod : codes) {
                        if (sample.getInstance().contains(cod)) {
                            newSamples.add(sample);
                        }
                    }
                }
                if (newSamples.size() > 0){
                    newArts.add(new Art(art.getName(), newSamples));
                }
            }
            if (newArts.size() > 0) {
                newTypes.add(new Type(type.getName(), newArts));
            }
        }
        newCompany.setTypes(newTypes);
        return newCompany;
    }
}
