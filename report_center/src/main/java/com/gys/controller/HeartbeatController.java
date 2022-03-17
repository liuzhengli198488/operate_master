package com.gys.controller;

import com.gys.common.data.JsonResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HeartbeatController {
    @GetMapping({"/heartbeat"})
    public JsonResult heartbeat(){
        return JsonResult.success("Alive", "");
    }
}
