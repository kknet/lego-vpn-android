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

public class ShadowsocksTunnel extends Tunnel {

    private ICrypt m_Encryptor;
    private ShadowsocksConfig m_Config;
    private boolean m_TunnelEstablished;
    private String choosed_vpn_ip;
    private int choosed_vpn_port;
    private String choosed_method = "aes-128-cfb";
    private String public_key;

    public ShadowsocksTunnel(ShadowsocksConfig config, Selector selector) throws Exception {
        super(config.ServerAddress, selector);
        m_Config = config;
        public_key = MainActivity.getPublicKey();
        String choosed_vpn = MainActivity.choosed_vpn_url;
        String[] vpn_info = choosed_vpn.split(":");
        if (vpn_info.length < 6) {
            return;
        }
        choosed_vpn_ip = vpn_info[0];
        choosed_vpn_port = Integer.parseInt(vpn_info[1]);
        m_Encryptor = CryptFactory.get(choosed_method, vpn_info[3]);
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
        byte[] enc_data = m_Encryptor.encrypt(header);

        buffer.clear();
        if (MainActivity.use_smart_route) {
            String vpn_ip = choosed_vpn_ip;
            int vpn_port = choosed_vpn_port;
            // TODO(change sec key)
            /*
            String res = P2pLibManager.getInstance().GetVpnNode();
            if (!res.isEmpty()) {
                String tmp_split[] = res.split(":");
                if (tmp_split.length == 2) {
                    vpn_ip = tmp_split[0];
                    vpn_port = Integer.parseInt(tmp_split[1]);
                }
            }
*/
            Log.e("vpn server", "real dest server, " + vpn_ip + ":" + vpn_port);
            buffer.putInt(ipToNum(vpn_ip));
            buffer.putShort((short)vpn_port);
        }
        buffer.put(public_key.getBytes());
        byte[] choosed_method_bytes = choosed_method.getBytes();
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
    }

    @Override
    protected boolean isTunnelEstablished() {
        return m_TunnelEstablished;
    }

    @Override
    protected void beforeSend(ByteBuffer buffer) throws Exception {
        byte[] bytes = new byte[buffer.limit()];
        buffer.get(bytes);
        byte[] enc_data = m_Encryptor.encrypt(bytes);
        buffer.clear();
        buffer.put(enc_data);
        buffer.flip();
    }

    @Override
    protected void afterReceived(ByteBuffer buffer) throws Exception {
        byte[] bytes = new byte[buffer.limit()];
        buffer.get(bytes);
        byte[] newbytes = m_Encryptor.decrypt(bytes);
        buffer.clear();
        buffer.put(newbytes);
        buffer.flip();
    }

    @Override
    protected void onDispose() {
        m_Config = null;
        m_Encryptor = null;
    }

}
