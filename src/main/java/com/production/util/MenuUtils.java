package com.production.util;

import com.production.input.sheet.DayColumnIndexes;
import com.production.input.sheet.InputSheetFileRowIndexes;
import com.production.input.sheet.RowTypeIndex;
import com.production.menu.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.production.menu.MealType.*;
import static com.production.util.Constants.DEFAULT_TITLE_ROW_INDEX_POSITION;
import static com.production.util.Constants.MAX_VALUE_TO_FIND_TITLE;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.*;

public final class MenuUtils {

    private static Optional<Food> extractMealInfoFromRow(
            final Row row,
            final DayColumnIndexes index,
            final MealType mealType
    ) {
        final Food food = new Food();

        final Cell cellName = row.getCell(index.index());
        final String value = cellName.getStringCellValue();
        if (cellName == null || value.trim().isEmpty()) {
            return Optional.empty();
        }
        final Cell cellKCal = row.getCell(index.index() + 1);

        final int kCal = (int)cellKCal.getNumericCellValue();
        food.setKCal(kCal);
        food.setMealType(mealType);
        food.setName(cellName.getStringCellValue());

        return Optional.of(food);
    }

    private static Optional<SaladBar> extractFromSaladsRow(
            final Sheet sheet
            , final DayColumnIndexes dayIndex
    , final RowTypeIndex rix) {

        final Row r = sheet.getRow(rix.getIndex());
        final Row r2 = sheet.getRow(rix.getIndex() + 1);
        final Row r3 = sheet.getRow(rix.getIndex() + 2);
        final Row[] saladsRows = {r, r2, r3};

        final Cell salads1 = saladsRows[0].getCell(dayIndex.index());
        final Cell salads2 = saladsRows[1].getCell(dayIndex.index());
        final Cell salads3 = saladsRows[2].getCell(dayIndex.index());

        if (!salads1.getStringCellValue().isEmpty()
                && !salads2.getStringCellValue().isEmpty()
                && !salads3.getStringCellValue().isEmpty()) {
            return Optional.of(
                    new SaladBar(
                            asList(salads1.getStringCellValue(), salads2.getStringCellValue(), salads3.getStringCellValue())
                    )
            );
        }
        return Optional.empty();
    }

    private static Optional<Integer> titleTagPosition(final Sheet sheet) {
        int pos = DEFAULT_TITLE_ROW_INDEX_POSITION;

        for (int i = 1; i < MAX_VALUE_TO_FIND_TITLE; i++) {
            final Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            final Cell cell = row.getCell(0);
            final String value = cell.getStringCellValue();
            if (value.contains("MENU SEMANAL DEL") || value.contains("MENUS") || value.contains("MENU DEL")) {
                pos = i;
                break;
            }
        }

        return pos >= MAX_VALUE_TO_FIND_TITLE ? Optional.empty() : Optional.of(pos);
    }

    public static Optional<Menu> extractMenuFromSheet(final Sheet sheet) {

        final Optional<Integer> titleRowPosition = MenuUtils.titleTagPosition(sheet);
        if (titleRowPosition.isEmpty()) {
            return Optional.empty();
        }

        final InputSheetFileRowIndexes indexes = new InputSheetFileRowIndexes();
        titleRowPosition.ifPresent(pos -> indexes.adjust(pos));

        final Menu menu = new Menu();
        final String title = sheet.getRow(indexes.title().getIndex()).getCell(0).getStringCellValue();
        menu.setTitle(title);

        final Map<Day, List<Food>> map = new TreeMap<>();

        indexes.values().stream().filter(rix -> rix.getMealType() != TITLE).forEach(rix -> {
            final Row r = sheet.getRow(rix.getIndex());
            for (final DayColumnIndexes dayIndex : DayColumnIndexes.values()) {
                if (rix.getMealType() == SALADS) {
                    final Row r2 = sheet.getRow(rix.getIndex() + 1);
                    final Row r3 = sheet.getRow(rix.getIndex() + 2);
                    final Optional<SaladBar> salads = extractFromSaladsRow(sheet, dayIndex, rix);
                    salads.ifPresent(sl ->
                            map.computeIfAbsent(dayIndex.day(), k -> new ArrayList<>()).addAll(
                                    sl.getSalads().stream().map(salad -> new Food(salad, 0, SALADS)).collect(toList())
                            ));
                } else {
                    MenuUtils.extractMealInfoFromRow(r, dayIndex, rix.getMealType())
                            .ifPresent(f -> map.computeIfAbsent(dayIndex.day(), k -> new ArrayList<>()).add(f));
                }
            }
        });

        final Map<Day, DayMeal> dayMenu = new TreeMap<>();
        Arrays.stream(Day.values()).forEach(day -> dayMenu.put(day, toDayMeal(map, day)));

        menu.setMenu(dayMenu);

        return Optional.of(menu);
    }

    private static DayMeal toDayMeal(final Map<Day, List<Food>> map, final Day day) {
        final DayMeal dayMeal = new DayMeal();

        final List<Food> foods = map.get(day);
        final List<String> salads = foods.stream()
                .filter(f -> f.getMealType() == SALADS)
                .map(sl -> sl.getName())
                .collect(toList());

        dayMeal.setSalads(salads);

        foods.stream().filter(f -> f.getMealType() == BREAKFAST)
                .findFirst()
                .ifPresent(dayMeal::setBreakfast);

        final List<Food> lunchDinnerAlternatives = foods
                .stream()
                .filter(f -> (f.getMealType() != SALADS) && (f.getMealType() != BREAKFAST))
                .collect(toList());

        dayMeal.setLunchDinnerFoodAlternatives(lunchDinnerAlternatives);

        return dayMeal;
    }

    private MenuUtils() {}

    public static List<Menu> extractMenuFromFile(final String path) throws IOException, InvalidFormatException {
        final List<Menu> menus = new ArrayList<>();

        try (final Workbook workbook = WorkbookFactory.create(new File(path))) {
            final int numberOfSheets = workbook.getNumberOfSheets();
            for (int i = 0; i < numberOfSheets; i++) {
                final Sheet sheet = workbook.getSheetAt(i);
                final Optional<Menu> menu = extractMenuFromSheet(sheet);
                menu.ifPresent(menus::add);
            }
        }

        return menus;
    }
}

