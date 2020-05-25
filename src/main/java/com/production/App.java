package com.production;

import com.production.domain.WorkOrderInformation;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class App {

    private static final int PART_CELL_INDEX = 4;
    private static final int WORKORDER_CELL_INDEX = 6;
    private static final int RUN_CELL_INDEX = 10;
    private static final int SETUP_CELL_INDEX = 9;
    private static final int QTY_CELL_INDEX = 12;

    private static final double RUN_EFFICIENCY = 0.8;

    public static void main(final String[] args) throws IOException, InvalidFormatException {

        final List<WorkOrderInformation> workOrderInfoItems = new ArrayList<>();
        final String path = "FAB Load by WC Leo.xls";

        try (final Workbook workbook = WorkbookFactory.create(new File(path))) {
            final Sheet fabLoadByWCSheet = workbook.getSheet("FAB Load by WC");
            final Iterator<Row> rowIterator = fabLoadByWCSheet.rowIterator();
            while (rowIterator.hasNext()) {
                final Row row = rowIterator.next();
                final Cell firstCell = row.getCell(0);
                final String firstCellStringCellValue = firstCell.getStringCellValue();
                switch (firstCellStringCellValue.trim()) {
                    case "WC Name":
                    case "Count":
                    case "Sum":
                        continue;
                }

                final Cell partCell = row.getCell(PART_CELL_INDEX);
                final Cell workOrderCell = row.getCell(WORKORDER_CELL_INDEX);
                final Cell runCell = row.getCell(RUN_CELL_INDEX);
                final Cell setupCell = row.getCell(SETUP_CELL_INDEX);
                final Cell qtyCell = row.getCell(QTY_CELL_INDEX);

                workOrderInfoItems.add(to(partCell, workOrderCell, runCell, setupCell, qtyCell));
            }
        }

        System.out.println(workOrderInfoItems);

    }

    private static WorkOrderInformation to(Cell partCell, Cell workOrderCell, Cell runCell, Cell setupCell, Cell qtyCell) {
        final WorkOrderInformation workOrderInformation = new WorkOrderInformation();
        workOrderInformation.setPartNumber(partCell.getStringCellValue().trim());
        workOrderInformation.setWorkOrder(workOrderCell.getStringCellValue().trim());

        final double runNumericCellValue = runCell.getNumericCellValue();

        workOrderInformation.setRunHours(runNumericCellValue * RUN_EFFICIENCY);
        workOrderInformation.setSetupHours(setupCell.getNumericCellValue());
        workOrderInformation.setQty((int)qtyCell.getNumericCellValue());

        return workOrderInformation;
    }

}