package com.weilus.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by liutq on 2019/3/22.
 */
public class Test {


    public static void main(String[] args) throws IOException {
        BufferedReader reader = Files.newBufferedReader(Paths.get("D:\\data\\logs\\zuul.log.1"));
        List<String> lines = reader.lines().limit(300).collect(Collectors.toList());
        lines.forEach(System.out::println);
    }
}
