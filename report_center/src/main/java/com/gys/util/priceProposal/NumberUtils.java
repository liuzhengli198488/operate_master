package com.gys.util.priceProposal;

import java.util.ArrayList;
import java.util.List;

public class NumberUtils {

    public static void main(String[] args) {
//        System.out.println(getArr(10,50,2));
        System.out.println(getArrIndex(getArr(10,50,5),11));
    }

    public static int getArrIndex(List<String> list, double num){
        for (int i = 0; i < list.size(); i++) {
            String[] split = list.get(i).split("-");
            double a = Double.parseDouble(split[0]);
            double b = Double.parseDouble(split[1]);
            if(num > a && num <= b){
                return i;
            }
        }
        return -1;
    }

    public static List<String> getArr(int start, int end, int num) {

        List<Integer> intList = new ArrayList<>();
        for (int i = start; i <= end; i += num) {
            intList.add(i);
        }

        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < (intList.size() - 1); i++) {
            strings.add(intList.get(i) +"-"+ intList.get(i+1));
        }

        return strings;
    }
}