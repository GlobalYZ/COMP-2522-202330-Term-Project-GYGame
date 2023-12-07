package com.example.game;

interface PuzzleGame {

    /**
     * The unique ID for the booster.
     */
    int BOOSTER_ID = 18;
    /**
     * The size of the tile.
     */
    int TILE_SIZE = 40;
    /**
     * The width of the grid.
     */
    int GRID_WIDTH = 10;
    /**
     * The height of the grid.
     */
    int GRID_HEIGHT = 16;

    /**
     * Level up if user reach the designed score.
     */
    void levelUpIfNeed();
    /**
     * Reset the game.
     */
    void resetGame();

    /**
     * Save the game.
     */
    void saveGame();

    /**
     * Load the game.
     */
    void loadGame();



}
