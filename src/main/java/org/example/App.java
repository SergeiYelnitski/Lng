package org.example;

import org.example.service.FileService;

import java.io.IOException;

public class App {

  public static void main(String[] args) throws IOException {

    long start = System.currentTimeMillis();
    FileService fileService = new FileService();
    var list = fileService.readFile("input.txt");
    var lineGroups = fileService.findLineGroups(list);
    fileService.writeGroupsToFile(lineGroups, args[0]);
    long end = System.currentTimeMillis();
    float finalTime = (end - start) / 1000f;
    System.out.printf("Время выполнения = %.3f сек", finalTime);
  }
}
