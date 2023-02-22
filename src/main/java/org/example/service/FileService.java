package org.example.service;

import org.example.model.NewLineElement;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

public class FileService {

  public FileService() {
  }

  public List<String> readFile(String fileName) {
    Set<String> uniqueLines = new HashSet<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
      String line;
      while ((line = reader.readLine()) != null) {
        uniqueLines.add(line);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return new ArrayList<>(uniqueLines);
  }

  public List<List<String>> findLineGroups(List<String> lines) {

    if (lines == null)
      return Collections.emptyList();

    List<List<String>> linesGroups = new ArrayList<>();
    if (lines.size() < 2) {
      linesGroups.add(lines);
      return linesGroups;
    }

    List<Map<String, Integer>> columns = new ArrayList<>();
    Map<Integer, Integer> unitedGroups = new HashMap<>();

    lines.forEach(line -> {
      String[] lineElements = line.split(";");
      HashSet<Integer> groupsWithSameElems = new HashSet<>();
      LinkedList<NewLineElement> newElements = new LinkedList<>();

      IntStream.range(0, lineElements.length).forEach(elmIndex -> {
        String currLnElem = lineElements[elmIndex];
        if (columns.size() == elmIndex)
          columns.add(new HashMap<>());
        if ("".equals(currLnElem.replaceAll("\"", "").trim()))
          return;

        Map<String, Integer> currCol = columns.get(elmIndex);
        Integer elemGrNum = currCol.get(currLnElem);
        if (elemGrNum != null) {
          while (unitedGroups.containsKey(elemGrNum))
            elemGrNum = unitedGroups.get(elemGrNum);
          groupsWithSameElems.add(elemGrNum);
        } else {
          newElements.add(new NewLineElement(currLnElem, elmIndex));
        }
      });

      int groupNumber;
      if (groupsWithSameElems.isEmpty()) {
        linesGroups.add(new ArrayList<>());
        groupNumber = linesGroups.size() - 1;
      } else {
        groupNumber = groupsWithSameElems.iterator().next();
      }

      newElements.forEach(newLineElement ->
          columns.get(newLineElement.columnNum())
              .put(newLineElement.lineElement(), groupNumber)
      );

      groupsWithSameElems.stream()
          .filter(matchedGrNum -> matchedGrNum != groupNumber)
          .forEach(matchedGrNum -> {
            unitedGroups.put(matchedGrNum, groupNumber);
            linesGroups.get(groupNumber).addAll(linesGroups.get(matchedGrNum));
            linesGroups.set(matchedGrNum, null);
          });

      if (linesGroups.get(groupNumber) == null) {
        linesGroups.set(groupNumber, new ArrayList<>());
      }
      linesGroups.get(groupNumber).add(line);
    });

    linesGroups.removeAll(Collections.singleton(null));
    return linesGroups;
  }

  public void writeGroupsToFile(List<List<String>> groups, String outputFile)
      throws IOException {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
      int groupCount = 0;
      for (List<String> group : groups) {
        if (group.size() > 1) {
          groupCount++;
          writer.write(String.format("Группа %d%n", groupCount));
          for (String string : group) {
            writer.write(String.join(";", string) + "\n");
          }
        }
      }
      writer.write(String.format("Число групп с более чем одним элементом: %d", groupCount));
    }
  }
}
