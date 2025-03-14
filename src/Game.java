import edu.usu.graphics.*;
import org.joml.Vector2f;

import java.util.*;

import static org.lwjgl.glfw.GLFW.*;

public class Game {
    private final Graphics2D graphics;
    private MazeGenerator.MazeCell[][] maze;
    private MazeGenerator mazeGen;
    private int size;
    private float startingMazeX = -0.5f;
    private float startingMazeY = -0.45f;
    private float startingMazeWidth = 1f;
    private float highScoreBoardX = 0.58f;
    private float highScoreBoardY = -0.15f;
    private float highScoreBoardWidth = 0.35f;
    private float highScoreBoardHeight = 0.5f;
    private float monkeyX = 0.5f;
    private float monkeyY = -0.45f;
    private float monkeyWidth = 0.65f;
    private float monkeyHeight = 1f;
    private float instructionX = -0.99f;
    private float instructionY = -0.55f;
    private float instructionWidth = 0.48f;
    private float instructionHeight = 1.1f;
    private Texture HighScoreTex;
    private final Rectangle highScoreRect = new Rectangle(highScoreBoardX, highScoreBoardY,highScoreBoardWidth, highScoreBoardHeight, 0);
    private final Rectangle highScoreRect1 = new Rectangle(highScoreBoardX - 0.50f, highScoreBoardY,highScoreBoardWidth, highScoreBoardHeight, 0);
    private final Rectangle highScoreRect2 = new Rectangle(highScoreBoardX - 1.00f, highScoreBoardY,highScoreBoardWidth, highScoreBoardHeight, 0);
    private final Rectangle highScoreRect3 = new Rectangle(highScoreBoardX - 1.50f, highScoreBoardY,highScoreBoardWidth, highScoreBoardHeight, 0);
    private final Rectangle creditRectangle = new Rectangle(startingMazeX - 0.4f, startingMazeY - 0.06f, startingMazeWidth, startingMazeWidth,-1);
    private Texture instructionTex;
    private Texture creditTex;
    private Rectangle instructionRect = new Rectangle(instructionX, instructionY, instructionWidth, instructionHeight, 0);
    private final Rectangle mazeBackground = new Rectangle(startingMazeX, startingMazeY, startingMazeWidth, startingMazeWidth,-1);
    private Texture backgroundTexture;
    private Texture playerIcon;
    private Rectangle playerRect;
    private Texture endIcon;
    private Rectangle endRect;
    private Texture shortestPathTex;
    private Rectangle shortestPathRect;
    private Texture breadcrumbTex;
    private Rectangle breadcrumbRect;
    private Texture monkeyTex;
    private Texture cannibalTex1;
    private Texture cannibalTex2;
    private Texture cannibalTex3;
    private Rectangle monkeyRect= new Rectangle(monkeyX, monkeyY, monkeyWidth,monkeyHeight,-1);
    private Rectangle cannibalRect1= new Rectangle(monkeyX- 0.51f, monkeyY, monkeyWidth- 0.1f,monkeyHeight,-1);
    private Rectangle cannibalRect2= new Rectangle(monkeyX- 1.02f, monkeyY, monkeyWidth -0.1f,monkeyHeight,-1);
    private Rectangle cannibalRect3= new Rectangle(monkeyX- 1.54f, monkeyY, monkeyWidth - 0.1f,monkeyHeight,-1);
    private int playerX;
    private int playerY;
    private Map<String, Boolean> keyState = new HashMap<>();
    Font font;
    Font highScoreFont;
    private double gameTime;
    private int timeMinutes;
    private int timeSeconds;
    private int score;
    private List<MazeGenerator.MazeCell> shortestPath;
    private List<MazeGenerator.MazeCell> startingShortestPath;
    private boolean renderShortestPath;
    private boolean renderHint;
    private boolean renderBreadcrumb;
    private boolean timerRunning;
    private boolean stopMovement;
    private boolean renderGame;
    private boolean renderHighScore;
    private boolean renderCredits;
    private List<Integer> fiveHighScore;
    private List<Integer> tenHighScore;
    private List<Integer> fifteenHighScore;
    private List<Integer> twentyHighScore;


    public Game(Graphics2D graphics) {
        this.graphics = graphics;
    }

    public void initialize() {
        backgroundTexture = new Texture("resources/images/Monkey_Island_Background.png");
        playerIcon = new Texture("resources/images/Murray.png");
        endIcon = new Texture("resources/images/Grog_Chicken.png");
        shortestPathTex = new Texture("resources/images/Grog_mug.png");
        breadcrumbTex = new Texture("resources/images/Breadcrumb_Bone.png");
        monkeyTex = new Texture("resources/images/Three_Headed_Monkey.png");
        HighScoreTex = new Texture("resources/images/High_Score_Background.png");
        instructionTex = new Texture("resources/images/Instruction_Sign.png");
        cannibalTex1 = new Texture("resources/images/Cannibal_1.png");
        cannibalTex2 = new Texture("resources/images/Cannibal_2.png");
        cannibalTex3 = new Texture("resources/images/Cannibal_3.png");
        creditTex = new Texture("resources/images/Credit.png");
        font = new Font("resources/fonts/BlackAndWhitePicture-Regular.ttf",42, false);
        highScoreFont = new Font("resources/fonts/BlackAndWhitePicture-Regular.ttf",42, true);
        score = 0;
        timeSeconds = 0;
        timeMinutes= 0;
        gameTime =0.0f;
        renderShortestPath = false;
        renderHint = false;
        renderGame = true;
        renderHighScore = false;
        renderCredits = false;
        renderBreadcrumb = false;
        timerRunning = false;
        stopMovement = false;
        fiveHighScore = new ArrayList<Integer>();
        tenHighScore = new ArrayList<Integer>();
        fifteenHighScore = new ArrayList<Integer>();
        twentyHighScore = new ArrayList<Integer>();



    }

    public void shutdown() {
    }

    public void run() {
        // Grab the first time
        double previousTime = glfwGetTime();

        while (!graphics.shouldClose()) {
            double currentTime = glfwGetTime();
            double elapsedTime = currentTime - previousTime;    // elapsed time is in seconds
            previousTime = currentTime;

            processInput(elapsedTime);
            update(elapsedTime);
            render(elapsedTime);
        }
    }

    private void processInput(double elapsedTime) {
        // Poll for window events: required in order for window, keyboard, etc events are captured.
        glfwPollEvents();
        if(renderGame) {
            // If user presses ESC, then exit the program
            if (glfwGetKey(graphics.getWindow(), GLFW_KEY_ESCAPE) == GLFW_PRESS) {
                glfwSetWindowShouldClose(graphics.getWindow(), true);
            }
            // If user presses F1, generate a new 5x5 maze
            if (glfwGetKey(graphics.getWindow(), GLFW_KEY_F1) == GLFW_PRESS) {
                if (!keyState.getOrDefault("F1", false)) {
                    keyState.put("F1", true);
                    mazeGen = new MazeGenerator(5);
                    maze = mazeGen.getMaze();
                    shortestPath = mazeGen.getShortestPath();
                    startingShortestPath = shortestPath;
                    size = 5;
                    playerX = mazeGen.getStartX();
                    playerY = mazeGen.getStartY();
                    renderHint = false;
                    renderShortestPath = false;
                    renderBreadcrumb = false;
                    timerRunning = true;
                    stopMovement = false;
                    gameTime = 0.0;
                    score = 0;
                }
            } else {
                keyState.put("F1", false);
            }
            // If user presses F2, generate a new 10x10 maze
            if (glfwGetKey(graphics.getWindow(), GLFW_KEY_F2) == GLFW_PRESS) {
                if (!keyState.getOrDefault("F2", false)) {
                    keyState.put("F2", true);
                    mazeGen = new MazeGenerator(10);
                    maze = mazeGen.getMaze();
                    shortestPath = mazeGen.getShortestPath();
                    startingShortestPath = shortestPath;
                    size = 10;
                    playerX = mazeGen.getStartX();
                    playerY = mazeGen.getStartY();
                    renderHint = false;
                    renderShortestPath = false;
                    renderBreadcrumb = false;
                    timerRunning = true;
                    stopMovement = false;
                    gameTime = 0.0;
                    score = 0;
                }
            } else {
                keyState.put("F2", false);
            }
            // If user presses F1, generate a new 15x15 maze
            if (glfwGetKey(graphics.getWindow(), GLFW_KEY_F3) == GLFW_PRESS) {
                if (!keyState.getOrDefault("F3", false)) {
                    keyState.put("F3", true);
                    mazeGen = new MazeGenerator(15);
                    maze = mazeGen.getMaze();
                    shortestPath = mazeGen.getShortestPath();
                    startingShortestPath = shortestPath;
                    size = 15;
                    playerX = mazeGen.getStartX();
                    playerY = mazeGen.getStartY();
                    renderHint = false;
                    renderShortestPath = false;
                    renderBreadcrumb = false;
                    timerRunning = true;
                    stopMovement = false;
                    gameTime = 0.0;
                    score = 0;
                }
            } else {
                keyState.put("F3", false);
            }
            // If user presses F1, generate a new 20x20 maze
            if (glfwGetKey(graphics.getWindow(), GLFW_KEY_F4) == GLFW_PRESS) {
                if (!keyState.getOrDefault("F4", false)) {
                    keyState.put("F4", true);
                    mazeGen = new MazeGenerator(20);
                    maze = mazeGen.getMaze();
                    shortestPath = mazeGen.getShortestPath();
                    startingShortestPath = shortestPath;
                    size = 20;
                    playerX = mazeGen.getStartX();
                    playerY = mazeGen.getStartY();
                    renderHint = false;
                    renderShortestPath = false;
                    renderBreadcrumb = false;
                    timerRunning = true;
                    stopMovement = false;
                    gameTime = 0.0;
                    score = 0;
                }
            } else {
                keyState.put("F4", false);
            }
            // If user presses F5, Show High Scores
            if (glfwGetKey(graphics.getWindow(), GLFW_KEY_F5) == GLFW_PRESS) {
                if (!keyState.getOrDefault("F5", false)) {
                    keyState.put("F5", true);
                    renderGame = false;
                    renderCredits = false;
                    renderHighScore = true;
                }
            }else{
                keyState.put("F5", false);
            }
            // If user presses F6, Show Credits
            if (glfwGetKey(graphics.getWindow(), GLFW_KEY_F6) == GLFW_PRESS) {
                if (!keyState.getOrDefault("F6", false)) {
                    keyState.put("F6", true);
                    renderGame = false;
                    renderCredits = true;
                    renderHighScore = false;
                }
            }else{
                keyState.put("F6", false);
            }
            if (!stopMovement) {
                // If user presses Right Arrow, check if they can move that way and if they can update they're location
                if (glfwGetKey(graphics.getWindow(), GLFW_KEY_RIGHT) == GLFW_PRESS
                        || glfwGetKey(graphics.getWindow(), GLFW_KEY_D) == GLFW_PRESS
                        || glfwGetKey(graphics.getWindow(), GLFW_KEY_L) == GLFW_PRESS) {
                    if (!keyState.getOrDefault("RIGHT", false)) {
                        keyState.put("RIGHT", true);
                        if (maze[playerX][playerY].connected.contains("RIGHT")) {
                            playerX = playerX + 1;
                            if (startingShortestPath.contains(maze[playerX][playerY]) && !maze[playerX][playerY].been_visited) {
                                score += 5;
                            } else if (!maze[playerX][playerY].been_visited) {
                                score -= 2;
                            }
                            if (maze[playerX][playerY].end) {
                                timerRunning = false;
                                stopMovement = true;
                                if (size == 5) {
                                    fiveHighScore = addScore(fiveHighScore, score);
                                } else if (size == 10) {
                                    tenHighScore = addScore(tenHighScore, score);
                                } else if (size == 15) {
                                    fifteenHighScore = addScore(fifteenHighScore, score);
                                } else if (size == 20) {
                                    twentyHighScore = addScore(twentyHighScore, score);
                                }
                            }
                            maze[playerX][playerY].been_visited = true;
                            if (shortestPath.contains(maze[playerX][playerY])) {
                                shortestPath.remove(shortestPath.size() - 1);
                            } else {
                                shortestPath.add(maze[playerX - 1][playerY]);
                            }
                        }
                    }
                } else {
                    keyState.put("RIGHT", false);
                }
                // If user presses Left Arrow, check if they can move that way and if they can update they're location
                if (glfwGetKey(graphics.getWindow(), GLFW_KEY_LEFT) == GLFW_PRESS
                        || glfwGetKey(graphics.getWindow(), GLFW_KEY_A) == GLFW_PRESS
                        || glfwGetKey(graphics.getWindow(), GLFW_KEY_J) == GLFW_PRESS) {
                    if (!keyState.getOrDefault("LEFT", false)) {
                        keyState.put("LEFT", true);
                        if (maze[playerX][playerY].connected.contains("LEFT")) {
                            playerX = playerX - 1;
                            if (startingShortestPath.contains(maze[playerX][playerY]) && !maze[playerX][playerY].been_visited) {
                                score += 5;
                            } else if (!maze[playerX][playerY].been_visited) {
                                score -= 2;
                            }
                            if (maze[playerX][playerY].end) {
                                timerRunning = false;
                                stopMovement = true;
                                if (size == 5) {
                                    fiveHighScore = addScore(fiveHighScore, score);
                                } else if (size == 10) {
                                    tenHighScore = addScore(tenHighScore, score);
                                } else if (size == 15) {
                                    fifteenHighScore = addScore(fifteenHighScore, score);
                                } else if (size == 20) {
                                    twentyHighScore = addScore(twentyHighScore, score);
                                }
                            }
                            maze[playerX][playerY].been_visited = true;
                            if (shortestPath.contains(maze[playerX][playerY])) {
                                shortestPath.remove(shortestPath.size() - 1);
                            } else {
                                shortestPath.add(maze[playerX + 1][playerY]);
                            }
                        }
                    }
                } else {
                    keyState.put("LEFT", false);
                }
                // If user presses Up Arrow, check if they can move that way and if they can update they're location
                if (glfwGetKey(graphics.getWindow(), GLFW_KEY_UP) == GLFW_PRESS
                        || glfwGetKey(graphics.getWindow(), GLFW_KEY_W) == GLFW_PRESS
                        || glfwGetKey(graphics.getWindow(), GLFW_KEY_I) == GLFW_PRESS) {
                    if (!keyState.getOrDefault("UP", false)) {
                        keyState.put("UP", true);
                        if (maze[playerX][playerY].connected.contains("UP")) {
                            playerY = playerY - 1;
                            if (startingShortestPath.contains(maze[playerX][playerY]) && !maze[playerX][playerY].been_visited) {
                                score += 5;
                            } else if (!maze[playerX][playerY].been_visited) {
                                score -= 2;
                            }
                            if (maze[playerX][playerY].end) {
                                timerRunning = false;
                                stopMovement = true;
                                if (size == 5) {
                                    fiveHighScore = addScore(fiveHighScore, score);
                                } else if (size == 10) {
                                    tenHighScore = addScore(tenHighScore, score);
                                } else if (size == 15) {
                                    fifteenHighScore = addScore(fifteenHighScore, score);
                                } else if (size == 20) {
                                    twentyHighScore = addScore(twentyHighScore, score);
                                }
                            }
                            maze[playerX][playerY].been_visited = true;
                            if (shortestPath.contains(maze[playerX][playerY])) {
                                shortestPath.remove(shortestPath.size() - 1);
                            } else {
                                shortestPath.add(maze[playerX][playerY + 1]);
                            }
                        }
                    }
                } else {
                    keyState.put("UP", false);
                }
                // If user presses Down Arrow, check if they can move that way and if they can update they're location
                if (glfwGetKey(graphics.getWindow(), GLFW_KEY_DOWN) == GLFW_PRESS
                        || glfwGetKey(graphics.getWindow(), GLFW_KEY_S) == GLFW_PRESS
                        || glfwGetKey(graphics.getWindow(), GLFW_KEY_K) == GLFW_PRESS) {
                    if (!keyState.getOrDefault("DOWN", false)) {
                        keyState.put("DOWN", true);
                        if (maze[playerX][playerY].connected.contains("DOWN")) {
                            playerY = playerY + 1;
                            if (startingShortestPath.contains(maze[playerX][playerY]) && !maze[playerX][playerY].been_visited) {
                                score += 5;
                            } else if (!maze[playerX][playerY].been_visited) {
                                score -= 2;
                            }
                            if (maze[playerX][playerY].end) {
                                timerRunning = false;
                                stopMovement = true;
                                if (size == 5) {
                                    fiveHighScore = addScore(fiveHighScore, score);
                                } else if (size == 10) {
                                    tenHighScore = addScore(tenHighScore, score);
                                } else if (size == 15) {
                                    fifteenHighScore = addScore(fifteenHighScore, score);
                                } else if (size == 20) {
                                    twentyHighScore = addScore(twentyHighScore, score);
                                }
                            }
                            maze[playerX][playerY].been_visited = true;
                            if (shortestPath.contains(maze[playerX][playerY])) {
                                shortestPath.remove(shortestPath.size() - 1);
                            } else {
                                shortestPath.add(maze[playerX][playerY - 1]);
                            }
                        }
                    }
                } else {
                    keyState.put("DOWN", false);
                }
                // If user presses p, toggle the shortest path on and off
                if (glfwGetKey(graphics.getWindow(), GLFW_KEY_P) == GLFW_PRESS) {
                    if (!keyState.getOrDefault("P", false)) {
                        keyState.put("P", true);
                        renderShortestPath = !renderShortestPath;
                        renderHint = false;
                    }
                } else {
                    keyState.put("P", false);
                }
                // If user presses h, toggle the hints on and off
                if (glfwGetKey(graphics.getWindow(), GLFW_KEY_H) == GLFW_PRESS) {
                    if (!keyState.getOrDefault("H", false)) {
                        keyState.put("H", true);
                        renderHint = !renderHint;
                        renderShortestPath = false;
                    }
                } else {
                    keyState.put("H", false);
                }
                // If user presses b, toggle breadcrumbs on and off
                if (glfwGetKey(graphics.getWindow(), GLFW_KEY_B) == GLFW_PRESS) {
                    if (!keyState.getOrDefault("B", false)) {
                        keyState.put("B", true);
                        renderBreadcrumb = !renderBreadcrumb;
                    }
                } else {
                    keyState.put("B", false);
                }
            }
        } else if (renderHighScore) {
            if (glfwGetKey(graphics.getWindow(), GLFW_KEY_ENTER) == GLFW_PRESS) {
                renderGame = true;
                renderHighScore = false;
                renderCredits = false;
            }
            // If user presses ESC, then exit the program
            if (glfwGetKey(graphics.getWindow(), GLFW_KEY_ESCAPE) == GLFW_PRESS) {
                glfwSetWindowShouldClose(graphics.getWindow(), true);
            }

        } else if (renderCredits){
            if (glfwGetKey(graphics.getWindow(), GLFW_KEY_ENTER) == GLFW_PRESS) {
                renderCredits = false;
                renderHighScore = false;
                renderGame = true;
            }
            // If user presses ESC, then exit the program
            if (glfwGetKey(graphics.getWindow(), GLFW_KEY_ESCAPE) == GLFW_PRESS) {
                glfwSetWindowShouldClose(graphics.getWindow(), true);
            }
        }
    }

    private void update(double elapsedTime) {
        if(timerRunning) {
            gameTime += elapsedTime;
            timeMinutes = (int) gameTime / 60;
            timeSeconds = (int) gameTime - (timeMinutes * 60);
        }
        if(score < 0 ){
            score = 0;
        }
    }

    private void render(double elapsedTime) {
        graphics.begin();
        if(renderCredits){
            graphics.draw(creditTex,creditRectangle, Color.WHITE);
            graphics.drawTextByHeight(font, "Created By:", 0.40f, -0.25f,0.1f, Color.YELLOW);
            graphics.drawTextByHeight(font, "Ammon Hanks", 0.37f, -0.15f,0.1f, Color.YELLOW);
            graphics.drawTextByHeight(font, "With A Lot Of Resources", 0.30f, 0.05f,0.08f, Color.YELLOW);
            graphics.drawTextByHeight(font, "From The Monkey Island Games!", 0.20f, 0.13f,0.08f, Color.YELLOW);
            graphics.drawTextByHeight(font, "Press Enter To Return To Game!", 0.20f, 0.3f, 0.08f, Color.YELLOW);


        } else if (renderHighScore) {
            graphics.drawTextByHeight(font, "Press Enter To Return To Game!", -0.30f, -0.55f, 0.08f, Color.YELLOW);

            graphics.draw(HighScoreTex, highScoreRect, Color.WHITE);
            graphics.draw(monkeyTex,monkeyRect, Color.WHITE);
            graphics.draw(HighScoreTex, highScoreRect1, Color.WHITE);
            graphics.draw(cannibalTex1, cannibalRect1, Color.WHITE);
            graphics.draw(HighScoreTex, highScoreRect2, Color.WHITE);
            graphics.draw(cannibalTex2,cannibalRect2, Color.WHITE);
            graphics.draw(HighScoreTex, highScoreRect3, Color.WHITE);
            graphics.draw(cannibalTex3,cannibalRect3, Color.WHITE);


            graphics.drawTextByHeight(highScoreFont, "High Scores:", highScoreBoardX + (highScoreBoardWidth / 8), highScoreBoardY + 0.04f, 0.075f, Color.YELLOW);
            graphics.drawTextByHeight(highScoreFont, "(5x5)", highScoreBoardX + (highScoreBoardWidth / 2.6f), highScoreBoardY + 0.04f + 0.075f, 0.05f, Color.YELLOW);
            for (int i = 0; i < 5; i++) {
                if (fiveHighScore.size() > i) {
                    graphics.drawTextByHeight(highScoreFont, String.valueOf(fiveHighScore.get(i)), highScoreBoardX + (highScoreBoardWidth / 2.5f), highScoreBoardY + 0.17f + (0.06f * i), 0.06f, Color.YELLOW);
                } else {
                    graphics.drawTextByHeight(highScoreFont, "---", highScoreBoardX + (highScoreBoardWidth / 2.5f), highScoreBoardY + 0.17f + (0.06f * i), 0.06f, Color.YELLOW);
                }
            }

            graphics.drawTextByHeight(highScoreFont, "High Scores:", highScoreBoardX + (highScoreBoardWidth / 8) - 0.50f, highScoreBoardY + 0.04f, 0.075f, Color.YELLOW);
            graphics.drawTextByHeight(highScoreFont, "(10x10)", highScoreBoardX + (highScoreBoardWidth / 2.7f) - 0.50f, highScoreBoardY + 0.04f + 0.075f, 0.05f, Color.YELLOW);
            for (int i = 0; i < 5; i++) {
                if (tenHighScore.size() > i) {
                    graphics.drawTextByHeight(highScoreFont, String.valueOf(tenHighScore.get(i)), highScoreBoardX + (highScoreBoardWidth / 2.5f) - 0.50f, highScoreBoardY + 0.17f + (0.06f * i), 0.06f, Color.YELLOW);
                } else {
                    graphics.drawTextByHeight(highScoreFont, "---", highScoreBoardX + (highScoreBoardWidth / 2.5f) - 0.50f, highScoreBoardY + 0.17f + (0.06f * i), 0.06f, Color.YELLOW);
                }
            }

            graphics.drawTextByHeight(highScoreFont, "High Scores:", highScoreBoardX + (highScoreBoardWidth / 8) - 1.0f, highScoreBoardY + 0.04f, 0.075f, Color.YELLOW);
            graphics.drawTextByHeight(highScoreFont, "(15x15)", highScoreBoardX + (highScoreBoardWidth / 2.7f)- 1.0f, highScoreBoardY + 0.04f + 0.075f, 0.05f, Color.YELLOW);
            for (int i = 0; i < 5; i++) {
                if (fifteenHighScore.size() > i) {
                    graphics.drawTextByHeight(highScoreFont, String.valueOf(fifteenHighScore.get(i)), highScoreBoardX + (highScoreBoardWidth / 2.5f)- 1.0f, highScoreBoardY + 0.17f + (0.06f * i), 0.06f, Color.YELLOW);
                } else {
                    graphics.drawTextByHeight(highScoreFont, "---", highScoreBoardX + (highScoreBoardWidth / 2.5f)- 1.0f, highScoreBoardY + 0.17f + (0.06f * i), 0.06f, Color.YELLOW);
                }
            }

            graphics.drawTextByHeight(highScoreFont, "High Scores:", highScoreBoardX + (highScoreBoardWidth / 8) - 1.5f, highScoreBoardY + 0.04f, 0.075f, Color.YELLOW);
            graphics.drawTextByHeight(highScoreFont, "(20x20)", highScoreBoardX + (highScoreBoardWidth / 2.85f) - 1.5f, highScoreBoardY + 0.04f + 0.075f, 0.05f, Color.YELLOW);
            for (int i = 0; i < 5; i++) {
                if (twentyHighScore.size() > i) {
                    graphics.drawTextByHeight(highScoreFont, String.valueOf(twentyHighScore.get(i)), highScoreBoardX + (highScoreBoardWidth / 2.5f) - 1.5f, highScoreBoardY + 0.17f + (0.06f * i), 0.06f, Color.YELLOW);
                } else {
                    graphics.drawTextByHeight(highScoreFont, "---", highScoreBoardX + (highScoreBoardWidth / 2.5f) - 1.5f, highScoreBoardY + 0.17f + (0.06f * i), 0.06f, Color.YELLOW);
                }
            }


        } else if(renderGame) {
            graphics.drawTextByHeight(font, "Time: " + timeMinutes + ":" + String.format("%02d", timeSeconds), startingMazeX, startingMazeY - 0.08f, 0.1f, Color.YELLOW);

            graphics.drawTextByHeight(font, "Score: " + String.format("%03d", score), startingMazeX + 0.685f, startingMazeY - 0.08f, 0.1f, Color.YELLOW);

            graphics.draw(instructionTex, instructionRect, Color.WHITE);

            graphics.drawTextByWidth(highScoreFont, "Stan's", instructionX + 0.07f, instructionY + 0.07f, instructionWidth - 0.15f, Color.YELLOW);
            graphics.drawTextByHeight(highScoreFont, "(Previously Owned)", instructionX + 0.1f, instructionY + 0.22f, 0.05f, Color.YELLOW);
            graphics.drawTextByWidth(highScoreFont, "Instructions!", instructionX + 0.07f, instructionY + 0.253f, instructionWidth - 0.15f, Color.YELLOW);

            graphics.drawTextByHeight(highScoreFont, "Movement - ", instructionX + 0.07f, instructionY + 0.49f, 0.05f, Color.YELLOW);
            graphics.drawTextByHeight(highScoreFont, "WASD", instructionX + 0.3f, instructionY + 0.45f, 0.05f, Color.YELLOW);
            graphics.drawTextByHeight(highScoreFont, "IJKL", instructionX + 0.3f, instructionY + 0.49f, 0.05f, Color.YELLOW);
            graphics.drawTextByHeight(highScoreFont, "Arrow-Keys", instructionX + 0.25f, instructionY + 0.52f, 0.05f, Color.YELLOW);

            graphics.drawTextByHeight(highScoreFont, "Generate", instructionX + 0.05f, instructionY + 0.59f, 0.05f, Color.YELLOW);
            graphics.drawTextByHeight(highScoreFont, "Maze", instructionX + 0.07f, instructionY + 0.64f, 0.05f, Color.YELLOW);
            graphics.drawTextByHeight(highScoreFont, "-", instructionX + 0.18f, instructionY + 0.62f, 0.05f, Color.YELLOW);
            graphics.drawTextByHeight(highScoreFont, "F1(5x5)", instructionX + 0.23f, instructionY + 0.59f, 0.05f, Color.YELLOW);
            graphics.drawTextByHeight(highScoreFont, "F2(10x10)", instructionX + 0.34f, instructionY + 0.59f, 0.05f, Color.YELLOW);
            graphics.drawTextByHeight(highScoreFont, "F3(15x15)", instructionX + 0.20f, instructionY + 0.64f, 0.05f, Color.YELLOW);
            graphics.drawTextByHeight(highScoreFont, "F4(20x20)", instructionX + 0.33f, instructionY + 0.64f, 0.05f, Color.YELLOW);

            graphics.drawTextByHeight(highScoreFont, "Hints", instructionX + 0.05f, instructionY + 0.72f, 0.05f, Color.YELLOW);
            graphics.drawTextByHeight(highScoreFont, "H", instructionX + 0.07f, instructionY + 0.77f, 0.05f, Color.YELLOW);

            graphics.drawTextByHeight(highScoreFont, "Solution", instructionX + 0.14f, instructionY + 0.72f, 0.05f, Color.YELLOW);
            graphics.drawTextByHeight(highScoreFont, "P", instructionX + 0.18f, instructionY + 0.77f, 0.05f, Color.YELLOW);

            graphics.drawTextByHeight(highScoreFont, "Breadcrumbs", instructionX + 0.28f, instructionY + 0.72f, 0.05f, Color.YELLOW);
            graphics.drawTextByHeight(highScoreFont, "B", instructionX + 0.35f, instructionY + 0.77f, 0.05f, Color.YELLOW);

            graphics.drawTextByHeight(highScoreFont, "High Scores -        F5", instructionX + 0.05f, instructionY + 0.85f, 0.05f, Color.YELLOW);
            graphics.drawTextByHeight(highScoreFont, "  Credits     -        F6", instructionX + 0.05f, instructionY + 0.9f, 0.05f, Color.YELLOW);

            graphics.draw(backgroundTexture, mazeBackground, Color.WHITE);

            graphics.draw(monkeyTex, monkeyRect, Color.WHITE);

            graphics.draw(HighScoreTex, highScoreRect, Color.WHITE);

            graphics.drawTextByHeight(highScoreFont, "High Scores:", highScoreBoardX + (highScoreBoardWidth / 8), highScoreBoardY + 0.04f, 0.075f, Color.YELLOW);
            switch (size) {
                case (5):
                    graphics.drawTextByHeight(highScoreFont, "(5x5)", highScoreBoardX + (highScoreBoardWidth / 2.6f), highScoreBoardY + 0.04f + 0.075f, 0.05f, Color.YELLOW);
                    for (int i = 0; i < 5; i++) {
                        if (fiveHighScore.size() > i) {
                            graphics.drawTextByHeight(highScoreFont, String.valueOf(fiveHighScore.get(i)), highScoreBoardX + (highScoreBoardWidth / 2.5f), highScoreBoardY + 0.17f + (0.06f * i), 0.06f, Color.YELLOW);
                        } else {
                            graphics.drawTextByHeight(highScoreFont, "---", highScoreBoardX + (highScoreBoardWidth / 2.5f), highScoreBoardY + 0.17f + (0.06f * i), 0.06f, Color.YELLOW);
                        }
                    }
                    break;
                case (10):
                    graphics.drawTextByHeight(highScoreFont, "(10x10)", highScoreBoardX + (highScoreBoardWidth / 2.7f), highScoreBoardY + 0.04f + 0.075f, 0.05f, Color.YELLOW);
                    for (int i = 0; i < 5; i++) {
                        if (tenHighScore.size() > i) {
                            graphics.drawTextByHeight(highScoreFont, String.valueOf(tenHighScore.get(i)), highScoreBoardX + (highScoreBoardWidth / 2.5f), highScoreBoardY + 0.17f + (0.06f * i), 0.06f, Color.YELLOW);
                        } else {
                            graphics.drawTextByHeight(highScoreFont, "---", highScoreBoardX + (highScoreBoardWidth / 2.5f), highScoreBoardY + 0.17f + (0.06f * i), 0.06f, Color.YELLOW);
                        }
                    }
                    break;
                case (15):
                    graphics.drawTextByHeight(highScoreFont, "(15x15)", highScoreBoardX + (highScoreBoardWidth / 2.7f), highScoreBoardY + 0.04f + 0.075f, 0.05f, Color.YELLOW);
                    for (int i = 0; i < 5; i++) {
                        if (fifteenHighScore.size() > i) {
                            graphics.drawTextByHeight(highScoreFont, String.valueOf(fifteenHighScore.get(i)), highScoreBoardX + (highScoreBoardWidth / 2.5f), highScoreBoardY + 0.17f + (0.06f * i), 0.06f, Color.YELLOW);
                        } else {
                            graphics.drawTextByHeight(highScoreFont, "---", highScoreBoardX + (highScoreBoardWidth / 2.5f), highScoreBoardY + 0.17f + (0.06f * i), 0.06f, Color.YELLOW);
                        }
                    }
                    break;
                case (20):
                    graphics.drawTextByHeight(highScoreFont, "(20x20)", highScoreBoardX + (highScoreBoardWidth / 2.85f), highScoreBoardY + 0.04f + 0.075f, 0.05f, Color.YELLOW);
                    for (int i = 0; i < 5; i++) {
                        if (twentyHighScore.size() > i) {
                            graphics.drawTextByHeight(highScoreFont, String.valueOf(twentyHighScore.get(i)), highScoreBoardX + (highScoreBoardWidth / 2.5f), highScoreBoardY + 0.17f + (0.06f * i), 0.06f, Color.YELLOW);
                        } else {
                            graphics.drawTextByHeight(highScoreFont, "---", highScoreBoardX + (highScoreBoardWidth / 2.5f), highScoreBoardY + 0.17f + (0.06f * i), 0.06f, Color.YELLOW);
                        }
                    }
                    break;

            }

            if (maze != null) {
                float stepSize = startingMazeWidth / size;
                for (int y = 0; y < size; y++) {
                    for (int x = 0; x < size; x++) {
                        if (!maze[x][y].connected.contains("UP")) {
                            graphics.draw(new Rectangle(startingMazeX + (stepSize * x), startingMazeY + (stepSize * y), stepSize, 0.005f, 1), Color.RED);
                        } else {
                            graphics.draw(new Rectangle(startingMazeX + (stepSize * x), startingMazeY + (stepSize * y), 0.005f, 0.005f, 1), Color.RED);
                        }
                        if (!maze[x][y].connected.contains("LEFT")) {
                            graphics.draw(new Rectangle(startingMazeX + (stepSize * x), startingMazeY + (stepSize * y), 0.005f, stepSize, 1), Color.RED);
                        }
                        //check if shortest path needs to be drawn
                        if (renderShortestPath) {
                            if (shortestPath.contains(maze[x][y]) && !maze[x][y].end) {
                                shortestPathRect = new Rectangle(startingMazeX + (stepSize * x) + (stepSize / 6), startingMazeY + (stepSize * y) + (stepSize / 6), stepSize / 1.5f, stepSize / 1.5f, 0);
                                graphics.draw(shortestPathTex, shortestPathRect, Color.WHITE);
                            }
                        }
                        //check if hint needs to be drawn
                        if (renderHint) {
                            int x_temp;
                            int y_temp;
                            if (!shortestPath.isEmpty()) {
                                x_temp = shortestPath.get(shortestPath.size() - 1).x_location;
                                y_temp = shortestPath.get(shortestPath.size() - 1).y_location;
                            } else {
                                x_temp = -100;
                                y_temp = -100;
                            }
                            if (x == x_temp && y == y_temp) {
                                shortestPathRect = new Rectangle(startingMazeX + (stepSize * x) + (stepSize / 6), startingMazeY + (stepSize * y) + (stepSize / 6), stepSize / 1.5f, stepSize / 1.5f, 0);
                                graphics.draw(shortestPathTex, shortestPathRect, Color.WHITE);
                            }
                        }
                        //check if breadcrumbs need to be drawn
                        if (renderBreadcrumb && maze[x][y].been_visited && (playerX != x || playerY != y)) {
                            breadcrumbRect = new Rectangle(startingMazeX + (stepSize * x) + (stepSize / 6), startingMazeY + (stepSize * y) + (stepSize / 3), stepSize / 1.5f, stepSize / 4, -1);
                            graphics.draw(breadcrumbTex, breadcrumbRect, 0.8f, new Vector2f(breadcrumbRect.left + breadcrumbRect.width / 2, breadcrumbRect.top + breadcrumbRect.height / 2), Color.WHITE);
                            graphics.draw(breadcrumbTex, breadcrumbRect, -0.8f, new Vector2f(breadcrumbRect.left + breadcrumbRect.width / 2, breadcrumbRect.top + breadcrumbRect.height / 2), Color.WHITE);
                        }
                        //check if player needs to be drawn
                        if (x == playerX && y == playerY) {
                            playerRect = new Rectangle(startingMazeX + (stepSize * x) + (stepSize / 6), startingMazeY + (stepSize * y) + (stepSize / 6), stepSize / 1.5f, stepSize / 1.5f, 1);
                            graphics.draw(playerIcon, playerRect, Color.WHITE);
                            maze[x][y].been_visited = true;
                        }
                        //draw end goal
                        if (maze[x][y].end) {
                            endRect = new Rectangle(startingMazeX + (stepSize * x) + (stepSize / 6), startingMazeY + (stepSize * y) + (stepSize / 6), stepSize / 1.5f, stepSize / 1.5f, 0);
                            graphics.draw(endIcon, endRect, Color.WHITE);
                        }
                    }
                    graphics.draw(new Rectangle(startingMazeX + (stepSize * (size)), startingMazeY + (stepSize * y), 0.005f, stepSize, 1), Color.RED);
                }
                graphics.draw(new Rectangle(startingMazeX, startingMazeY - 0.005f + (stepSize * size), startingMazeWidth + 0.005f, 0.005f, 1), Color.RED);
            }
        }
        graphics.end();
    }

    public List<Integer> addScore(List<Integer> topScores, int score) {
        topScores.add(score);  // Add the new score
        Collections.sort(topScores, Collections.reverseOrder());  // Sort descending

        if (topScores.size() > 5) {
            topScores.remove(topScores.size() - 1);  // Keep only top 5
        }
        return topScores;
    }

}
