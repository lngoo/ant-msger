package org.yzh.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yzh.web.config.SessionKey;
import org.yzh.web.endpoint.JT808Endpoint;
import org.yzh.web.jt808.dto.CommonResult;

import javax.servlet.http.HttpSession;

@Controller
public class ConsoleController {

    @Autowired
    private JT808Endpoint endpoint;

    @GetMapping("/")
    public String console(HttpSession session) {
        session.setAttribute(SessionKey.USER_ID, session.getId().hashCode());
        return "forward:/index.html";
    }

    @GetMapping("test/{terminalId}")
    @ResponseBody
    public CommonResult updateParameters(@PathVariable("terminalId") String terminalId, @RequestParam String hex) {
        CommonResult response = (CommonResult) endpoint.send(terminalId, hex);
        return response;
    }
}