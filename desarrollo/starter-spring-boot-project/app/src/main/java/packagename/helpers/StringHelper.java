package packagename.helpers;

import java.util.Base64;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.lang3.StringUtils;


public class StringHelper
{
    public static String getRandomString(Integer length, Boolean lowerLetters, Boolean upperLetters, Boolean integers, Boolean symbols)
    {
        String dictionary = "";
        String out = "";
        
        if(lowerLetters)
        {
            dictionary += "abcdefghijklmnopqrstuvwxyz";
        }
        
        if(upperLetters)
        {
            dictionary += "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        }
        
        if(integers)
        {
            dictionary += "0123456789";
        }
        
        if(symbols)
        {
            dictionary += "'/+-_.,;@()=?!&%${}[]";
        }
        
        Random r = new Random();
        for(int itercount = 0; itercount <= length; itercount++)
        {
            int index = r.nextInt(dictionary.length() - 1);
            out += dictionary.substring(index, index);
        }
        
        return out;
    }
    
    public static Integer toInteger(String value)
    {
        if((value != null) && (!value.isEmpty()) && StringUtils.isNumeric(value.trim()))
        {
            try
            {
                return Integer.parseInt(value.trim());
            }
            catch(Exception ignored)
            {
                return 0;
            }
        }
        else
        {
            return 0;           
        }
    }
    
    public static Long toLong(String value)
    {
        if((value != null) && (!value.isEmpty()) && StringUtils.isNumeric(value.trim()))
        {
            try
            {
                return Long.parseLong(value.trim());
            }
            catch(Exception ignored)
            {
                return 0L;
            }
        }
        else
        {
            return 0L;
        }
    }
    
    public static String base64_encode(String string) 
    {
        try 
        {
            byte[] data = string.getBytes("UTF-8");
            return Base64.getEncoder().encodeToString(data).trim();
        }
        catch (Exception e) 
        {
            return "Unsupported encoding Ascii to UTF-8";
        }
    }

    public static String base64ToAscii(String buffer)
    {
        buffer = buffer.trim();
        try 
        {
            byte[] data = buffer.getBytes("UTF-8");
            return Base64.getEncoder().encodeToString(data).trim();
        }
        catch (Exception e) 
        {
            return "The Base64 hash is corrupt";
        }
    }

    public static String MD5(String str)
    {
        return digestToStr(str, "md5");
    }

    public static String SHA1(String str)
    {
        return digestToStr(str, "sha-1");
    }

    public static String SHA256(String str)
    {
        return digestToStr(str, "sha-256");
    }

    public static String SHA512(String str)
    {
        return digestToStr(str, "sha-512");
    }

    public static String RC4(String str, String key)
    {
        return toRC4(str, key);
    }

    public static String strDec(String str)
    {
        return toUnicodePrefix(str, "", " ");
    }

    public static String decToStr(String buffer)
    {
        buffer = buffer
                .replace(",", " ")
                .replace(".", " ")
                .replace("/", " ")
                .replace("-", " ")
                .replace("\n", " ")
                .replace("\r", " ")
                .replace("\t", " ")
                .replaceAll("[^0-9 ]", "")
                .replaceAll("\\s+", " ");

        String str = "";
        String[] parts = buffer.split(" ");
        buffer = null; // Empty memory

        for(String part : parts)
        {
            try 
            {
                int primitive_part = toInteger(part);
                char character = (char) primitive_part;
                str += character;
            }
            catch(Exception e)
            {
                str += "?";
            }
        }

        return str;
    }

    public static String strToOctal(String str)
    {
        return toOctal(str, "", " ");
    }

    public static String strHex(String str)
    {
        return strToHexPrefix(str, "\\x", "");
    }

    public static String urlencode(String str)
    {
        return strToHexPrefix(str, "%", "");
    }

    public static String strToHtmlentities(String str)
    {
        return toUnicodePrefix(str, "&#", "");
    }

    public static String strToSqliDword(String str)
    {
        return "function(0x" + strToHexPrefix(str, "", "") + ")";
    }

    public static String strToBinary(String str)
    {
        return toBinary(str, "", " ");
    }

    public static String checkMD5Sum(String filename)
    {
        return checkSum(filename, "md5");
    }

    public static String checkSHA1Sum(String filename)
    {
        return checkSum(filename, "sha-1");
    }

    private static String checkSum(String filename, String algorithm)
    {
        try
        {
            byte[] bytes = null;

            /* Read binary file */
            File fileHandle = new File(filename);
            byte[] buffer = new byte[(int)fileHandle.length()];
            FileInputStream fis = new FileInputStream(fileHandle);
            fis.read(buffer);
            fis.close();

            /* Convert */
            MessageDigest messageDigest;
            messageDigest = MessageDigest.getInstance(algorithm.toUpperCase());
            messageDigest.reset();
            messageDigest.update(buffer);
            bytes = messageDigest.digest();
            return byteToHexadecimal(bytes);

        }
        catch(FileNotFoundException e)
        {
            return "Error: " + e.getMessage().toString();
        }
        catch (IOException e)
        {
            return "Error: " + e.getMessage().toString();
        }
        catch (NoSuchAlgorithmException e) 
        {
            return "Error: " + e.getMessage().toString();
        }
    }

    private static String toRC4(String buff, String key)
    {
        if(key.isEmpty())
        {
            return "Invalid key length";
        }

        try 
        {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes(), "AES"));
            return "0x" + byteToHexadecimal(cipher.doFinal(buff.getBytes()));

        }
        catch (NoSuchAlgorithmException e) 
        {
            return "Error: " + e.getMessage().toString();
        }
        catch (NoSuchPaddingException e) 
        {
            return "Error: " + e.getMessage().toString();
        }
        catch (InvalidKeyException e) 
        {
            return "Error: " + e.getMessage().toString();
        }
        catch (IllegalBlockSizeException e) 
        {
            return "Error: " + e.getMessage().toString();
        }
        catch (BadPaddingException e) 
        {
            return "Error: " + e.getMessage().toString();
        }
    }

    private static String byteToHexadecimal(byte[] bt) 
    {
        String hash = "";
        for (byte aux : bt) 
        {
            int b = aux & 0xff;
            if (Integer.toHexString(b).length() == 1)
            {
                hash += "0";
            }
            hash += Integer.toHexString(b);
        }
        return hash;
    }

    private static String digestToStr(String str, String algorithm)
    {
        try
        {
            byte[] bytes = null;
            byte[] buffer = str.getBytes();
            MessageDigest messageDigest;
            messageDigest = MessageDigest.getInstance(algorithm.toUpperCase());
            messageDigest.reset();
            messageDigest.update(buffer);
            bytes = messageDigest.digest();
            return byteToHexadecimal(bytes);
        }
        catch (NoSuchAlgorithmException e)
        {
            return "Error: " + e.getMessage().toString();
        }
    }

    public static String strToHexPrefix(String buff, String prefix, String sufix)
    {
        byte[] bt = buff.getBytes();
        String hash = "";
        for (byte aux : bt) 
        {
            int b = aux & 0xff;
            if (Integer.toHexString(b).length() == 1)
            {
                hash += "0";                
            }
            hash += prefix + Integer.toHexString(b) + sufix;
        }
        return hash;
    }

    public static String hexToStr(String buffer)
    {
        buffer = buffer.trim().toLowerCase();

        if(buffer.startsWith("0x"))
        {
            buffer = buffer.substring(2);
        }

        buffer = buffer.replaceAll("[^0-9a-f]", "");

        StringBuilder sb = new StringBuilder();
        char[] hexData = buffer.toCharArray();
        for (int count = 0; count < hexData.length - 1; count += 2) 
        {
            int firstDigit = Character.digit(hexData[count], 16);
            int lastDigit = Character.digit(hexData[count + 1], 16);
            int decimal = firstDigit * 16 + lastDigit;
            sb.append((char)decimal);
        }
        return sb.toString();
    }

    private static String toUnicodePrefix(String str, String prefix, String sufix)
    {
        StringBuilder sb = new StringBuilder();
        byte[] writeBuf  = str.getBytes();
        for (int i =0; i < writeBuf.length; ++i)
        {
            sb.append(prefix + writeBuf[i] + sufix);
        }
        return sb.toString();
    }

    private static String toOctal(String str, String prefix, String sufix)
    {
        StringBuilder sb = new StringBuilder();
        byte[] writeBuf  = str.getBytes();

        for (int i =0; i < writeBuf.length; ++i)
        {
            sb.append(prefix + Integer.toOctalString(writeBuf[i]) + sufix);
        }

        return sb.toString();
    }

    public static String octalToStr(String buffer)
    {
        buffer = buffer
                .replace(",", " ")
                .replace(".", " ")
                .replace("/", " ")
                .replace("-", " ")
                .replace("\n", " ")
                .replace("\r", " ")
                .replace("\t", " ")
                .replaceAll("[^0-9 ]", "")
                .replaceAll("\\s+", " ");

        String str = "";
        String[] parts = buffer.split(" ");
        buffer = null; // Empty memory

        for(String part : parts)
        {
            try 
            {
                int primitive_part = toInteger(part);

                int ascii = Integer.parseInt(Integer.toString(primitive_part), 8);
                str += "" + (char)ascii;

            }
            catch(Exception e)
            {
                str += "?";
            }
        }

        return str;
    }

    public static String toBinary(String str, String prefix, String sufix)
    {
        byte[] bytes = str.getBytes();
        StringBuilder binary = new StringBuilder();
        for (byte b : bytes)
        {
            int val = b;
            binary.append(prefix);
            for (int i = 0; i < 8; i++)
            {
                binary.append((val & 128) == 0 ? 0 : 1);
                val <<= 1;
            }
            binary.append(sufix);
        }
        return binary.toString();
    }

    public static String binaryToAscii(String buffer)
    {
        try 
        {
            buffer = buffer.trim().replaceAll("[^0-1]", "");

            String result = "";
            char nextChar;
            for (int i = 0; i <= buffer.length() - 8; i += 8) 
            { //this is a little tricky.  we want [0, 7], [9, 16], etc
                nextChar = (char) Integer.parseInt(buffer.substring(i, i + 8), 2);
                result += nextChar;
            }
            return result;
        }
        catch(Exception e)
        {
            return "Incorrect format binary";
        }
    }
    
    public static String getRandomSha1()
    {
        return SHA1((new Timestamp(System.currentTimeMillis())).toString() + ThreadLocalRandom.current().nextInt(10, 1000000 + 1));
    }
}
