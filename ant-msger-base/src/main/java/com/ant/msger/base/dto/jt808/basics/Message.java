package com.ant.msger.base.dto.jt808.basics;

import com.ant.msger.base.annotation.Property;
import com.ant.msger.base.enums.DataType;
import com.ant.msger.base.message.AbstractBody;
import com.ant.msger.base.message.AbstractMessage;

public class Message<T extends AbstractBody> extends AbstractMessage<T> {

    protected Integer delimiter = 0x7e;
    protected Integer type;
    // 2019版本追加，协议版本号
    protected Integer protocolVersion = 1;
    protected Integer bodyProperties;
    protected String mobileNumber;
    protected Integer serialNumber;

    protected Integer subPackageTotal;
    protected Integer subPackageNumber;

    protected Integer bodyLength = 0;
    protected Integer encryptionType = 0b000;
    protected boolean subPackage = false;
    // 2019版本追加，消息体属性中的版本标识，第14位
    protected Integer versionFlag = 1;
    protected Integer reservedBit = 0;

    protected String sessionId; // 响应通道消息处理的时候需要下，不是消息体的一部分

    public Message() {
    }

    public Message(Integer type, String mobileNumber, T body) {
        this.type = type;
        this.mobileNumber = mobileNumber;
        this.body = body;
    }

    public Message(Integer type, Integer serialNumber, String mobileNumber) {
        this.type = type;
        this.serialNumber = serialNumber;
        this.mobileNumber = mobileNumber;
    }

    public Message(Integer type, Integer serialNumber, String mobileNumber, T body) {
        this.type = type;
        this.serialNumber = serialNumber;
        this.mobileNumber = mobileNumber;
        this.body = body;
    }

    @Override
    public Integer getDelimiter() {
        return delimiter;
    }

    @Override
    public void setDelimiter(Integer delimiter) {
        this.delimiter = delimiter;
    }

    @Property(index = 0, type = DataType.WORD, desc = "消息ID")
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Property(index = 2, type = DataType.WORD, desc = "消息体属性")
    public Integer getBodyProperties() {
        if (bodyLength > 1024)
            System.out.println("The max value of msgLen is 1024, but {} ." + bodyLength);
        int subPkg = subPackage ? 1 : 0;
        int ret = (bodyLength & 0x3FF) |
                ((encryptionType << 10) & 0x1C00) |
                ((subPkg << 13) & 0x2000) |
                ((versionFlag << 14) & 0x4000) |
                ((reservedBit << 15) & 0x8000);
        this.bodyProperties = ret & 0xffff;

        return bodyProperties;
    }

    /**
     * [ 0-9 ] 0000,0011,1111,1111(3FF)(消息体长度)
     * [10-12] 0001,1100,0000,0000(1C00)(加密类型)
     * [ 13 ] 0010,0000,0000,0000(2000)(是否有子包)
     *  [14-15] 1100,0000,0000,0000(C000)(保留位) ,已弃用2011版
     *  [14] 0100,0000,0000,0000(4000)(版本标识)
     *  [15] 1000,0000,0000,0000(8000)(保留位)
     */
    public void setBodyProperties(Integer bodyProperties) {
        this.bodyProperties = bodyProperties;

        this.bodyLength = bodyProperties & 0x3ff;
        this.encryptionType = (bodyProperties & 0x1c00) >> 10;
        this.subPackage = ((bodyProperties & 0x2000) >> 13) == 1;
        this.versionFlag = ((bodyProperties & 0x4000) >> 14);
        this.reservedBit = ((bodyProperties & 0x8000) >> 15);
    }

    @Property(index = 4, type = DataType.BYTE, desc = "协议版本号")
    public Integer getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(Integer protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    @Property(index = 5, type = DataType.BCD8421, length = 10, pad = 48, desc = "终端手机号")
    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    @Property(index = 15, type = DataType.WORD, desc = "流水号")
    public Integer getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(Integer serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * 本次发送的子包是分包中的第几个消息包,从1开始
     * 如果消息体属性中相关标识位确定消息分包处理，则该项有内容，否则无该项
     */
    @Property(index = 17, type = DataType.WORD, desc = "消息包总数")
    public Integer getSubPackageTotal() {
        return hasSubPackage() ? subPackageTotal : null;
    }

    public void setSubPackageTotal(Integer subPackageTotal) {
        this.subPackageTotal = subPackageTotal;
    }

    /**
     * 本次发送的子包是分包中的第几个消息包,从1开始
     * 如果消息体属性中相关标识位确定消息分包处理，则该项有内容，否则无该项
     */
    @Property(index = 19, type = DataType.WORD, desc = "包序号")
    public Integer getSubPackageNumber() {
        return hasSubPackage() ? subPackageNumber : null;
    }

    public void setSubPackageNumber(Integer subPackageNumber) {
        this.subPackageNumber = subPackageNumber;
    }

    @Override
    public Integer getBodyLength() {
        return bodyLength;
    }

    public void setBodyLength(Integer bodyLength) {
        this.bodyLength = bodyLength;
    }

    @Override
    public Integer getHeaderLength() {
        return hasSubPackage() ? 21 : 17;
    }

    public Integer getEncryptionType() {
        return encryptionType;
    }

    public void setEncryptionType(Integer encryptionType) {
        this.encryptionType = encryptionType;
    }

    @Override
    public boolean hasSubPackage() {
        return subPackage;
    }

    public void setSubPackage(boolean subPackage) {
        this.subPackage = subPackage;
    }

    public boolean isSubPackage() {
        return subPackage;
    }

    public Integer getReservedBit() {
        return reservedBit;
    }

    public void setReservedBit(Integer reservedBit) {
        this.reservedBit = reservedBit;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}