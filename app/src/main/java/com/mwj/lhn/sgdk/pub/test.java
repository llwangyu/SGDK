package com.mwj.lhn.sgdk.pub;

import org.apache.commons.net.ftp.FTPClient;

import java.io.FileInputStream;

/**
 * Created by LHN on 2017/11/16.
 */

public class test {
    public static void main(String[] args){
        String Ssgsj="20171100";
        Ssgsj=Ssgsj.substring(0,4)+"-"+Ssgsj.substring(5,6)+"-"+Ssgsj.substring(7,8);
        System.out.println(Ssgsj);
    }

}
