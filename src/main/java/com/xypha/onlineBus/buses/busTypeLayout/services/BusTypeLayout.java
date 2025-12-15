package com.xypha.onlineBus.buses.busTypeLayout.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BusTypeLayout {

  private static List <String> generateSeatNumbers(int rows, int seatsPerRow){
      List<String> seats = new ArrayList<>();
      for (int r = 0; r<rows; r++){
          char rowLetter = (char) ('A'+r);
          for (int s =1 ; s<= seatsPerRow; s++){
              seats.add(rowLetter+String.valueOf(s));
          }
      }
      return seats;
  }

  private static final Map<String, List<String>> LAYOUTS = new HashMap<>();
  static {
      LAYOUTS.put("STANDARD", generateSeatNumbers(10,4));
      LAYOUTS.put("VIP", generateSeatNumbers(10,3));
      LAYOUTS.put("SLEEPER", generateSeatNumbers(8,2));
      LAYOUTS.put("BUSINESS", generateSeatNumbers(12, 2));
  }

  public static List<String> getSeatNumbersByBusType (String busTypeName){
      return LAYOUTS.getOrDefault(busTypeName.toUpperCase(), new ArrayList<>());
  }



}
