package lesson1;

public class Racetrack extends Obstacle
{
    int length;

    public Racetrack (int length) { setLength(length); }

    public final void setLength (int length) {
        this.length = length == 0 ? -1 : length;
    }

    public int getLength() {
        return length;
    }

    @Override
    public String getInfo() {
         int n = getLength();
         return "беговая дорожка " + (n < 0 ? "бесконечной длины" : "длиной " + n + "м");
    }

}
