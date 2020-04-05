import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main
{
    private static String file = "C:\\Users\\Valet\\OneDrive\\журнал 2";
    public static void main(String[] args) throws IOException {
        ArrayList<Sample> samples = new ArrayList<>();
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
                        Sample sample = new Sample(sheet.getSheetName(), cell.getStringCellValue(), type[type.length - 1]);
                        samples.add(sample);
                    }
                }
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        samples.forEach(System.out::println);
    }
}
