1. 终端1注册，下发授权码
7e0100002d0138888888880065000000014d414b4552202050524f424f4f4b207a68616e36362d616d640000000000413101b4a8413838383838097e
2. 终端2注册，下发授权码
7e0100002d0138777788880066000200024d414b4532202050524f424f4f4b207a68616e36362d616d320000000000413202b4a8413737373737327e
3. 两终端发送心跳，打印心跳信息
7e00020000013888888888042f107e
7e00020000013877778888042e117e
4. 中止所有客户端连接2分钟

5. 终端1位置信息汇报 被拒绝
7e02000016013888888888006600000002000000020000000100000002000100010001497e

6. 终端1鉴权
7e0102001301388888888800663031333838383838383838385f617574686564137e

7. 终端1位置信息汇报
7e02000016013888888888006600000002000000020000000100000002000100010001497e

8. 服务端查询终端1参数
<?xml version="1.0" ?><com.antnest.msger.core.dto.jt808.basics.Message><body class="com.antnest.msger.core.dto.jt808.ParameterSetting"><parameters></parameters></body><type>33028</type><bodyProperties>1</bodyProperties><mobileNumber>013888888888</mobileNumber><serialNumber>102</serialNumber><bodyLength>1</bodyLength><encryptionType>0</encryptionType><subPackage>false</subPackage><reservedBit>0</reservedBit></com.antnest.msger.core.dto.jt808.basics.Message>

a:b:c:response

---------------------------------------
http://www.websocket-test.com/





--------
