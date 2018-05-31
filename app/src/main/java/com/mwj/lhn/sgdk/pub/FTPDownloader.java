package com.mwj.lhn.sgdk.pub;

/**
 * Created by LHN on 2017/11/28.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPHTTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;
import org.apache.commons.net.util.TrustManagerUtils;

import android.text.TextUtils;
import android.util.Log;

public class FTPDownloader {
    private static final String TAG = "--FTPDownloader--";
    private static FTPDownloader downloader;
    @SuppressWarnings("unused")
    private boolean binaryTransfer = false, error = false, listFiles = false, listNames = false, hidden = false;
    @SuppressWarnings("unused")
    private boolean localActive = false, useEpsvWithIPv4 = false, feat = false, printHash = false;
    @SuppressWarnings("unused")
    private boolean mlst = false, mlsd = false;
    @SuppressWarnings("unused")
    private boolean lenient = false;
    private long keepAliveTimeout = -1;
    private int controlKeepAliveReplyTimeout = -1;
    private int minParams = 5; // listings require 3 params
    private String protocol = null; // SSL protocol
    @SuppressWarnings("unused")
    private String doCommand = null;
    private String trustmgr = null;
    private String proxyHost = null;
    private int proxyPort = 80;
    private String proxyUser = null;
    private String proxyPassword = null;

    private int port = 0;
    private String server;
    private String username;
    private String password;
    private String remote;
    private String local;
    private FTPClient ftp;

    private StreamListener sListener = null;

    public synchronized static FTPDownloader getInstance(){
        if(downloader == null){
            downloader = new FTPDownloader();
        }
        return downloader;
    }

    public void config(String server,String username,String password, String remote, String local){
        if(TextUtils.isEmpty(server) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password)){
            throw new NullPointerException("server username password can not null");
        }else{
            init(new String[]{"-b","-e","-w","10000","-#",server,username,password,remote,local});
        }

    }

    private void init(String[] args){
        int base = 0;
        for (base = 0; base < args.length; base++)
        {
            if (args[base].equals("-a")) {
                localActive = true;
            }
            else if (args[base].equals("-b")) {
                binaryTransfer = true;
            }
            else if (args[base].equals("-c")) {
                doCommand = args[++base];
                minParams = 3;
            }
            else if (args[base].equals("-d")) {
                mlsd = true;
                minParams = 3;
            }
            else if (args[base].equals("-e")) {
                useEpsvWithIPv4 = true;
            }
            else if (args[base].equals("-f")) {
                feat = true;
                minParams = 3;
            }
            else if (args[base].equals("-h")) {
                hidden = true;
            }
            else if (args[base].equals("-k")) {
                keepAliveTimeout = Long.parseLong(args[++base]);
            }
            else if (args[base].equals("-l")) {
                listFiles = true;
                minParams = 3;
            }
            else if (args[base].equals("-L")) {
                lenient = true;
            }
            else if (args[base].equals("-n")) {
                listNames = true;
                minParams = 3;
            }
            else if (args[base].equals("-p")) {
                protocol = args[++base];
            }
            else if (args[base].equals("-t")) {
                mlst = true;
                minParams = 3;
            }
            else if (args[base].equals("-w")) {
                controlKeepAliveReplyTimeout = Integer.parseInt(args[++base]);
            }
            else if (args[base].equals("-T")) {
                trustmgr = args[++base];
            }
            else if (args[base].equals("-PrH")) {
                proxyHost = args[++base];
                String parts[] = proxyHost.split(":");
                if (parts.length == 2){
                    proxyHost=parts[0];
                    proxyPort=Integer.parseInt(parts[1]);
                }
            }
            else if (args[base].equals("-PrU")) {
                proxyUser = args[++base];
            }
            else if (args[base].equals("-PrP")) {
                proxyPassword = args[++base];
            }
            else if (args[base].equals("-#")) {
                printHash = true;
            }
            else {
                break;
            }
        }

        int remain = args.length - base;
        if (remain < minParams) // server, user, pass, remote, local [protocol]
        {
            throw new IllegalArgumentException("server, user, pass argument can not null!");
        }

        server = args[base++];

        String parts[] = server.split(":");
        if (parts.length == 2){
            server=parts[0];
            port=Integer.parseInt(parts[1]);
        }
        username = args[base++];
        password = args[base++];

        remote = null;
        if (args.length - base > 0) {
            remote = args[base++];
        }

        local = null;
        if (args.length - base > 0) {
            local = args[base++];
        }

        if (protocol == null ) {
            if(proxyHost !=null) {
                Log.d(TAG,"Using HTTP proxy server: " + proxyHost);
                ftp = new FTPHTTPClient(proxyHost, proxyPort, proxyUser, proxyPassword);
            }
            else {
                ftp = new FTPClient();
            }
        } else {
            FTPSClient ftps;
            if (protocol.equals("true")) {
                ftps = new FTPSClient(true);
            } else if (protocol.equals("false")) {
                ftps = new FTPSClient(false);
            } else {
                String prot[] = protocol.split(",");
                if (prot.length == 1) { // Just protocol
                    ftps = new FTPSClient(protocol);
                } else { // protocol,true|false
                    ftps = new FTPSClient(prot[0], Boolean.parseBoolean(prot[1]));
                }
            }
            ftp = ftps;
            if ("all".equals(trustmgr)) {
                ftps.setTrustManager(TrustManagerUtils.getAcceptAllTrustManager());
            } else if ("valid".equals(trustmgr)) {
                ftps.setTrustManager(TrustManagerUtils.getValidateServerCertificateTrustManager());
            } else if ("none".equals(trustmgr)) {
                ftps.setTrustManager(null);
            }
        }

        if (printHash) {
            ftp.setCopyStreamListener(createListener());
        }
        if (keepAliveTimeout >= 0) {
            ftp.setControlKeepAliveTimeout(keepAliveTimeout);
        }
        if (controlKeepAliveReplyTimeout >= 0) {
            ftp.setControlKeepAliveReplyTimeout(controlKeepAliveReplyTimeout);
        }
        ftp.setListHiddenFiles(hidden);

        // suppress login details
        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out), true));
    }

    public boolean connect(){
        try
        {
            int reply;
            if (port > 0) {
                ftp.connect(server, port);
            } else {
                ftp.connect(server);
            }
            Log.d(TAG,"Connected to " + server + " on " + (port>0 ? port : ftp.getDefaultPort()));

            // After connection attempt, you should check the reply code to verify
            // success.
            reply = ftp.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply))
            {
                ftp.disconnect();
                Log.d(TAG,"FTP server refused connection.");
            }else{
                return true;
            }
        }
        catch (IOException e)
        {
            if (ftp.isConnected())
            {
                try
                {
                    ftp.disconnect();
                }
                catch (IOException f)
                {
                    // do nothing
                }
            }
            Log.d(TAG,"Could not connect to server."+e.getMessage());
        }
        return false;
    }

    public boolean login(){
        try
        {
            if (!ftp.login(username, password))
            {
                ftp.logout();
                error = true;
                return false;
            }

            System.out.println("Remote system is " + ftp.getSystemType());

            if (binaryTransfer) {
                ftp.setFileType(FTP.BINARY_FILE_TYPE);
            }

            // Use passive mode as default because most of us are
            // behind firewalls these days.
            if (localActive) {
                ftp.enterLocalActiveMode();
            } else {
                ftp.enterLocalPassiveMode();
            }

            ftp.setUseEPSVwithIPv4(useEpsvWithIPv4);
        }catch(FTPConnectionClosedException e){
            Log.d(TAG,e.getMessage());
        } catch (IOException e) {
            Log.d(TAG,e.getMessage());
        }
        return true;
    }

    public void disConnect(){
        if (ftp.isConnected())
        {
            try
            {
                ftp.disconnect();
            }
            catch (IOException e)
            {
                Log.d(TAG,e.getMessage());
            }
        }
    }

    public boolean upload(){
        if(sListener != null){
            File f = new File(local);
            sListener.totalSize(f.length());
        }
        InputStream input = null;
        try {
            input = new FileInputStream(local);
            ftp.storeFile(remote, input);
            return true;
        } catch (FileNotFoundException e) {
            Log.d(TAG,e.getMessage());
        } catch (IOException e) {
            Log.d(TAG,e.getMessage());
        } finally{
            try {
                input.close();
            } catch (IOException e) {
                Log.d(TAG,e.getMessage());
            }
        }
        return false;
    }

    private void listFile(){
        try {
            FTPFile[] fileList = ftp.listFiles(remote);
            for(FTPFile f :fileList){
                if(sListener != null){
                    sListener.totalSize(f.getSize());
                    return;
                };
            }
        } catch (IOException e) {
            Log.d(TAG,e.getMessage());
        }
    }

    public boolean download(){
        listFile();
        OutputStream output = null;
        try {
            output = new FileOutputStream(local);
            ftp.retrieveFile(remote, output);
            return true;
        } catch (FileNotFoundException e) {
            Log.d(TAG,e.getMessage());
        } catch (IOException e) {
            Log.d(TAG,e.getMessage());
        } finally{
            try {
                output.close();
            } catch (IOException e) {
                Log.d(TAG,e.getMessage());
            }
        }
        return false;
    }

    private CopyStreamListener createListener(){
        return new CopyStreamListener(){
            public void bytesTransferred(CopyStreamEvent event) {
                bytesTransferred(event.getTotalBytesTransferred(), event.getBytesTransferred(), event.getStreamSize());
            }

            public void bytesTransferred(long totalBytesTransferred,
                                         int bytesTransferred, long streamSize) {
                if(sListener != null){
                    sListener.progress(bytesTransferred);
                    sListener.progressed(totalBytesTransferred);
                }
            }
        };
    }

    public void setRemote(String remote) {
        this.remote = remote;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public void setServer(String server) {
        this.server = server;
        String parts[] = this.server.split(":");
        if (parts.length == 2){
            this.server=parts[0];
            port=Integer.parseInt(parts[1]);
        }
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setsListener(StreamListener sListener) {
        this.sListener = sListener;
    }

    public interface StreamListener{
        public void totalSize(long total);
        public void progress(int progress);
        public void progressed(long progressed);
    }

    public static void main(String[] args) {
        FTPDownloader fd = FTPDownloader.getInstance();
        fd.config("server", "username", "password", "remotetest.dat", "d:/test.dat");
        fd.setsListener(new StreamListener() {
            long totalsize = 0;
            @Override
            public void totalSize(long total) {
                totalsize = total;

            }

            @Override
            public void progressed(long progressed) {
                Log.d("--ftp--", "progress:"+progressed/totalsize*100+"%");

            }

            @Override
            public void progress(int progress) {

            }
        });
        fd.connect();
        fd.login();
        fd.download();
        fd.disConnect();

    }
}
