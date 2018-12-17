/*
 * version date author 
 * ────────────────────────────────── 
 * 1.0  May 23, 2012 Neal Miao 
 * 
 * Copyright(c) 2012, by HTWater. All Rights Reserved.
 */
package cn.miao.framework.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * 拼音工具类
 * 
 * @author Neal Miao
 * @version
 * @since v 1.0
 * @Date May 23, 2012 2:26:32 PM
 * 
 * @see
 */
public class PinyinUtil {

	/**
	 * 中文转拼音
	 * 
	 * @param chinese
	 * @return String
	 * @since v 1.0
	 */
	public static String converterToPinYin(String chinese) {
		String pinyinString = "";
		String firstCode = "";
		//chinese = chinese.replaceAll("(", "").replaceAll(")", "");
		char[] inChs = chinese.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		defaultFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
		try {
			for (char inCh : inChs) {
				String[] pinyinArray;
				if (!isChinese(inCh)) {
					pinyinString += inCh;
					firstCode += inCh;
					continue;
				}
				pinyinArray = PinyinHelper.toHanyuPinyinStringArray(inCh,
						defaultFormat);
				if (null == pinyinArray) {
					continue;
				}
				pinyinString += pinyinArray[0];
				firstCode += pinyinArray[0].substring(0, 1);
			}
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			return firstCode + "-" + pinyinString;
		}
		return firstCode + "-" + pinyinString;
	}

	/**
	 * 判断是否是中文字符
	 * 
	 * @param c
	 * @return boolean
	 * @since v 1.0
	 */
	public static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
		|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
		|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
		|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
		|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
		|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		System.out.println(converterToPinYin("中国中文绿"));
	}
}
