package com.weilus.test;

import java.io.IOException;

/**
 * Created by liutq on 2019/3/22.
 */
public class Test {


    public static void main(String[] args) throws IOException {
//        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**\\zuul-*.*");
//        Files.list(Paths.get("D:\\data\\logs"))
//                .filter(path-> matcher.matches(path))
//                .forEach(System.out::println);


        String line = "\tat org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:70) [spring-test-4.3.8.RELEASE.jar:4.3.8.RELEASE]";
    }
}
