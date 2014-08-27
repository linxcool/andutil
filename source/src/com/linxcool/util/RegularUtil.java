package com.linxcool.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegularUtil {

	/**
	 * 邮箱校验
	 * @param email
	 * @return
	 */
	public static boolean validateEmail(String email){
		String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";  
		Pattern regex = Pattern.compile(check);  
		Matcher matcher = regex.matcher(email);  
		return matcher.matches(); 
	}

	/**
	 * QQ号码校验
	 * @param qq
	 * @return
	 */
	public static boolean validateQQ(String qq){
		String check = "^[0-9]{5,12}$";  
		Pattern regex = Pattern.compile(check);  
		Matcher matcher = regex.matcher(qq);  
		return matcher.matches(); 
	}
	
	/**
	 * 手机号码校验
	 * @param mobile
	 * @return
	 */
	public static boolean validateMobile(String mobile){
		String check = "^((13[0-9])|(15[^4,\\D])|(14[0-9])|(18[0-9]))\\d{8}$";  
		Pattern regex = Pattern.compile(check);  
		Matcher matcher = regex.matcher(mobile);  
		return matcher.matches(); 
	}
}
