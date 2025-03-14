import java.util.*;

public class MazeGenerator {
    private final int size;
    private final MazeCell[][] maze;
    private final Random rand = new Random();
    private List<MazeCell> shortestPath;
    private int startX;
    private int startY;
    private int endX;
    private int endY;

    public MazeGenerator(int n) {
        this.size = n;
        this.maze = new MazeCell[this.size][this.size];
        this.shortestPath = new ArrayList<>();
        generateMaze();
    }

    public MazeCell[][] getMaze(){
        return maze;
    }

    public List<MazeCell> getShortestPath(){
        return shortestPath;
    }

    public int getStartX(){
        return startX;
    }

    public int getStartY(){
        return startY;
    }

    private void generateMaze() {
        // Initialize maze with only cells that have 4 walls
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                String[] neighbors = new String[4];

                // Check boundaries before assigning neighbors
                if (y - 1 >= 0) neighbors[0] = x + "," + (y - 1); // Up
                if (y + 1 < size) neighbors[1] = x + "," + (y + 1); // Down
                if (x + 1 < size) neighbors[2] = (x + 1) + "," + y; // Right
                if (x - 1 >= 0) neighbors[3] = (x - 1) + "," + y; // Left

                this.maze[x][y] = new MazeCell(x, y, neighbors);
            }
        }

        // Frontier and visited cells tracking
        ArrayList<MazeCell> frontier = new ArrayList<>();
        HashMap<String, MazeCell> visited = new HashMap<>();

        // Pick a random starting cell
        int startX = rand.nextInt(this.size);
        int startY = rand.nextInt(this.size);

        visited.put(startX + "," + startY, maze[startX][startY]);

        // Add its neighbors to the frontier list
        for (String item : maze[startX][startY].neighbors) {
            if (item != null) {
                String[] xy = item.split(",");
                frontier.add(maze[Integer.parseInt(xy[0])][Integer.parseInt(xy[1])]);
            }
        }

        // Prim's Algorithm: Expand the maze
        while (!frontier.isEmpty()) {
            int randIndex = rand.nextInt(frontier.size());
            MazeCell cellToCheck = frontier.remove(randIndex); // Pick a random frontier cell

            ArrayList<MazeCell> visitedNeighbors = new ArrayList<>();

            // Find visited neighbors
            for (String location : cellToCheck.neighbors) {
                if (location != null && visited.containsKey(location)) {
                    visitedNeighbors.add(visited.get(location));
                }
            }

            // If there is at least one visited neighbor, connect to it
            if (!visitedNeighbors.isEmpty()) {
                MazeCell neighborCell = visitedNeighbors.get(rand.nextInt(visitedNeighbors.size()));

                // Remove walls between the two
                connectCells(cellToCheck, neighborCell);

                visited.put(cellToCheck.x_location + "," + cellToCheck.y_location, cellToCheck);

                // Add new unvisited neighbors to the frontier
                for (String item : cellToCheck.neighbors) {
                    if (item != null && !visited.containsKey(item)) {
                        String[] xy = item.split(",");
                        MazeCell neighbor = maze[Integer.parseInt(xy[0])][Integer.parseInt(xy[1])];

                        if (!frontier.contains(neighbor)) {
                            frontier.add(neighbor);
                        }
                    }
                }
            }
        }
        this.startX = rand.nextInt(size/4);
        this.startY = rand.nextInt(size/4);

        endX = rand.nextInt(size -(size/4), size);
        endY = rand.nextInt(size -(size/4), size);

        maze[this.startX][this.startY].start = true;
        maze[endX][endY].end = true;

        shortestPath = findShortestPath();
        shortestPath.remove(0);
        Collections.reverse(shortestPath);
    }

    public List<MazeCell> findShortestPath() {
        // Find the start and end cells
        MazeCell start = null, end = null;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (maze[x][y].start) {
                    start = maze[x][y];
                }
                if (maze[x][y].end) {
                    end = maze[x][y];
                }
            }
        }

        if (start == null || end == null) {
            System.out.println("Start or end not set.");
            return new ArrayList<>();
        }

        // BFS queue and visited set
        Queue<MazeCell> queue = new LinkedList<>();
        Map<MazeCell, MazeCell> cameFrom = new HashMap<>();

        queue.add(start);
        cameFrom.put(start, null); // Start has no predecessor

        while (!queue.isEmpty()) {
            MazeCell current = queue.poll();

            // If we reached the end, reconstruct the path
            if (current == end) {
                return reconstructPath(cameFrom, end);
            }

            // Check all possible connected neighbors
            for (String direction : current.connected) {
                MazeCell neighbor = getNeighbor(current, direction);
                if (neighbor != null && !cameFrom.containsKey(neighbor)) {
                    queue.add(neighbor);
                    cameFrom.put(neighbor, current);
                }
            }
        }

        System.out.println("No path found.");
        return new ArrayList<>(); // No path found
    }

    private List<MazeCell> reconstructPath(Map<MazeCell, MazeCell> cameFrom, MazeCell end) {
        List<MazeCell> path = new ArrayList<>();
        for (MazeCell at = end; at != null; at = cameFrom.get(at)) {
            path.add(at);
        }
        Collections.reverse(path); // Reverse the path to go from start to end
        return path;
    }
    private MazeCell getNeighbor(MazeCell cell, String direction) {
        int x = cell.x_location;
        int y = cell.y_location;

        switch (direction) {
            case "UP":
                return y > 0 ? maze[x][y - 1] : null;
            case "DOWN":
                return y < size - 1 ? maze[x][y + 1] : null;
            case "LEFT":
                return x > 0 ? maze[x - 1][y] : null;
            case "RIGHT":
                return x < size - 1 ? maze[x + 1][y] : null;
        }
        return null;
    }

    // Removes the wall between two connected cells
    private void connectCells(MazeCell cell1, MazeCell cell2) {
        int xDiff = cell1.x_location - cell2.x_location;
        int yDiff = cell1.y_location - cell2.y_location;

        if (xDiff == 1) { // Cell1 is to the right of Cell2
            if (!cell1.connected.contains("LEFT")) cell1.connected.add("LEFT");
            if (!cell2.connected.contains("RIGHT")) cell2.connected.add("RIGHT");
        } else if (xDiff == -1) { // Cell1 is to the left of Cell2
            if (!cell1.connected.contains("RIGHT")) cell1.connected.add("RIGHT");
            if (!cell2.connected.contains("LEFT")) cell2.connected.add("LEFT");
        } else if (yDiff == 1) { // Cell1 is below Cell2
            if (!cell1.connected.contains("UP")) cell1.connected.add("UP");
            if (!cell2.connected.contains("DOWN")) cell2.connected.add("DOWN");
        } else if (yDiff == -1) { // Cell1 is above Cell2
            if (!cell1.connected.contains("DOWN")) cell1.connected.add("DOWN");
            if (!cell2.connected.contains("UP")) cell2.connected.add("UP");
        }
    }


    public void printMaze() {
        for (int y = 0; y < size; y++) {
            // Print top walls of each row
            for (int x = 0; x < size; x++) {
                System.out.print("+"); // Corner of each cell
                if (maze[x][y].connected.contains("UP")) {
                    System.out.print("   "); // No wall (open path)
                } else {
                    System.out.print("---"); // Wall
                }
            }
            System.out.println("+"); // End of top border

            // Print left wall and cell spaces
            for (int x = 0; x < size; x++) {
                if (maze[x][y].connected.contains("LEFT")) {
                    if(maze[x][y].start){
                        System.out.print("  S ");
                    }else if(maze[x][y].end){
                        System.out.print("  E ");
                    }else if(shortestPath.contains(maze[x][y])) {
                        System.out.print("  P ");
                    }else{
                        System.out.print("    "); // No wall (open path)
                    }
                } else {
                    if(maze[x][y].start){
                        System.out.print("| S ");
                    }else if(maze[x][y].end){
                        System.out.print("| E ");
                    }else if(shortestPath.contains(maze[x][y])) {
                        System.out.print("| P ");
                    }else {
                        System.out.print("|   "); // Wall
                    }
                }
            }
            System.out.println("|"); // Rightmost wall
        }

        // Print bottom border
        for (int x = 0; x < size; x++) {
            System.out.print("+---");
        }
        System.out.println("+");
    }


    public class MazeCell {
        public boolean been_visited = false;
        public boolean start = false;
        public boolean end = false;
        public int x_location;
        public int y_location;
        public String[] neighbors;
        public ArrayList<String> connected = new ArrayList<>();

        public MazeCell(int X, int Y, String[] Neighbors) {
            this.x_location = X;
            this.y_location = Y;
            this.neighbors = Neighbors;
        }

    }

    public static void main(String[] args) {
        MazeGenerator mazeGen = new MazeGenerator(15); // Generate a 15x15 maze
        mazeGen.printMaze();
    }
}