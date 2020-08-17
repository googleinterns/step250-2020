package com.google.step.coffee.servlets;

import com.google.step.coffee.JsonServlet;
import com.google.step.coffee.entity.Group;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/api/groupList")
public class GroupListServlet extends JsonServlet {
    @Override
    public Object get(HttpServletRequest request) throws IOException, HttpError {
        List<Group> groups = new ArrayList<>();

        groups.add(
            Group.builder()
                .setId("1")
                .setName("Mountain Climbers @ Home")
                .setDescription("Something something follow your dreams")
                .build()
        );

        groups.add(
            Group.builder()
                .setId("2")
                .setName("Board Games Dublin")
                .setDescription("That is what we do, yes")
                .build()
        );

        groups.add(
            Group.builder()
                .setId("3")
                .setName("Unicode fans")
                .setDescription("私たちはユニコードファンです \uD83D\uDC4B")
                .build()
        );

        return groups;
    }
}
