package com.clarivate.cc.wherehows.controller;

import com.clarivate.cc.wherehows.UserWorkspace;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class Hello {
    static Logger LOG = LoggerFactory.getLogger(Hello.class);

    @RequestMapping("/")
    public String index() {
        return "Hello from Wherehows Proxy!";
    }

    @RequestMapping(value = "/workspace/{user_id}/{db_id}", method = RequestMethod.GET)
    public RedirectView redirect(@PathVariable("user_id") String user_id,
                                 @PathVariable("db_id") int db_id,
                                 @RequestParam(value = "tbl", required=false) String tbl,
                                 @RequestParam(value = "parent", required=false) String parent) {

        UserWorkspace workspace = new UserWorkspace();
        String fullName = StringUtils.isEmpty(parent)? tbl: parent + "." + tbl;

        String url = workspace.getWorkspaceUrl(user_id, db_id,"", fullName);
        LOG.info("url ==== " + url);
        return new RedirectView(url);
    }
}