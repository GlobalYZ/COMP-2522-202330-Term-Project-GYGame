package com.example.game;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class JsonFormatter implements Serializable {

    public Integer scoreNum = 0;

    public Integer comboCount = 0;

    public Integer level = 1;

    public int[][] grid;

    public Integer scoreAchieved = 0;

    public List<Mino> minos = new ArrayList<>();  // minos on the board
    public Mino selected; // the mino that is going to be moved
    public Mino minoInQueue; // the mino that is going to be selected on the board
    public Mino minoPreview; // the mino that is going to be placed on the board



}
