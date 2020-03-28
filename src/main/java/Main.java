import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;
import java.util.ArrayList;

public class Main
{
    private static String file = "C:\\Users\\Valet\\OneDrive\\журнал 2\\РазноеК-1.xlsx";
    public static void main(String[] args)
    {
        ArrayList<Sample> samples = new ArrayList<>();
        try
        {
            FileInputStream inp = new FileInputStream(file);
            Workbook workbook = WorkbookFactory.create(inp);
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
                    if (cell == null || cell.getStringCellValue().isEmpty() || cell.getStringCellValue().replaceAll(" ", "").length()
                            < row.getCell(1).toString().length())
                    {
                        continue;
                    }
                    Sample sample = new Sample(sheet.getSheetName(), cell.getStringCellValue());
                    samples.add(sample);
                }
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        samples.forEach(System.out::println);
    }
}
