package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(
            List<UserMeal> meals,
            LocalTime startTime,
            LocalTime endTime,
            int caloriesPerDay
    ) {
        final int startT = startTime.get(ChronoField.MINUTE_OF_DAY);
        final int entT = endTime.get(ChronoField.MINUTE_OF_DAY);
        final List<UserMealWithExcess> listUserMealWithExcess = new ArrayList<>();
        final Map<LocalDate, Integer> sumCaloriesDay = new HashMap<>();
        meals.forEach(e -> {
            final int timeM = e.getDateTime().toLocalTime().get(ChronoField.MINUTE_OF_DAY);
            final int summColories = sumCaloriesDay.getOrDefault(e.getDateTime().toLocalDate(), 0) + e.getCalories();
            sumCaloriesDay.put(e.getDateTime().toLocalDate(), summColories);
            if (timeM > startT && timeM < entT) {
                listUserMealWithExcess.add(new UserMealWithExcess(
                        e.getDateTime(),
                        e.getDescription(),
                        e.getCalories(),
                        false
                ));
            }
        });
        listUserMealWithExcess.forEach(e -> {
            if (sumCaloriesDay.get(e.getDateTime().toLocalDate()) > caloriesPerDay) {
                e.setExcess(true);
            }
        });
        return listUserMealWithExcess;
    }

    public static List<UserMealWithExcess> filteredByStreams(
            List<UserMeal> meals,
            LocalTime startTime,
            LocalTime endTime,
            int caloriesPerDay
    ) {
        final int startT = startTime.get(ChronoField.MINUTE_OF_DAY);
        final int entT = endTime.get(ChronoField.MINUTE_OF_DAY);

        final Map<LocalDate, Integer> sumCaloriesDay = meals
                .stream()
                .collect(Collectors.toMap(
                        p -> p.getDateTime().toLocalDate(),
                        p -> p.getCalories(),
                        (cal1, cal2) -> cal1 + cal2
                ));

        final List<UserMealWithExcess> listUserMealWithExcess = meals
                .stream().
                        filter(s -> {
                                    int minuteOfDay = s.getDateTime().toLocalTime().get(ChronoField.MINUTE_OF_DAY);
                                    return minuteOfDay > startT && minuteOfDay < entT;
                                }
                        ).map(s -> {
                    if (sumCaloriesDay.get(s.getDateTime().toLocalDate()) > caloriesPerDay) {
                        return new UserMealWithExcess(s.getDateTime(), s.getDescription(), s.getCalories(), true);
                    } else {
                        return new UserMealWithExcess(s.getDateTime(), s.getDescription(), s.getCalories(), false);
                    }
                }).collect(Collectors.toList());
        return listUserMealWithExcess;
    }
}
