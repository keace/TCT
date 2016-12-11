package ua.kyslytsia.tct.utils.excelExport;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class WriteExcelBasic {
    private static final String LOG = "Log WriteExcelBasic";

    /**
     *
     * @param fileName - the name to give the new workbook file
     * @return - a new WritableWorkbook with the given fileName
     */
    public WritableWorkbook createWorkbook (String fileName){
        //exports must use a temp file while writing to avoid memory hogging
        WorkbookSettings workbookSettings = new WorkbookSettings();
        workbookSettings.setUseTemporaryFileDuringWrite(true);

        //get the sdcard's storage directory
        File sdCard = Environment.getExternalStorageDirectory();

        //add on the your app's path
        File dir = new File(sdCard.getAbsolutePath() + "/JExcelApiTest");

        //make them in case they're not there
        if (!dir.mkdirs()) {
            Log.i(LOG, "Directory already exist");
        }

        //create a standard java.io.File object for the Workbook to use
        File workbookFile = new File(dir, fileName);

        WritableWorkbook writableWorkbook = null;

        //TODO Если файл есть, то не создавать, а получать его
        try {
            //create a new WritableWorkbook using the java.io.File and
            //WorkbookSettings from above
            writableWorkbook = Workbook.createWorkbook(workbookFile, workbookSettings);
        } catch (IOException ex) {
            Log.e(LOG, ex.getMessage());
        }
        return writableWorkbook;
    }

    /**
     *
     * @param writableWorkbook - WritableWorkbook to create new sheet in
     * @param sheetName - name to be given to new sheet
     * @param sheetIndex - position in sheet tabs at bottom of workbook
     * @return - a new WritableSheet in given WritableWorkbook
     */
    public WritableSheet createSheet (WritableWorkbook writableWorkbook, String sheetName, int sheetIndex){
        //create a new WritableSheet and return it
        return writableWorkbook.createSheet(sheetName, sheetIndex);
    }

    /**
     *
     * @param columnPosition - column to place new cell in
     * @param rowPosition - row to place new cell in
     * @param contents - string value to place in cell
     * @param headerCell - whether to give this cell special formatting
     * @param sheet - WritableSheet to place cell in
     * @throws WriteException - might be thrown
     */
    public void writeCell (WritableSheet sheet, int columnPosition, int rowPosition, String contents, boolean headerCell) throws WriteException {
        //create a new cell with contents at position
        Label newCell = new Label(columnPosition, rowPosition, contents);

        if (headerCell) {
            //give header cells size 10, Arial, bold
            WritableFont headerFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
            WritableCellFormat headerFormat = new WritableCellFormat(headerFont);

            //center align the cells' contents
            headerFormat.setAlignment(Alignment.CENTRE);
            newCell.setCellFormat(headerFormat);
        }

        //TODO можно сделать Left-Header with no-center align and set constants to different formatting
        sheet.addCell(newCell);
    }
}
