1. 终端1注册，下发授权码
0100002d0138888888880065000000014d414b4552202050524f424f4f4b207a68616e36362d616d640000000000413101b4a841383838383809
2. 终端2注册，下发授权码
0100002d0138777788880066000200024d414b4532202050524f424f4f4b207a68616e36362d616d320000000000413202b4a841373737373732
3. 两终端发送心跳，打印心跳信息
00020000013888888888042f10
00020000013877778888042e11
4. 中止所有客户端连接2分钟

5. 终端1位置信息汇报 被拒绝
0200001601388888888800660000000200000002000000010000000200010001000149

6. 终端1鉴权
0102001301388888888800663031333838383838383838385f61757468656413

7. 终端1位置信息汇报
0200001601388888888800660000000200000002000000010000000200010001000149

8. 服务端查询终端1参数
<?xml version="1.0" ?><com.ant.jt808.base.dto.jt808.basics.Message><body class="com.ant.jt808.base.dto.jt808.ParameterSetting"><parameters></parameters></body><type>33028</type><bodyProperties>1</bodyProperties><mobileNumber>013888888888</mobileNumber><serialNumber>102</serialNumber><bodyLength>1</bodyLength><encryptionType>0</encryptionType><subPackage>false</subPackage><reservedBit>0</reservedBit></com.ant.jt808.base.dto.jt808.basics.Message>

a:b:c:response





--------
问题：鉴权衔接流程（鉴权应该反馈到业务层进行处理，处理完成后反馈鉴权结果，然后业务层各个处理逻辑应先判断是否已鉴权？）
