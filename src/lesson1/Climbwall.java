package lesson1;

public class Climbwall extends Obstacle
{
    int height;

    public Climbwall (int height) { setHeight(height); }

    public final void setHeight (int height) {
        this.height = height == 0 ? -1 : height;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public String getInfo() {
         int n = getHeight();
         return "стенка " + (n < 0 ? "бесконечной высоты" : "высотой " + n + "м");
    }

}
