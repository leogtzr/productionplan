package com.production.input.sheet;

import com.production.menu.MealType;
import lombok.*;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RowTypeIndex {
    private MealType mealType;
    private int index;
}
