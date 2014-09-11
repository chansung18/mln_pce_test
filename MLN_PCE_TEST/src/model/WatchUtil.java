package model;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.StringTokenizer;
import java.util.Vector;

//import com.etri.pce.SMM.SystemManager;

public class WatchUtil
{

    public WatchUtil()
    {
    }

    public static boolean isDNSEnabled()
    {
        return enableDNS;
    }

    public static void setDNSEnabled(boolean flag)
    {
        enableDNS = flag;
    }

    public static String getDefaultNetMask(String s)
    {
        if(s == null || s.indexOf(".") == -1)
        {
        	System.out.println("Get Network Mask: Invalid network argument. " + s);
            return null;
        }
        int ai[] = getAddrArray(s);
        if(ai == null)
            System.out.println("Get Network Mask: Invalid network argument. " + s);
        if(ai[0] < 128)
            return "255.0.0.0";
        if(ai[0] < 192)
            return "255.255.0.0";
        else
            return "255.255.255.0";
    }

    public static long getNumIPs(String s, String s1)
    {
        long l = getAddrLong("255.255.255.255");
        long l1 = getAddrLong(s1);
        if(l1 == l)
            return 0L;
        long l2 = getAddrLong(s);
        long l3 = l ^ l1;
        if(l1 == 0L || l2 == 0L || l1 > l || l3 > l)
        {
            System.out.println("Invalid network address or netmask.  Net: " + s + " " + "Mask: " + s1);
            return 0L;
        } else
        {
            return l3;
        }
    }

    public static String getOneIpFromThisNet(String s, String s1)
    {
        long l = getAddrLong(s);
        return convertAddr(l + 1L);
    }

    public static String[] getRange(String s, String s1, String s2, String s3)
    {
        long l = getAddrLong(s2);
        long l1 = getAddrLong(s3);
        Vector vector = new Vector();
        int i = (int)((l1 - l) + 1L);
        for(long l2 = l; l2 <= l1; l2++)
        {
            if(!inNet(l2, s, s1))
                break;
            vector.addElement(convertAddr(l2));
        }

        String as[] = new String[vector.size()];
        for(int j = 0; j < as.length; j++)
            as[j] = (String)vector.elementAt(j);

        return as;
    }

    public static String[] getIPList(String s, String s1)
    {
        String as[] = null;
        long l = getAddrLong("255.255.255.255");
        long l1 = getAddrLong(s1);
        if(l1 == l)
        {
            as = new String[1];
            as[0] = s;
            return as;
        }
        long l2 = getAddrLong(s);
        long l3 = l ^ l1;
        if(l1 == 0L || l2 == 0L || l1 > l || l3 > l)
        {
            System.out.println("Invalid network address or netmask.  Net: " + s + " " + "Mask: " + s1);
            return null;
        }
        if(l3 > 0x10000L)
        {
            System.out.println("Pointless searching net of this size.  Net: " + s + " " + "Mask: " + s1);
            l3 = 65025L;
        }
        if(l3 == 2L)
        {
            as = new String[1];
            as[0] = convertAddr(l2 + 1L);
            return as;
        }
        if((l2 + l3) - 1L > l)
        {
            System.out.println("Invalid network address or netmask.  Net: " + s + " " + "Mask: " + s1);
            return null;
        }
        as = new String[(int)l3 - 1];
        for(int i = 0; (long)i < l3 - 1L; i++)
            as[i] = convertAddr(l2 + 1L + (long)i);

        return as;
    }

    public static String getNetAddr(String s, String s1)
    {
        if(s.indexOf(":") != -1)
            return getIPV6NetAddr(s, s1);
        if(s != null && s1 != null)
        {
            if(s.equals(s1))
                s1 = "255.255.255.0";
            if(s1.equals("255.255.255.255"))
                s1 = "255.255.255.0";
            if(s1.equals("0.0.0.0"))
                s1 = "255.255.255.0";
        }
        long l = getAddrLong(s1);
        long l1 = getAddrLong(s);
        long l2 = l1 & l;
        return convertAddr(l2);
    }

    public static String convertAddr(long l)
    {
        int ai[] = new int[4];
        for(int i = 0; i < 4; i++)
            ai[i] = (int)(l >> 8 * (3 - i) & 255L);

        return new String(ai[0] + "." + ai[1] + "." + ai[2] + "." + ai[3]);
    }

    public static long getAddrLong(String s)
    {
        int ai[] = getAddrArray(s);
        if(ai == null)
        {
//        	SystemManager.otllogger.out.setAdditivity(true);
//            SystemManager.otllogger.out.debug("getAddrLong()   Invalid network address argument. " + s);
//            SystemManager.otllogger.out.setAdditivity(false);
            return 0L;
        }
        long l = 0L;
        for(int i = 0; i < 4; i++)
            l |= (long)ai[i] << 8 * (3 - i);

        return l;
    }

    public static int[] getAddrArray(String s)
    {
        if(s == null)
            return null;
        if(s.indexOf(":") != -1)
            return getIPV6AddrArray(s);
        StringTokenizer stringtokenizer = new StringTokenizer(s, ".");
        if(stringtokenizer.countTokens() != 4)
            return null;
        int ai[] = new int[4];
        try
        {
            for(int i = 0; i < 4; i++)
                ai[i] = Integer.parseInt(stringtokenizer.nextToken());

        }
        catch(NumberFormatException numberformatexception)
        {
            return null;
        }
        for(int j = 0; j < 4; j++)
            if(ai[j] < 0 || ai[j] > 255)
                return null;

        return ai;
    }

    public static String getDNSName(String s)
    {
        if(!enableDNS)
            return s;
        String s1;
        try
        {
            if(System.getProperty("os.name").startsWith("Linux"))
            {
                InetAddress inetaddress = InetAddress.getByName(s);
                s1 = inetaddress.getHostName().trim();
            } else
            {
                InetAddress inetaddress1 = InetAddress.getByName(s);
                s1 = inetaddress1.getHostName().trim();
            }
        }
        catch(UnknownHostException unknownhostexception)
        {
            s1 = s;
        }
        return s1.toLowerCase();
    }

    public static String getIP(String s)
    {
        String s1;
        try
        {
            if(System.getProperty("os.name").startsWith("Linux"))
            {
                synchronized(syncIPAdrs)
                {
                    InetAddress inetaddress1 = InetAddress.getByName(s);
                    s1 = inetaddress1.getHostAddress().trim();
                }
            } else
            {
                InetAddress inetaddress = InetAddress.getByName(s);
                s1 = inetaddress.getHostAddress().trim();
            }
        }
        catch(UnknownHostException unknownhostexception)
        {
            s1 = s;
        }
        return s1;
    }

    public static boolean inNet(String s, String s1, String s2)
    {
        if(s.indexOf(":") != -1 && s1.indexOf(":") != -1)
            return inIPV6Net(s, s1, s2);
        if(s.indexOf(":") != -1 || s1.indexOf(":") != -1)
            return false;
        else
            return inNet(getAddrLong(s), s1, s2);
    }

    private static boolean inNet(long l, String s, String s1)
    {
        long l1 = getAddrLong("255.255.255.255");
        long l2 = getAddrLong(s1);
        long l3 = getAddrLong(s);
        l3 &= l2;
        long l4 = l1 ^ l2;
        if(l == 0L)
            return false;
        return l < l3 + l4 && l >= l3;
    }

    public static String getMinMaxAddr(Vector vector, boolean flag)
    {
        String s = (String)vector.firstElement();
        long l = getAddrLong(s);
        for(int i = 1; i < vector.size(); i++)
        {
            String s1 = (String)vector.elementAt(i);
            long l1 = getAddrLong(s1);
            if(!flag)
            {
                if(l1 < l)
                {
                    s = s1;
                    l = l1;
                }
            } else
            if(l1 > l)
            {
                s = s1;
                l = l1;
            }
        }

        return s;
    }

    public static int getAddressRangeIndex(String s, Vector vector, Vector vector1)
    {
        if(vector == null && vector1 == null)
            return -1;
        long l = getAddrLong(s);
        if(l == 0L)
            return -1;
        for(int i = 0; i < vector.size(); i++)
        {
            long l1 = getAddrLong((String)vector.elementAt(i));
            long l2 = getAddrLong((String)vector1.elementAt(i));
            if(l <= l2 && l >= l1)
                return i;
        }

        return -1;
    }

    public static boolean isAddressInRange(String s, Vector vector, Vector vector1)
    {
        if(vector == null && vector1 == null)
            return true;
        long l = getAddrLong(s);
        if(l == 0L)
            return false;
        for(int i = 0; i < vector.size(); i++)
        {
            long l1 = getAddrLong((String)vector.elementAt(i));
            long l2 = getAddrLong((String)vector1.elementAt(i));
            if(l <= l2 && l >= l1)
                return true;
        }

        return false;
    }

    public static String getMACAddrFromIPV6Addr(String s)
    {
        int ai[] = getIPV6AddrArray(s);
        if(ai == null || ai.length != 16)
            return null;
        int ai1[] = new int[6];
        ai1[0] = ai[8];
        ai1[1] = ai[9];
        ai1[2] = ai[10];
        ai1[3] = ai[13];
        ai1[4] = ai[14];
        ai1[5] = ai[15];
        int i = ai1[0];
        String s1 = Integer.toBinaryString(i);
        if(s1.length() < 2)
            return null;
        char ac[] = s1.toCharArray();
        ac[s1.length() - 2] = '0';
        s1 = new String(ac);
        ai1[0] = Integer.valueOf(s1, 2).intValue();
        String s2 = "";
        for(int j = 0; j < ai1.length; j++)
        {
            s2 = s2 + Integer.toHexString(ai1[j]);
            s2 = s2 + " ";
        }

        s2 = s2.substring(0, s2.length() - 1);
        return s2;
    }

    public static String[] getNetAndMaskFromIPV6Addr(String s, int i)
    {
        Vector vector = new Vector();
        int j = i / 8;
        int k = i % 8;
        for(int l = 0; l < j; l++)
            vector.addElement("ff");

        if(k != 0)
        {
            char ac[] = {
                '0', '0', '0', '0', '0', '0', '0', '0'
            };
            for(int j1 = 0; j1 < k; j1++)
                ac[j1] = '1';

            String s2 = new String(ac);
            vector.addElement(Integer.toHexString(Integer.valueOf(s2, 2).intValue()));
        }
        for(int i1 = vector.size(); i1 < 16; i1++)
            vector.addElement("00");

        String s1 = "";
        for(int k1 = 0; k1 < vector.size(); k1 += 2)
        {
            s1 = s1 + (String)vector.elementAt(k1);
            s1 = s1 + (String)vector.elementAt(k1 + 1);
            s1 = s1 + ":";
        }

        s1 = s1.substring(0, s1.length() - 1);
        String s3 = getNetAddr(s, s1);
        if(s3 == null)
            return null;
        try
        {
            s1 = InetAddress.getByName(s1).getHostAddress();
        }
        catch(UnknownHostException unknownhostexception) { }
        return (new String[] {
            s3, s1
        });
    }

    public static boolean compareMACAddrs(String s, String s1)
    {
        String s2 = " ";
        if(s.indexOf(":") != -1)
            s2 = ":";
        else
        if(s.indexOf("-") != -1)
            s2 = "-";
        StringTokenizer stringtokenizer = new StringTokenizer(s, s2);
        if(stringtokenizer.countTokens() != 6)
            return false;
        int ai[] = new int[6];
        for(int i = 0; i < 6; i++)
            ai[i] = Integer.parseInt(stringtokenizer.nextToken(), 16);

        String s3 = " ";
        if(s1.indexOf(":") != -1)
            s3 = ":";
        else
        if(s1.indexOf("-") != -1)
            s3 = "-";
        StringTokenizer stringtokenizer1 = new StringTokenizer(s1, s3);
        if(stringtokenizer1.countTokens() != 6)
            return false;
        int ai1[] = new int[6];
        for(int j = 0; j < 6; j++)
            ai1[j] = Integer.parseInt(stringtokenizer1.nextToken(), 16);

        long l = 0L;
        for(int k = 0; k < 6; k++)
            l |= (long)ai[k] << 8 * (3 - k);

        long l1 = 0L;
        for(int i1 = 0; i1 < 6; i1++)
            l1 |= (long)ai1[i1] << 8 * (3 - i1);

        return l == l1;
    }

    public static boolean compareIPV6Addrs(String s, String s1)
    {
        String s2 = getMACAddrFromIPV6Addr(s);
        String s3 = getMACAddrFromIPV6Addr(s1);
        if(s2 == null || s3 == null)
            return false;
        else
            return compareMACAddrs(s2, s3);
    }

    private static String getIPV6NetAddr(String s, String s1)
    {
        int ai[] = getIPV6AddrArray(s);
        int ai1[] = getIPV6AddrArray(s1);
        if(ai == null || ai1 == null)
            return null;
        if(ai.length != 16 || ai1.length != 16)
            return null;
        int ai2[] = new int[ai.length];
        int i = 0;
        for(int j = 0; j < ai.length; j += 4)
        {
            int ai3[] = {
                ai[j], ai[j + 1], ai[j + 2], ai[j + 3]
            };
            int ai4[] = {
                ai1[j], ai1[j + 1], ai1[j + 2], ai1[j + 3]
            };
            long l = 0L;
            for(int j1 = 0; j1 < 4; j1++)
                l |= (long)ai3[j1] << 8 * (3 - j1);

            long l1 = 0L;
            for(int i2 = 0; i2 < 4; i2++)
                l1 |= (long)ai4[i2] << 8 * (3 - i2);

            long l2 = l & l1;
            for(int j2 = 0; j2 < 4; j2++)
                ai2[i++] = (int)(l2 >> 8 * (3 - j2) & 255L);

        }

        String s2 = "";
        for(int k = 0; k < ai2.length; k += 2)
        {
            String s3 = Integer.toHexString(ai2[k]);
            for(int i1 = s3.length(); i1 < 2; i1++)
                s3 = "0" + s3;

            String s4 = Integer.toHexString(ai2[k + 1]);
            for(int k1 = s4.length(); k1 < 2; k1++)
                s4 = "0" + s4;

            s2 = s2 + s3 + s4 + ":";
        }

        s2 = s2.substring(0, s2.length() - 1);
        try
        {
            s2 = InetAddress.getByName(s2).getHostAddress();
        }
        catch(UnknownHostException unknownhostexception) { }
        return s2;
    }

    private static int[] getIPV6AddrArray(String s)
    {
        try
        {
            InetAddress inetaddress = InetAddress.getByName(s);
            s = inetaddress.getHostAddress();
        }
        catch(UnknownHostException unknownhostexception) { }
        StringTokenizer stringtokenizer = new StringTokenizer(s, ":");
        if(stringtokenizer.countTokens() != 8)
            return null;
        String as[] = new String[8];
        for(int i = 0; i < 8; i++)
            as[i] = stringtokenizer.nextToken();

        int ai[] = new int[16];
        int j = 0;
        for(int k = 0; k < as.length; k++)
        {
            for(int l = as[k].length(); l < 4; l++)
                as[k] = "0" + as[k];

            String s1 = as[k].substring(0, 2);
            String s2 = as[k].substring(2);
            try
            {
                ai[j++] = Integer.parseInt(s1, 16);
                ai[j++] = Integer.parseInt(s2, 16);
            }
            catch(NumberFormatException numberformatexception)
            {
                return null;
            }
        }

        for(int i1 = 0; i1 < 16; i1++)
            if(ai[i1] < 0 || ai[i1] > 255)
                return null;

        return ai;
    }

    private static boolean inIPV6Net(String s, String s1, String s2)
    {
        String s3 = getIPV6NetAddr(s, s2);
        try
        {
            s1 = InetAddress.getByName(s1).getHostAddress();
        }
        catch(UnknownHostException unknownhostexception) { }
        return s3.equals(s1);
    }

    private static boolean enableDNS = true;
    private static Integer syncDNSName = new Integer(0);
    private static Integer syncIPAdrs = new Integer(0);

}