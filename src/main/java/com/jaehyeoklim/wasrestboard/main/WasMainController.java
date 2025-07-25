package main.java.com.jaehyeoklim.wasrestboard.main;

import main.java.com.jaehyeoklim.wasrestboard.httpserver.HttpRequest;
import main.java.com.jaehyeoklim.wasrestboard.httpserver.HttpResponse;
import main.java.com.jaehyeoklim.wasrestboard.httpserver.servlet.annotation.Mapping;

import java.io.IOException;

public class WasMainController {

    @Mapping("/")
    public void home(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {

    }
}
