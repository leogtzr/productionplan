package com.production;

import com.production.domain.WorkOrderInformation;
import com.production.util.ProductionUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.util.List;

public class App {

    public static void main(final String[] args) throws IOException, InvalidFormatException {

        // Note ...
        // TODO: Use a constant for this:
        final String path = "FAB Load by WC Leo.xls";
        final List<WorkOrderInformation> workOrderInformationItems = ProductionUtils.extractWorkOrdersFromSheetFile(path);

        // TODO: Use a constant for this ...
        ProductionUtils.reconcileInformationFromAgeFile("Age  by WC.xls", workOrderInformationItems);
        workOrderInformationItems.forEach(System.out::println);
    }

}