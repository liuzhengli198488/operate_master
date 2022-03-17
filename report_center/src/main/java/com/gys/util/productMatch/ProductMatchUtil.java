package com.gys.util.productMatch;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class ProductMatchUtil {
    private static char[] itemCharList = new char[] { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '.' };

    public static boolean isMatchSpec(String item1, String item2)
    {
        boolean result = false;
        try
        {
            item1 = item1.toUpperCase();
            item2 = item2.toUpperCase();
            if (item1.equals(item2))
            {
                result = true;
            }
            if (!result)
            {
                item1 = computerMathChar(computerMathChar(item1, 'M', "0.001"), 'K', "1000");
                item2 = computerMathChar(computerMathChar(item2, 'M', "0.001"), 'K', "1000");
                if (item1.equals(item2))
                {
                    result = true;
                }
            }
            if (!result)
            {
                List<String> item1List = Arrays.asList(item1.split("\\*"));
                List<String> item2List = Arrays.asList(item2.split("\\*"));
                if (item1List.size() == item2List.size())
                {
                    boolean isMatchAll = true;
                    for (String it1 : item1List)
                    {
                        boolean isMatch = item2List.contains(it1);
                        if (!isMatch)
                        {
                            isMatchAll = false;
                            break;
                        }
                    }
                    if (isMatchAll)
                    {
                        result = true;
                    }
                }
                else
                {
                    List<String> matchedList = new ArrayList<>();
                    for (String it1 : item1List)
                    {
                        if (item2List.contains(it1))
                        {
                            matchedList.add(it1);
                        }
                    }
                    for (String matched : matchedList)
                    {
                        item1List.remove(matched);
                        item2List.remove(matched);
                    }
                    if (ExecListVal(item1List).equals(ExecListVal(item2List)))
                    {
                        result = true;
                    }
                }
            }
            if (!result)
            {
                item1 = charConvert(item1);
                item2 = charConvert(item2);
                if (item1.equals(item2))
                {
                    result = true;
                }
            }
            return result;
        }
        catch (Exception e)
        {
            log.debug(e.getMessage());
            return result;
        }
    }

    private static String computerMathChar(String item, char c, String replaceString)
    {
        String result = item;
        int iPos = result.indexOf(c);
        if (iPos >= 0)
        {
            String left = result.substring(0, iPos);
            String right = result.substring(iPos + 1);
            String comput = getExecString(left);
            left = left.substring(0, left.length() - comput.length());
            String mix = (new BigDecimal(comput).multiply(new BigDecimal(replaceString))).toString();
            mix = rvZeroAndDot(mix);
            result = left + mix + right;
        }
        return result;
    }

    private static String getExecString(String item)
    {
        String result = item;
        for (int i = item.length() - 1; i >= 0; i--)
        {
            boolean isContain = false;
            char a = item.charAt(i);
            for (char con : itemCharList)
            {
                if (a == con)
                {
                    isContain = true;
                    break;
                }
            }
            if (!isContain)
            {
                result = result.substring(i + 1);
                break;
            }
        }
        return result;
    }

    private static String ExecListVal(List<String> itemList)
    {
        BigDecimal result = BigDecimal.ONE;
        for (String item : itemList)
        {
            result = result.multiply(new BigDecimal(TrimNumberic(item)));
        }
        String strResult = rvZeroAndDot(result.toString());
        return strResult;
    }

    private static String TrimNumberic(String item)
    {
        String result = "";
        for (char a : item.toCharArray())
        {
            for (char con : itemCharList)
            {
                if (a == con)
                {
                    result = result + a;
                    break;
                }
            }

        }
        if (result.equals(""))
        {
            result = "1";
        }
        return result;
    }

    //数字格式化
    private static String rvZeroAndDot(String s)
    {
        if (StringUtils.isEmpty(s))
        {
            return null;
        }
        if (s.indexOf(".") > 0)
        {
            s = s.replaceAll("0+?$", "");//去掉多余的0
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return s;
    }

    // 去除所有中文 保留数字
    public static String charConvert(String str)
    {
        return str.replaceAll("[^0-9*.:]", "");
    }
}
