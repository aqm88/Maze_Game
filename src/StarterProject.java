import edu.usu.graphics.Color;
import edu.usu.graphics.Graphics2D;

public class StarterProject {
    public static void main(String[] args) {
        try (Graphics2D graphics = new Graphics2D(1920, 1080, "Maze")) {
            Color goldenSand = new Color(194f / 255f, 178f / 255f, 128f / 255f);
            graphics.initialize(goldenSand);
            Game game = new Game(graphics);
            game.initialize();
            game.run();
            game.shutdown();
        }
    }
}