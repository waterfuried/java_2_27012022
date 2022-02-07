package lesson3;

/*
1. Создать массив с набором слов (10-20 слов, должны встречаться повторяющиеся).
   Найти и вывести список уникальных слов, из которых состоит массив (дубликаты не считаем).
   Посчитать сколько раз встречается каждое слово.
2. Написать простой класс ТелефонныйСправочник, который хранит в себе список фамилий и телефонных номеров.
   В этот телефонный справочник с помощью метода add() можно добавлять записи.
   С помощью метода get() искать номер телефона по фамилии.
   Следует учесть, что под одной фамилией может быть несколько телефонов (в случае однофамильцев),
   тогда при запросе такой фамилии должны выводиться все телефоны.
   Желательно как можно меньше добавлять своего, чего нет в задании (т.е. не надо в телефонную запись
   добавлять еще дополнительные поля (имя, отчество, адрес), делать взаимодействие с пользователем
   через консоль и т.д.). Консоль желательно не использовать (в том числе Scanner),
   тестировать просто из метода main() прописывая add() и get().
*/

import java.util.*;

public class Main {

    /**
     * ключом в справочнике выбран номер телефона, чтобы обеспечить его уникальность:
     * у каждого номера только один владелец
     *
     * метод changeOwner используется, если сменился владелец номера
     *
     * если номер абонента меняется (например, выбыл - переехал/умер),
     * используется add с новым номером, для старого вызвается changeOwner,
     * например, с пустой фамилией - пока не появится новый владелец
     */
    static class PhoneDirectory {
        Map<String, String> map = new HashMap<>();

        boolean add (String number, String name) {
            if (name != null && name.length() != 0 &&
                isValidNumber(number) && !map.containsKey(number)) {
                    map.put(number, name.toLowerCase());
                    return true;
                } else
                    return false;
        }

        String[] get (String name) {
            ArrayList<String> str = new ArrayList<>();
            if (name == null || !map.containsValue(name.toLowerCase())) {
                str.add("");
            } else {
                map.forEach((k, v) -> {
                    if (v.equals(name.toLowerCase())) str.add(k);
                });
            }
            return str.toArray(new String[] {});
        }

        boolean isEmpty (String[] str) {
            return str != null && str.length == 1 && str[0].length() == 0;
        }

        boolean isValidNumber (String number) {
            return number != null && number.length() != 0 &&
                   number.replaceAll("\\s","").matches("^\\d+$");
        }

        boolean changeOwner (String number, String name) {
            boolean res = name != null && isValidNumber(number) && map.containsKey(number);
            if (res) {
                res = !name.equalsIgnoreCase(map.get(number));
                if (res) map.replace(number, name.toLowerCase());
            }
            return res;
        }

        // привести фамилию (или части составной) к виду "с заглавной буквы"
        String capitalize (String s) {
            if (s == null || s.length() == 0) return s;
            String[] part = s.split("-");
            StringBuilder str = new StringBuilder();
            int i = 0;
            while (i < part.length) {
                str.append(part[i].substring(0, 1).toUpperCase()).append(part[i].substring(1));
                i++;
                if (i < part.length) str.append('-');
            }
            return str.toString();
        }

        void print (String name) {
            if (name != null) {
                String[] numbers = get(name.toLowerCase());
                String s = capitalize(name);
                if (isEmpty(numbers)) {
                    System.out.println(
                        name.length() > 0
                            ? "Фамилии \"" + s + "\" в справочнике нет"
                            : "Незанятых номеров нет");
                } else {
                    System.out.println(
                        (name.length() > 0
                            ? "Все телефоны для фамилии \"" + s + "\""
                            : "Список незянятых номеров")
                        + ":");
                    for (String n : numbers) System.out.println("\t" + n);
                }
            }
        }
    }

    public static void main (String[] args) {
        String[] str = {
            "яблоко", "груша", "киви", "Банан",
            "слива", "арбуз", "апельсин", "лимон",
            "дыня", null, "банан", "гранат", "груша",
            "Лимон", "яблоко", "Киви", null, "дыня",
            "банан", "киви", "ГРУША", "слива", "КИВИ",
            "киви"
        };

        Map<String, Integer> unique = new HashMap<>();

        for (String s : str) {
            if (s != null) {
                unique.put(
                    s.toLowerCase(),
                    unique.containsKey(s.toLowerCase()) ? unique.get(s.toLowerCase()) + 1 : 1);
            }
        }
        unique.forEach((k, v) ->
            System.out.println(k + " встречается " + v + " раз" + (v % 10 >= 2 && v % 10 <= 4 ? "а" : "")));

        PhoneDirectory dir = new PhoneDirectory();

        System.out.println("\nТелефонный справочник:");
        dir.add("499 123 45 67", "Иванов");
        dir.add("499 123 45 67", "Петров"); // не будет добавлен
        dir.add("495 345 67 80", "Петров");
        dir.add("499 125 25 75", "Сидоров");
        dir.add("495 495 49 54", "Иванов");
        dir.add("495 211 33 44", "Сидоров");
        dir.add("495 800 20 40", "Иванов");
        dir.add("495 773 77 61", "Кара-Мурза");
        dir.changeOwner("495 211 33 44", "");
        dir.add("499 765 43 21", "сидоров");
        //dir.changeOwner("495 211 33 44", "петров");
        dir.print("Иванов");
        dir.print("Петров");
        dir.print("петрров");
        dir.print("Сидоров");
        dir.print("кара-мурза");
        dir.print("");
    }
}