package com.production;

import com.production.domain.WorkOrderInformation;
import com.production.util.Constants;
import com.production.util.ProductionUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class App {

    public static void main(final String[] args) throws IOException, InvalidFormatException {

        final String path = "FAB Load by WC Leo.xls";
        final List<WorkOrderInformation> workOrderInformationItems = ProductionUtils.extractWorkOrdersFromSheetFile(path);

        workOrderInformationItems.forEach(System.out::println);

    }

}