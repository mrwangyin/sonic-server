/*
 *   sonic-server  Sonic Cloud Real Machine Platform.
 *   Copyright (C) 2022 SonicCloudOrg
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.cloud.sonic.controller.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.cloud.sonic.common.config.WebAspect;
import org.cloud.sonic.common.http.RespEnum;
import org.cloud.sonic.common.http.RespModel;
import org.cloud.sonic.controller.models.domain.Agents;
import org.cloud.sonic.controller.models.interfaces.ConfType;
import org.cloud.sonic.controller.services.AgentsService;
import org.cloud.sonic.controller.services.ConfListService;
import org.cloud.sonic.controller.transport.TransportWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "配置项相关")
@RestController
@RequestMapping("/confList")
public class ConfListController {
    @Autowired
    private ConfListService confListService;
    @Autowired
    private AgentsService agentsService;

    @WebAspect
    @ApiOperation(value = "获取远控超时时间", notes = "获取远控超时时间")
    @GetMapping("/getRemoteTimeout")
    public RespModel getRemoteTimeout() {
        return new RespModel<>(RespEnum.SEARCH_OK,
                Integer.parseInt(confListService.searchByKey(ConfType.REMOTE_DEBUG_TIMEOUT).getContent()));
    }

    @WebAspect
    @ApiOperation(value = "设置远控超时时间", notes = "设置远控超时时间")
    @GetMapping("/setRemoteTimeout")
    public RespModel setRemoteTimeout(@RequestParam(name = "timeout") int timeout) {
        confListService.save(ConfType.REMOTE_DEBUG_TIMEOUT, timeout + "", null);
        List<Agents> agentsList = agentsService.findAgents();
        for (Agents agents : agentsList) {
            JSONObject result = new JSONObject();
            result.put("msg", "settings");
            result.put("remoteTimeout", timeout);
            TransportWorker.send(agents.getId(), result);
        }
        return new RespModel<>(RespEnum.HANDLE_OK);
    }
}
