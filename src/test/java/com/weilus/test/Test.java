package com.weilus.test;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;

/**
 * Created by liutq on 2019/3/22.
 */
public class Test {


    public static void main(String[] args) throws IOException {
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**\\zuul-*.*");
        Files.list(Paths.get("D:\\data\\logs"))
                .filter(path-> matcher.matches(path))
                .forEach(System.out::println);
    }
}
