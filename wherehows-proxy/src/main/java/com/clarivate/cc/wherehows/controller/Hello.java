package com.clarivate.cc.wherehows.controller;

import com.clarivate.cc.wherehows.dao.UserWorkspaceDao;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@RestController
public class Hello {
    @RequestMapping("/")
    public String index() {
        return "Hello from Wherehows Proxy!";
    }

    @RequestMapping(value = "/workspace/{user_id}/{db_id}", method = RequestMethod.GET)
    public RedirectView go(@PathVariable("user_id") String user_id,
                           @PathVariable("db_id") String db_id,
                           @RequestParam(value = "tbl", required=false) String tbl) {
        //code
        UserWorkspaceDao userWorkspaceDao = new UserWorkspaceDao();

        Map<String, Object> workspace = userWorkspaceDao.getUserWorkspace(user_id, Integer.parseInt(db_id), "", tbl);

        System.out.println(workspace);
        // test redirect;
        return new RedirectView(workspace.get("note_url").toString());
    }
}