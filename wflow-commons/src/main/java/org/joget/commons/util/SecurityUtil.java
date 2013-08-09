package org.joget.commons.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service("securityUtil")
public class SecurityUtil implements ApplicationContextAware {

    public final static String ENVELOPE = "%%%%";
    private static ApplicationContext appContext;
    private static DataEncryption de;

    public static ApplicationContext getApplicationContext() {
        return appContext;
    }

    public void setApplicationContext(ApplicationContext context) throws BeansException {
        appContext = context;
    }

    public static DataEncryption getDataEncryption() {
        if (de == null) {
            try {
                de = (DataEncryption) getApplicationContext().getBean("dataEncryption");
            } catch (Exception e) {
            }
        }
        return de;
    }

    public static String encrypt(String rawContent) {

        if (rawContent != null && !rawContent.isEmpty() && getDataEncryption() != null) {
            return ENVELOPE + getDataEncryption().encrypt(rawContent) + ENVELOPE;
        }
        return rawContent;
    }

    public static String decrypt(String protectedContent) {
        if (protectedContent.startsWith(ENVELOPE) && protectedContent.endsWith(ENVELOPE) && getDataEncryption() != null) {
            protectedContent = cleanPrefixPostfix(protectedContent);
            return getDataEncryption().decrypt(protectedContent);
        }
        return protectedContent;
    }

    public static String computeHash(String rawContent, String randomSalt) {

        if (rawContent != null && !rawContent.isEmpty()) {
            if (getDataEncryption() != null) {
                return ENVELOPE + getDataEncryption().computeHash(rawContent, randomSalt) + ENVELOPE;
            } else {
                return StringUtil.md5Base16(rawContent);
            }
        }
        return rawContent;
    }

    public static Boolean verifyHash(String hash, String randomSalt, String rawContent) {
        if (hash != null && !hash.isEmpty() && rawContent != null && !rawContent.isEmpty()) {
            if (hash.startsWith(ENVELOPE) && hash.endsWith(ENVELOPE) && getDataEncryption() != null) {
                return getDataEncryption().verifyHash(hash, randomSalt, rawContent);
            } else {
                return hash.equals(StringUtil.md5Base16(rawContent));
            }
        }
        return false;
    }

    public static String generateRandomSalt() {
        if (getDataEncryption() != null) {
            getDataEncryption().generateRandomSalt();
        }
        return "";
    }

    protected static String cleanPrefixPostfix(String content) {
        content = content.replaceAll(ENVELOPE, "");

        return content;
    }
}