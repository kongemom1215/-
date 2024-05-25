package com.unity.potato.util;

import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
public class StringUtil {
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9가-힣]*$");
    private static final String RGB_PATTERN = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";
    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]*>");

    public static boolean isEmailType(String input){
        if (input == null || input.isEmpty()) {
            return false;
        }

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

    public static boolean isNicknameType(String nickname) {
        if (nickname == null || nickname.isEmpty()) {
            return false;
        }

        if (nickname.length() < 2 || nickname.length() > 10) {
            return false;
        }

        if (!NICKNAME_PATTERN.matcher(nickname).matches()) {
            return false;
        }

        return true;
    }

    public static boolean isHashtagType(String tag) {
        if (tag == null || tag.isEmpty()) {
            return false;
        }

        if (tag.length() > 15) {
            return false;
        }

        if (!NICKNAME_PATTERN.matcher(tag).matches()) {
            return false;
        }

        return true;
    }

    public static String generateUuid(){
        String uuid = UUID.randomUUID().toString();

        return uuid;
    }

    public static boolean isValidDateAndAge(String dateOfBirth) {
        // 생년월일이 유효한지 확인 (YYYYMMDD 형식)
        if (!isValidDateFormat(dateOfBirth)) {
            return false;
        }

        // 14살 이상인지 확인
        if (!isOver14YearsOld(dateOfBirth)) {
            return false;
        }

        return true;
    }

    private static boolean isValidDateFormat(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        sdf.setLenient(false);
        try {
            sdf.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private static boolean isOver14YearsOld(String dateOfBirth) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate birthDate = LocalDate.parse(dateOfBirth, formatter);
            LocalDate date14YearsAgo = birthDate.plusYears(14);
            LocalDate currentDate = LocalDate.now();
            return date14YearsAgo.isBefore(currentDate);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isValidPassword(String password, String email) {
        // 1. 비밀번호가 8글자~16글자인지 확인
        if (password.length() < 8 || password.length() > 16) {
            System.out.println("비밀번호는 8글자에서 16글자 사이여야 합니다.");
            return false;
        }

        // 2. 비밀번호가 이메일 주소를 포함하는지 확인
        if (email != null && password.contains(email.split("@")[0])) {
            System.out.println("비밀번호에 이메일 주소를 포함할 수 없습니다.");
            return false;
        }

        // 3. 비밀번호가 영문 대/소문자, 숫자, 기호 중 2개 이상 조합되어 있는지 확인
        int complexityCount = 0;
        if (password.matches(".*[a-z].*")) complexityCount++;
        if (password.matches(".*[A-Z].*")) complexityCount++;
        if (password.matches(".*\\d.*")) complexityCount++;
        if (password.matches(".*[~!@#$%^&*()_+\\-={}\\[\\]:;\"'<>,.?/\\\\].*")) complexityCount++;

        if (complexityCount < 2) { //비밀번호는 영문 대/소문자, 숫자, 특수기호 중 2개 이상을 조합해야 합니다.
            return false;
        }

        return true;
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static String LocalDateTimeToString(LocalDateTime date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        String formattedDate = date.format(formatter);

        return formattedDate;
    }

    public static boolean isNumberic(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return str.chars().allMatch(Character::isDigit);
    }

    public static String createSaveFileName(String extName) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        String formattedDateTime = now.format(formatter);
        String fileName = formattedDateTime + "." + extName;

        return fileName;
    }

    public static String truncateString(String input, int maxLength) {
        if (input == null || input.length() <= maxLength) {
            return input;
        } else {
            return input.substring(0, maxLength - 3) + "...";
        }
    }

    public static boolean isValidRgbCode(String code) {
        Pattern pattern = Pattern.compile(RGB_PATTERN);
        Matcher matcher = pattern.matcher(code);
        return matcher.matches();
    }

    public static String truncate(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + "...";
    }

    public static String removeHtml(String input) {
        return HTML_TAG_PATTERN.matcher(input).replaceAll("");
    }
}

