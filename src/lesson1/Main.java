package lesson1;

/*
	1. Создайте три класса Человек, Кот, Робот, которые не наследуются от одного класса.
	   Эти классы должны уметь бегать и прыгать (методы просто выводят информацию о действии в консоль).
	2. Создайте два класса: беговая дорожка и стена, при прохождении через которые,
	   участники должны выполнять соответствующие действия (бежать или прыгать),
	   результат выполнения печатаем в консоль (успешно пробежал, не смог пробежать и т.д.).
	3. Создайте два массива: с участниками и препятствиями,
	   и заставьте всех участников пройти этот набор препятствий.
	4. * У препятствий есть длина (для дорожки) или высота (для стены),
	   а участников ограничения на бег и прыжки.
	   Если участник не смог пройти одно из препятствий,
	   то дальше по списку он препятствий не идет.
*/
public class Main {
    public static void main(String[] args) {
        Cat leo = new Cat("Леопольд");
        leo.setMaxRunDistance(400);

        Movable[] participant = new Movable[] {
            new Cat("Васька"),
            new Robot(1),
            leo,
            new Human("Вася"),
            new Human("Эдуард")
        };

        Obstacle[] obstacle = new Obstacle[] {
            new Racetrack(300),
            new Racetrack(-1),
            null,
            new Climbwall(5)
        };

        Movable[] curParticipant = new Movable[participant.length];
        System.arraycopy(participant, 0, curParticipant, 0, participant.length);

        for (Obstacle obs : obstacle) {
            System.out.println("Препятствие: " +
                    (obs == null || obs.getInfo() == null ? "неизвестный тип" : obs.getInfo()));
            int i = 0;
            while (i < curParticipant.length) {
                int obstType = 0;
                int obstSize = 0;
                if (obs instanceof Racetrack) {
                    obstSize = ((Racetrack) obs).length;
                } else if (obs instanceof Climbwall) {
                    obstType = 1;
                    obstSize = ((Climbwall) obs).height;
                } else {
                    obstType = -1;
                }
                if (curParticipant[i] != null) {
                    System.out.print("\tУчастник " + participant[i].getDesignation() + " ");
                    if (!curParticipant[i].overcome(obstType, obstSize)) {
                        System.out.print(" и выбывает");
                        curParticipant[i] = null;
                    }
                    System.out.println();
                }
                i++;
            }
        }
    }
}
