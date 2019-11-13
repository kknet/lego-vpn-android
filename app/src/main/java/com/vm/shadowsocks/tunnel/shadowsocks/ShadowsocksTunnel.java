package com.vm.shadowsocks.tunnel.shadowsocks;

import com.vm.shadowsocks.tunnel.Tunnel;
import com.vm.shadowsocks.ui.MainActivity;
import com.vm.shadowsocks.ui.P2pLibManager;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.List;
import android.util.Log;
import java.util.concurrent.atomic.AtomicInteger;

public class ShadowsocksTunnel extends Tunnel {
    private boolean m_TunnelEstablished;
    public ICrypt encryptor = null;
    private String seckey = "";
    static private AtomicInteger connect_times = new AtomicInteger(1);

    public ShadowsocksTunnel(ShadowsocksConfig config, Selector selector) throws Exception {
        super(config.ServerAddress, selector);
    }

    protected int ipToNum(String ip) {
        int num = 0;
        String[] sections = ip.split("\\.");
        int i = 3;
        for (String str : sections) {
            num += (Long.parseLong(str) << (i * 8));
            i--;
        }
        //	System.out.println(num);
        return num;
    }

    private static String HexEncode(byte[] src) {
        String strHex = "";
        StringBuilder sb = new StringBuilder("");
        for (int n = 0; n < src.length; n++) {
            strHex = Integer.toHexString(src[n] & 0xFF);
            sb.append((strHex.length() == 1) ? "0" + strHex : strHex);
        }
        return sb.toString().trim();
    }

    private static byte[] HexDecode(String src) {
        int m = 0, n = 0;
        int byteLen = src.length() / 2;
        byte[] ret = new byte[byteLen];
        for (int i = 0; i < byteLen; i++) {
            m = i * 2 + 1;
            n = m + 1;
            int intVal = Integer.decode("0x" + src.substring(i * 2, m) + src.substring(m, n));
            ret[i] = Byte.valueOf((byte)intVal);
        }
        return ret;
    }

    @Override
    protected void onConnected(ByteBuffer buffer) throws Exception {
        buffer.clear();
        // https://shadowsocks.org/en/spec/protocol.html
        buffer.put((byte) 0x03);//domain
        byte[] domainBytes = m_DestAddress.getHostName().getBytes();
        buffer.put((byte) domainBytes.length);//domain length;
        buffer.put(domainBytes);
        buffer.putShort((short) m_DestAddress.getPort());
        buffer.flip();
        byte[] header = new byte[buffer.limit()];
        buffer.get(header);

        if (!seckey.equals(P2pLibManager.getInstance().seckey)) {
            encryptor = CryptFactory.get(P2pLibManager.getInstance().choosed_method, P2pLibManager.getInstance().seckey);
            seckey = P2pLibManager.getInstance().seckey;
        }
        byte[] enc_data = encryptor.encrypt(header);

        buffer.clear();
        if (P2pLibManager.getInstance().use_smart_route) {
            String vpn_ip = P2pLibManager.getInstance().choosed_vpn_ip;
            int vpn_port = P2pLibManager.getInstance().choosed_vpn_port;
            buffer.putInt(ipToNum(vpn_ip));
            buffer.putShort((short)vpn_port);
        }

        buffer.put(P2pLibManager.getInstance().public_key.getBytes());
        byte[] choosed_method_bytes = P2pLibManager.getInstance().platfrom.getBytes();
        buffer.put((byte) choosed_method_bytes.length);
        buffer.put(choosed_method_bytes);
        buffer.put(enc_data);
        buffer.flip();

        if (write(buffer, true)) {
            m_TunnelEstablished = true;
            onTunnelEstablished();
        } else {
            m_TunnelEstablished = true;
            this.beginReceive();
        }

        int now_times = connect_times.incrementAndGet();
        if (now_times > 15) {
            String old_ip = P2pLibManager.getInstance().choosed_vpn_ip;
            P2pLibManager.getInstance().GetVpnNode();
            if (!old_ip.equals(P2pLibManager.getInstance().choosed_vpn_ip)) {
                connect_times.set(0);
            }
        }
    }

    @Override
    protected boolean isTunnelEstablished() {
        return m_TunnelEstablished;
    }

    @Override
    protected void beforeSend(ByteBuffer buffer) throws Exception {
        byte[] bytes = new byte[buffer.limit()];
        buffer.get(bytes);
        byte[] enc_data = encryptor.encrypt(bytes);
        buffer.clear();
        buffer.put(enc_data);
        buffer.flip();
    }

    @Override
    protected void afterReceived(ByteBuffer buffer) throws Exception {
        byte[] bytes = new byte[buffer.limit()];
        buffer.get(bytes);
        byte[] newbytes = encryptor.decrypt(bytes);
        buffer.clear();
        buffer.put(newbytes);
        buffer.flip();
        connect_times.set(0);
    }

    @Override
    protected void onDispose() {
        encryptor = null;
    }
}
