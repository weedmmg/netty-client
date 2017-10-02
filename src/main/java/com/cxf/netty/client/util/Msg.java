package com.cxf.netty.client.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Msg {

    private static String encoding = "utf8";
    private static Logger logger = LoggerFactory.getLogger(Msg.class);
    private final static byte errorCmd = 0x04;

    public static byte[] conventMsg(Object msg) throws UnsupportedEncodingException {

        ByteBuf totalBytes = Unpooled.wrappedBuffer((byte[]) msg);
        ByteBuf cmdBytes = totalBytes.slice(1, 1), lenBytes = totalBytes.slice(2, 1);

        byte[] cmds = new byte[1], lens = new byte[1];

        cmdBytes.readBytes(cmds);
        lenBytes.readBytes(lens);

        int totalLengh = totalBytes.readableBytes(), len = ByteUtil.byteArrayToInt(lens, 1), signLen = totalLengh - 4 - len;

        ByteBuf dataBytes = totalBytes.slice(3, len), signBytes = totalBytes.slice(3 + len, signLen), checkBytes = totalBytes.slice(1, 2 + len);
        byte[] datas = new byte[len];
        dataBytes.readBytes(datas);
        byte[] checkBytesArray = new byte[2 + len], signBytesArray = new byte[signLen];

        checkBytes.readBytes(checkBytesArray);
        signBytes.readBytes(signBytesArray);

        if (checkSign(checkBytesArray, signBytesArray)) {
            logger.error("sign error");
            return intMsg(errorCmd, "sign error".getBytes(encoding));
        }

        return datas;
    }

    public static boolean checkSign(byte[] msg, byte[] sign) {
        String msgSign = MD5Util.string2MD5(new String(msg));
        if (msgSign.equals(new String(sign))) {
            return false;
        }
        return true;
    }

    public static byte[] intMsg(byte cmd, byte[] data) {
        ByteBuf bf = Unpooled.wrappedBuffer(initCmd(cmd), initLen(data.length), data);
        byte[] array = new byte[bf.readableBytes()];
        bf.readBytes(array);
        return array;

    }

    /**
     * 初始化坐标信息
     * 
     * @param cmd
     * @param times
     *            坐标改变
     * @return
     */
    public static byte[] intMsgLls(byte cmd, int times) {
        byte[] data = initls(times);
        ByteBuf bf = Unpooled.wrappedBuffer(initCmd(cmd), initLen(data.length), data);
        byte[] array = new byte[bf.readableBytes()];
        bf.readBytes(array);
        return array;

    }

    public static byte[] initCmd(byte b) {
        byte[] bytes = new byte[1];
        bytes[0] = b;
        return bytes;
    }

    public static byte[] initLen(int size) {
        logger.debug("len:" + size);
        return ByteUtil.intToByteArray(size, 1);
    }

    public static byte[] initls(int times) {
        long ll = 121552298, la = 29820054 + times * 10;

        byte[] dws = ByteUtil.longToByteArray(0, 1), nss = ByteUtil.longToByteArray(1, 1), ews = ByteUtil.longToByteArray(1, 1), speeds = ByteUtil.longToByteArray(100, 2), lls = ByteUtil
                .longToByteArray(ll, 4), las = ByteUtil.longToByteArray(la, 4), status = ByteUtil.longToByteArray(0, 1);

        return ByteUtil.byteMergerAll(dws, nss, ews, speeds, lls, las, status);
    }

    public static void main(String[] args) {
        String headStr = "EX", endStr = "86";
        byte cmd = 0x01;
        String uuid = UUID.randomUUID().toString();
        logger.debug(uuid);
        byte[] head, end, msg;
        try {
            head = headStr.getBytes(encoding);
            end = endStr.getBytes(encoding);
            msg = intMsg(cmd, uuid.getBytes(encoding));

            String sign = MD5Util.string2MD5(new String(msg));

            byte[] newMsg = ByteUtil.byteMergerAll(head, msg, sign.getBytes(encoding), end);

            logger.debug(sign);
            logger.debug("加密后" + newMsg.length);
            System.out.println("len:" + newMsg.toString().length());

            System.out.println(Msg.conventMsg(newMsg));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
}
