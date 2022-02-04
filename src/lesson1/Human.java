package lesson1;

public class Human implements Movable
{
    int totalNumber = 0;

    String name;

    int maxRunDistance, maxJumpHeight;

    void setLimits() {
        maxRunDistance = 500;
        maxJumpHeight = 2;
    }

    public Human () { setLimits(); totalNumber++; }

    public Human (String name) { setLimits(); this.name = name; totalNumber++; }

    public Human (int maxRunDistance, int maxJumpHeight) {
        setLimits();
        if (maxRunDistance >= 0) this.maxRunDistance = maxRunDistance;
        if (maxJumpHeight >= 0) this.maxJumpHeight = maxJumpHeight;
        totalNumber++;
    }

    public void setMaxRunDistance (int maxRunDistance) {
        this.maxRunDistance = maxRunDistance < 0 ? 200 : maxRunDistance;
    }

    public void setMaxJumpHeight (int maxJumpDistance) {
        this.maxJumpHeight = Math.max(maxJumpHeight, 0);
    }

    public int getMaxRunDistance() {
        return maxRunDistance;
    }

    public int getMaxJumpHeight() {
         return maxJumpHeight;
    }

    public int getCount() { return totalNumber; }

    @Override
    public boolean run (int distance) {
        System.out.print(
                distance < 0
                        ? (maxRunDistance >= 0 ? "не " : "") + "может бегать бесконечно долго"
                        : (maxRunDistance < 0
                            ? ""
                            : distance <= maxRunDistance
                                ? ""
                                : "не ") + "пробежал " + distance + "м");
        return distance < 0 ? maxRunDistance < 0 : distance <= maxRunDistance || maxRunDistance < 0;
    }

    @Override
    public boolean jump (int height) {
        System.out.print(
                height < 0
                        ? (maxJumpHeight >= 0 ? "не " : "") + "может прыгать бесконечно высоко"
                        : (maxJumpHeight < 0
                            ? ""
                            : height <= maxJumpHeight
                                ? ""
                                : "не ") + "прыгнул вверх на " + height + "м");
        return height < 0 ? maxJumpHeight < 0 : height <= maxJumpHeight || maxJumpHeight < 0;
    }

    @Override
    public String getDesignation() {
        return name == null || name.length() == 0
            ? "безымянный человек"
            : "человек по имени " + name;
    }
}
