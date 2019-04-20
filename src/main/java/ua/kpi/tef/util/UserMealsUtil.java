package ua.kpi.tef.util;

import ua.kpi.tef.model.UserMeal;
import ua.kpi.tef.model.UserMealWithExceed;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;

/**
 * GKislin
 * 31.05.2015.
 */
public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> mealList = Arrays.asList(
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31, 20, 0), "Ужин", 510)
        );

        getFilteredWithExceeded(mealList, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000).forEach(System.out::println);
        System.out.println();
        getFilteredWithExceededStream(mealList, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000).forEach(System.out::println);
    }

    public static List<UserMealWithExceed> getFilteredWithExceeded(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        List<UserMeal> filteredUserMeal;
        Map<LocalDate, Integer> dayCalories;
        List<UserMealWithExceed> userMealWithExceeds;

        filteredUserMeal = filterUserMeal(mealList, startTime, endTime);
        dayCalories = countCaloriesPerDay(mealList);
        userMealWithExceeds = createUserMealsWithExceed(filteredUserMeal, dayCalories, caloriesPerDay);

        return userMealWithExceeds;
    }

    private static ArrayList<UserMeal> filterUserMeal(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime) {
        ArrayList<UserMeal> filteredUserMeal = new ArrayList<>();
        for (UserMeal meal : mealList) {
            if (isInTimeInterval(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                filteredUserMeal.add(meal);
            }
        }

        return filteredUserMeal;
    }

    private static Map<LocalDate, Integer> countCaloriesPerDay(List<UserMeal> mealList) {
        Map<LocalDate, Integer> dayCalories = new HashMap<>();

        for (UserMeal meal : mealList) {
            dayCalories.put(meal.getDateTime().toLocalDate(), dayCalories.getOrDefault(meal.getDateTime().toLocalDate(), 0) + meal.getCalories());
        }

        return dayCalories;
    }

    private static List<UserMealWithExceed> createUserMealsWithExceed(List<UserMeal> mealList, Map<LocalDate, Integer> dayCalories, int caloriesPerDay) {
        ArrayList<UserMealWithExceed> userMealWithExceeds = new ArrayList<>();
        for (UserMeal meal : mealList) {
            userMealWithExceeds.add(createUserMealWithExceed(meal, dayCalories.get(meal.getDateTime().toLocalDate()) > caloriesPerDay));
        }

        return userMealWithExceeds;
    }

    public static List<UserMealWithExceed> getFilteredWithExceededStream(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        List<UserMealWithExceed> userMealWithExceeds = new ArrayList<>();
        Map<LocalDate, Integer> dayCalories = new HashMap<>();

        mealList.forEach(meal -> dayCalories.put(meal.getDateTime().toLocalDate(), dayCalories.getOrDefault(meal.getDateTime().toLocalDate(), 0) + meal.getCalories()));

        mealList.stream().filter(meal -> isInTimeInterval(meal.getDateTime().toLocalTime(), startTime, endTime))
                .forEach(meal -> userMealWithExceeds.add(createUserMealWithExceed(meal, dayCalories.get(meal.getDateTime().toLocalDate()) > caloriesPerDay)));

        return userMealWithExceeds;
    }

    private static boolean isInTimeInterval(LocalTime value, LocalTime startTime, LocalTime endTime) {
        return value.isAfter(startTime) && value.isBefore(endTime);
    }

    private static UserMealWithExceed createUserMealWithExceed(UserMeal meal, boolean exceed) {
        return new UserMealWithExceed(meal.getDateTime(), meal.getDescription(), meal.getCalories(), exceed);
    }
}
