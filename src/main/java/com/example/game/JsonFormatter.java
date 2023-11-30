package com.example.game;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class JsonFormatter implements Serializable {

    public Integer scoreNum = 0;

    public Integer scoreAchieved = 0;

    public Integer comboCount = 0;

    public Integer level = 1;

    public int[][] grid;

    public List<Mino> minos = new ArrayList<>();  // minos on the board
    public Mino selected; // the mino that is going to be moved
    public Mino minoInQueue; // the mino that is going to be selected on the board
    public Mino minoPreview; // the mino that is going to be placed on the board



}
